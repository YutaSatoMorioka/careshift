package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Care Shift のタスク・依頼・休息・通知・プラン機能を扱うDAO。全検索/更新をgroup_idで限定する。 */
public class CareFeatureDao extends Dao {
    private Connection openFeatureConnection() throws Exception { return getConnection(); }

    public List<Map<String,Object>> tasks(long groupId, LocalDateTime from, LocalDateTime to) throws Exception {
        String sql = "SELECT t.id,t.title,t.details,t.due_at,t.duration_minutes,t.frequency,t.assignee_user_id,t.status,t.created_by,u.display_name assignee_name FROM care_tasks t LEFT JOIN users u ON u.id=t.assignee_user_id WHERE t.group_id=?" + (from == null ? "" : " AND t.due_at>=?") + (to == null ? "" : " AND t.due_at<?") + " ORDER BY t.due_at,t.id";
        try (Connection c=openFeatureConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            int i=1; p.setLong(i++,groupId); if(from!=null)p.setTimestamp(i++,Timestamp.valueOf(from)); if(to!=null)p.setTimestamp(i,Timestamp.valueOf(to));
            try(ResultSet r=p.executeQuery()){return rows(r);}
        }
    }
    public Map<String,Object> task(long groupId,long id) throws Exception {
        for(Map<String,Object> row:tasks(groupId,null,null)) if(((Number)row.get("id")).longValue()==id)return row;
        return null;
    }
    public long saveTask(long groupId,long userId,Long taskId,String title,String details,LocalDateTime dueAt,int minutes,String frequency,Long assignee) throws Exception {
        try(Connection c=openFeatureConnection()) {
            if(assignee!=null && !isMember(c,groupId,assignee)) throw new IllegalArgumentException("担当者は同じ家族グループのメンバーから選択してください。");
            if(taskId==null) {
                String sql="INSERT INTO care_tasks(group_id,title,details,due_at,duration_minutes,frequency,assignee_user_id,created_by) VALUES(?,?,?,?,?,?,?,?)";
                try(PreparedStatement p=c.prepareStatement(sql,new String[]{"id"})) { bindTask(p,groupId,title,details,dueAt,minutes,frequency,assignee);p.setLong(8,userId);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(!k.next())throw new IllegalStateException("タスクIDを取得できません");return k.getLong(1);} }
            }
            String sql="UPDATE care_tasks SET title=?,details=?,due_at=?,duration_minutes=?,frequency=?,assignee_user_id=?,updated_at=CURRENT_TIMESTAMP WHERE id=? AND group_id=?";
            try(PreparedStatement p=c.prepareStatement(sql)){p.setString(1,title);p.setString(2,details);p.setTimestamp(3,Timestamp.valueOf(dueAt));p.setInt(4,minutes);p.setString(5,frequency);if(assignee==null)p.setNull(6,java.sql.Types.BIGINT);else p.setLong(6,assignee);p.setLong(7,taskId);p.setLong(8,groupId);if(p.executeUpdate()!=1)throw new IllegalArgumentException("対象のタスクがありません");return taskId;}
        }
    }
    private void bindTask(PreparedStatement p,long groupId,String title,String details,LocalDateTime dueAt,int minutes,String frequency,Long assignee)throws Exception {p.setLong(1,groupId);p.setString(2,title);p.setString(3,details);p.setTimestamp(4,Timestamp.valueOf(dueAt));p.setInt(5,minutes);p.setString(6,frequency);if(assignee==null)p.setNull(7,java.sql.Types.BIGINT);else p.setLong(7,assignee);}
    public boolean isTaskCreator(long groupId,long taskId,long userId) throws Exception {try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("SELECT 1 FROM care_tasks WHERE id=? AND group_id=? AND created_by=?")){p.setLong(1,taskId);p.setLong(2,groupId);p.setLong(3,userId);try(ResultSet r=p.executeQuery()){return r.next();}}}
    public boolean removeMember(long groupId,long userId)throws Exception{try(Connection c=openFeatureConnection()){try(PreparedStatement q=c.prepareStatement("SELECT role FROM group_members WHERE group_id=? AND user_id=?")){q.setLong(1,groupId);q.setLong(2,userId);try(ResultSet r=q.executeQuery()){if(!r.next()||"PRIMARY".equals(r.getString(1)))return false;}}try(PreparedStatement p=c.prepareStatement("UPDATE care_tasks SET assignee_user_id=NULL,updated_at=CURRENT_TIMESTAMP WHERE group_id=? AND assignee_user_id=?")){p.setLong(1,groupId);p.setLong(2,userId);p.executeUpdate();}try(PreparedStatement p=c.prepareStatement("DELETE FROM group_members WHERE group_id=? AND user_id=?")){p.setLong(1,groupId);p.setLong(2,userId);return p.executeUpdate()==1;}}}
    public boolean deleteTask(long groupId,long taskId) throws Exception {try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("DELETE FROM care_tasks WHERE id=? AND group_id=?")){p.setLong(1,taskId);p.setLong(2,groupId);return p.executeUpdate()==1;}}
    public boolean updateTaskStatus(long groupId,long taskId,String status) throws Exception {try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("UPDATE care_tasks SET status=?,updated_at=CURRENT_TIMESTAMP WHERE id=? AND group_id=?")){p.setString(1,status);p.setLong(2,taskId);p.setLong(3,groupId);return p.executeUpdate()==1;}}
    public List<Map<String,Object>> members(long groupId) throws Exception {String sql="SELECT m.user_id,u.display_name,m.role FROM group_members m JOIN users u ON u.id=m.user_id WHERE m.group_id=? ORDER BY m.joined_at";try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,groupId);try(ResultSet r=p.executeQuery()){return rows(r);}}}
    private boolean isMember(Connection c,long groupId,long userId)throws Exception{try(PreparedStatement p=c.prepareStatement("SELECT 1 FROM group_members WHERE group_id=? AND user_id=?")){p.setLong(1,groupId);p.setLong(2,userId);try(ResultSet r=p.executeQuery()){return r.next();}}}

    public long createRequest(long groupId,long requesterId,String type,String title,String body,LocalDateTime requestedAt,Integer minutes)throws Exception {
        try(Connection c=openFeatureConnection()) {
            try(PreparedStatement p=c.prepareStatement("INSERT INTO care_requests(group_id,requester_id,type,title,body,requested_at,duration_minutes) VALUES(?,?,?,?,?,?,?)",new String[]{"id"})) {
                p.setLong(1,groupId);p.setLong(2,requesterId);p.setString(3,type);p.setString(4,title);p.setString(5,body);if(requestedAt==null)p.setNull(6,java.sql.Types.TIMESTAMP);else p.setTimestamp(6,Timestamp.valueOf(requestedAt));if(minutes==null)p.setNull(7,java.sql.Types.INTEGER);else p.setInt(7,minutes);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){k.next();long id=k.getLong(1);notifyGroup(c,groupId,requesterId,"依頼",title+" の依頼が届きました","/careshift/main/Request.action");return id;}
            }
        }
    }
    public List<Map<String,Object>> requests(long groupId)throws Exception{String sql="SELECT r.id,r.requester_id,u.display_name requester_name,r.type,r.title,r.body,r.requested_at,r.duration_minutes,r.status,r.created_at FROM care_requests r JOIN users u ON u.id=r.requester_id WHERE r.group_id=? ORDER BY r.created_at DESC";try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,groupId);try(ResultSet r=p.executeQuery()){return rows(r);}}}
    public boolean respond(long groupId,long requestId,long userId,String choice,String message)throws Exception {
        try(Connection c=openFeatureConnection()) {
            long requester;String title;
            try(PreparedStatement q=c.prepareStatement("SELECT requester_id,title FROM care_requests WHERE id=? AND group_id=?")){q.setLong(1,requestId);q.setLong(2,groupId);try(ResultSet r=q.executeQuery()){if(!r.next())return false;requester=r.getLong(1);title=r.getString(2);}}
            if(!isMember(c,groupId,userId))return false;
            int updated;try(PreparedStatement p=c.prepareStatement("UPDATE care_request_responses SET choice=?,message=?,created_at=CURRENT_TIMESTAMP WHERE request_id=? AND responder_id=?")){p.setString(1,choice);p.setString(2,message);p.setLong(3,requestId);p.setLong(4,userId);updated=p.executeUpdate();}
            if(updated==0)try(PreparedStatement p=c.prepareStatement("INSERT INTO care_request_responses(request_id,responder_id,choice,message) VALUES(?,?,?,?)")){p.setLong(1,requestId);p.setLong(2,userId);p.setString(3,choice);p.setString(4,message);p.executeUpdate();}
            try(PreparedStatement p=c.prepareStatement("UPDATE care_requests SET status=? WHERE id=? AND group_id=?")){p.setString(1,"担当する".equals(choice)?"ACCEPTED":"OPEN");p.setLong(2,requestId);p.setLong(3,groupId);p.executeUpdate();}
            notifyUser(c,requester,"回答",title+" に回答がありました","/careshift/main/Request.action");return true;
        }
    }
    public List<Map<String,Object>> responses(long requestId)throws Exception{String sql="SELECT r.responder_id,u.display_name,r.choice,r.message,r.created_at FROM care_request_responses r JOIN users u ON u.id=r.responder_id WHERE r.request_id=? ORDER BY r.created_at";try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,requestId);try(ResultSet r=p.executeQuery()){return rows(r);}}}

    public void createRest(long groupId,long userId,LocalDateTime start,LocalDateTime end,String note)throws Exception {try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("INSERT INTO rest_records(group_id,user_id,starts_at,ends_at,note) VALUES(?,?,?,?,?)")){p.setLong(1,groupId);p.setLong(2,userId);p.setTimestamp(3,Timestamp.valueOf(start));p.setTimestamp(4,Timestamp.valueOf(end));p.setString(5,note);p.executeUpdate();notifyGroup(c,groupId,userId,"休息", "休息リクエストが登録されました","/careshift/main/Rest.action");}}
    public List<Map<String,Object>> rests(long groupId)throws Exception{String sql="SELECT r.id,r.user_id,u.display_name,r.starts_at,r.ends_at,r.note,r.status FROM rest_records r JOIN users u ON u.id=r.user_id WHERE r.group_id=? ORDER BY r.starts_at DESC";try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,groupId);try(ResultSet r=p.executeQuery()){return rows(r);}}}
    public List<Map<String,Object>> restReport(long groupId,LocalDate from,LocalDate to)throws Exception{String sql="SELECT u.display_name,r.starts_at,r.ends_at FROM rest_records r JOIN users u ON u.id=r.user_id WHERE r.group_id=? AND r.starts_at>=? AND r.starts_at<? ORDER BY u.display_name";Map<String,Map<String,Object>> totals=new LinkedHashMap<>();try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,groupId);p.setTimestamp(2,Timestamp.valueOf(from.atStartOfDay()));p.setTimestamp(3,Timestamp.valueOf(to.plusDays(1).atStartOfDay()));try(ResultSet r=p.executeQuery()){while(r.next()){String name=r.getString(1);Map<String,Object> m=totals.computeIfAbsent(name,k->{Map<String,Object>x=new LinkedHashMap<>();x.put("display_name",name);x.put("rest_count",0L);x.put("rest_minutes",0L);return x;});long count=(Long)m.get("rest_count"),minutes=(Long)m.get("rest_minutes");m.put("rest_count",count+1);m.put("rest_minutes",minutes+java.time.Duration.between(r.getTimestamp(2).toLocalDateTime(),r.getTimestamp(3).toLocalDateTime()).toMinutes());}}}return new ArrayList<>(totals.values());}
    public List<Map<String,Object>> workload(long groupId)throws Exception{String sql="SELECT m.user_id,u.display_name,COALESCE(SUM(CASE WHEN t.status<>'DONE' THEN 1 ELSE 0 END),0) task_count,COALESCE(SUM(CASE WHEN t.status<>'DONE' THEN t.duration_minutes ELSE 0 END),0) minutes FROM group_members m JOIN users u ON u.id=m.user_id LEFT JOIN care_tasks t ON t.group_id=m.group_id AND t.assignee_user_id=m.user_id WHERE m.group_id=? GROUP BY m.user_id,u.display_name ORDER BY minutes DESC";try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,groupId);try(ResultSet r=p.executeQuery()){return rows(r);}}}

    public void notifyGroup(long groupId,long exceptUser,String type,String message,String link)throws Exception{try(Connection c=openFeatureConnection()){notifyGroup(c,groupId,exceptUser,type,message,link);}}
    private void notifyGroup(Connection c,long groupId,long exceptUser,String type,String message,String link)throws Exception{try(PreparedStatement p=c.prepareStatement("INSERT INTO notifications(user_id,type,message,link_path) SELECT user_id,?,?,? FROM group_members WHERE group_id=? AND user_id<>?")){p.setString(1,type);p.setString(2,message);p.setString(3,link);p.setLong(4,groupId);p.setLong(5,exceptUser);p.executeUpdate();}}
    private void notifyUser(Connection c,long userId,String type,String message,String link)throws Exception{try(PreparedStatement p=c.prepareStatement("INSERT INTO notifications(user_id,type,message,link_path) VALUES(?,?,?,?)")){p.setLong(1,userId);p.setString(2,type);p.setString(3,message);p.setString(4,link);p.executeUpdate();}}
    public void notifyMember(long userId,String type,String message,String link)throws Exception{try(Connection c=openFeatureConnection()){notifyUser(c,userId,type,message,link);}}
    public boolean notifyOverloadOnce(long groupId,long userId,String message)throws Exception{try(Connection c=openFeatureConnection()){try(PreparedStatement p=c.prepareStatement("INSERT INTO care_alerts(group_id,user_id,alert_date) VALUES(?,?,?)")){p.setLong(1,groupId);p.setLong(2,userId);p.setDate(3,java.sql.Date.valueOf(LocalDate.now()));p.executeUpdate();}catch(java.sql.SQLException e){if("23505".equals(e.getSQLState()))return false;throw e;}notifyUser(c,userId,"負担",message,"/careshift/main/Burden.action");return true;}}
    public List<Map<String,Object>> notifications(long userId)throws Exception{try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("SELECT id,type,message,link_path,is_read,created_at FROM notifications WHERE user_id=? ORDER BY created_at DESC")){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){return rows(r);}}}
    public void markNotificationsRead(long userId)throws Exception{try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("UPDATE notifications SET is_read=TRUE WHERE user_id=?")){p.setLong(1,userId);p.executeUpdate();}}
    public void createDueReminders()throws Exception{try(Connection c=openFeatureConnection()){String sql="SELECT t.id,t.assignee_user_id,t.title FROM care_tasks t LEFT JOIN task_reminders r ON r.task_id=t.id AND r.reminder_date=? WHERE t.assignee_user_id IS NOT NULL AND t.status<>'DONE' AND CAST(t.due_at AS DATE)=? AND r.task_id IS NULL";LocalDate tomorrow=LocalDate.now().plusDays(1);List<long[]> todo=new ArrayList<>();List<String> titles=new ArrayList<>();try(PreparedStatement p=c.prepareStatement(sql)){p.setDate(1,java.sql.Date.valueOf(LocalDate.now()));p.setDate(2,java.sql.Date.valueOf(tomorrow));try(ResultSet r=p.executeQuery()){while(r.next()){todo.add(new long[]{r.getLong(1),r.getLong(2)});titles.add(r.getString(3));}}}for(int i=0;i<todo.size();i++){long[] x=todo.get(i);try(PreparedStatement p=c.prepareStatement("INSERT INTO task_reminders(task_id,reminder_date) VALUES(?,?)")){p.setLong(1,x[0]);p.setDate(2,java.sql.Date.valueOf(LocalDate.now()));p.executeUpdate();}notifyUser(c,x[1],"期限リマインド","明日が期限です: "+titles.get(i),"/careshift/main/TaskList.action");}}}

    public Map<String,Object> plan(long groupId)throws Exception{try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("SELECT plan_name,starts_at,ends_at FROM group_plans WHERE group_id=?")){p.setLong(1,groupId);try(ResultSet r=p.executeQuery()){if(r.next())return row(r);}}try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("INSERT INTO group_plans(group_id,plan_name) VALUES(?,'無料')")){p.setLong(1,groupId);p.executeUpdate();}return plan(groupId);}
    public List<Map<String,Object>> recentLogs(int limit)throws Exception{try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("SELECT level,screen,message,created_at FROM operation_logs ORDER BY created_at DESC")){try(ResultSet r=p.executeQuery()){List<Map<String,Object>> out=new ArrayList<>();while(r.next()&&out.size()<limit)out.add(row(r));return out;}}}
    public void log(String level,String screen,String message)throws Exception{try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("INSERT INTO operation_logs(level,screen,message) VALUES(?,?,?)")){p.setString(1,level);p.setString(2,screen);p.setString(3,message==null?"処理エラー":message.substring(0,Math.min(500,message.length())));p.executeUpdate();}}
    public void purgeOldLogs()throws Exception{try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("DELETE FROM operation_logs WHERE created_at<?")){p.setTimestamp(1,Timestamp.valueOf(LocalDateTime.now().minusDays(90)));p.executeUpdate();}}
    public Map<String,Boolean> checklist(long userId)throws Exception{Map<String,Boolean> out=new LinkedHashMap<>();try(Connection c=openFeatureConnection();PreparedStatement p=c.prepareStatement("SELECT item_key,checked FROM care_check_items WHERE user_id=?")){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())out.put(r.getString(1),r.getBoolean(2));}}return out;}
    public void saveChecklistItem(long userId,String key,boolean checked)throws Exception{try(Connection c=openFeatureConnection()){int n;try(PreparedStatement p=c.prepareStatement("UPDATE care_check_items SET checked=?,updated_at=CURRENT_TIMESTAMP WHERE user_id=? AND item_key=?")){p.setBoolean(1,checked);p.setLong(2,userId);p.setString(3,key);n=p.executeUpdate();}if(n==0)try(PreparedStatement p=c.prepareStatement("INSERT INTO care_check_items(user_id,item_key,checked) VALUES(?,?,?)")){p.setLong(1,userId);p.setString(2,key);p.setBoolean(3,checked);p.executeUpdate();}}}
    private List<Map<String,Object>> rows(ResultSet r)throws Exception{List<Map<String,Object>> list=new ArrayList<>();int n=r.getMetaData().getColumnCount();while(r.next()){Map<String,Object> m=new LinkedHashMap<>();for(int i=1;i<=n;i++)m.put(r.getMetaData().getColumnLabel(i),r.getObject(i));list.add(m);}return list;}
    private Map<String,Object> row(ResultSet r)throws Exception{Map<String,Object> m=new LinkedHashMap<>();int n=r.getMetaData().getColumnCount();for(int i=1;i<=n;i++)m.put(r.getMetaData().getColumnLabel(i),r.getObject(i));return m;}
}
