package careshift.main;

import java.time.LocalDateTime;
import bean.Member;
import bean.User;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TaskSaveAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        if(!requirePost(req,resp))return;
        Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);
        String title=value(req,"title"), details=value(req,"details"), due=value(req,"dueAt"), frequency=value(req,"frequency");
        int minutes;long assigneeId=id(req.getParameter("assigneeUserId"));Long assignee=assigneeId>0?assigneeId:null;long taskId=id(req.getParameter("id"));
        if(title.isEmpty()||title.length()>100||details.length()>1000){fail(req,resp,"内容を入力してください（内容100文字以内、詳細1000文字以内）。","TaskForm");return;}
        try{minutes=Integer.parseInt(value(req,"durationMinutes"));}catch(Exception e){fail(req,resp,"所要時間を数字で入力してください。","TaskForm");return;}
        if(minutes<5||minutes>1440){fail(req,resp,"所要時間は5分以上24時間以内で入力してください。","TaskForm");return;}
        LocalDateTime dueAt;try{dueAt=LocalDateTime.parse(due);}catch(Exception e){fail(req,resp,"期限を入力してください。","TaskForm");return;}
        if(frequency.length()>30){fail(req,resp,"頻度は30文字以内で入力してください。","TaskForm");return;}
        CareFeatureDao dao=new CareFeatureDao();
        if(taskId>0&&!me.isPrimary()){
            var existing=dao.task(me.getGroupId(),taskId);
            if(existing==null||(((Number)existing.get("created_by")).longValue()!=user.getId()&&(existing.get("assignee_user_id")==null||((Number)existing.get("assignee_user_id")).longValue()!=user.getId()))){fail(req,resp,"このタスクを編集する権限がありません。","TaskList");return;}
        }
        if(dueAt.isBefore(LocalDateTime.now())&&!"true".equals(req.getParameter("confirmPast"))){req.setAttribute("confirmPastWarning",Boolean.TRUE);req.setAttribute("members",dao.members(me.getGroupId()));req.getRequestDispatcher("/careshift/main/task_form.jsp").forward(req,resp);return;}
        dao.saveTask(me.getGroupId(),user.getId(),taskId>0?taskId:null,title,details,dueAt,minutes,frequency,assignee);
        if(assignee!=null&&assignee!=user.getId())dao.notifyMember(assignee,"タスク","担当タスクが登録・更新されました","/careshift/main/TaskList.action");
        boolean medical=java.util.regex.Pattern.compile("病名|薬名|服薬|診断",java.util.regex.Pattern.CASE_INSENSITIVE).matcher(title+" "+details).find();
        req.getSession().setAttribute("flash",medical?"タスクを保存しました。病名・薬名などの医療情報は入力しないでください。":"タスクを保存しました。");redirect(req,resp,"TaskList");
    }
}
