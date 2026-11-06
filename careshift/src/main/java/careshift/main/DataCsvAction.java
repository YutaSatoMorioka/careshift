package careshift.main;

import java.nio.charset.StandardCharsets;import java.util.List;import java.util.Map;import bean.Member;import bean.User;import dao.CareFeatureDao;import dao.GroupDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;

/** ログイン中ユーザーの家族グループとタスクをCSVで出力する。 */
public class DataCsvAction extends FeatureActionSupport {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);GroupDao groups=new GroupDao();var group=groups.findGroup(me.getGroupId());CareFeatureDao dao=new CareFeatureDao();List<Map<String,Object>> members=dao.members(me.getGroupId()),tasks=dao.tasks(me.getGroupId(),null,null);resp.setCharacterEncoding("UTF-8");resp.setContentType("text/csv; charset=UTF-8");resp.setHeader("Content-Disposition","attachment; filename=careshift_export.csv");var out=resp.getOutputStream();out.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});write(out,"区分,グループ名,名前/タスク,メール/期限,役割/担当,詳細\r\n");write(out,"グループ,"+csv(group.getName())+",,,,\r\n");for(var m:members)write(out,"メンバー,"+csv(group.getName())+","+csv(m.get("display_name"))+",,"+csv(m.get("role"))+","+csv(m.get("relationship"))+"\r\n");for(var t:tasks)write(out,"タスク,"+csv(group.getName())+","+csv(t.get("title"))+","+csv(t.get("due_at"))+","+csv(t.get("assignee_name"))+","+csv(t.get("status"))+"\r\n");}
 private void write(java.io.OutputStream out,String s)throws Exception{out.write(s.getBytes(StandardCharsets.UTF_8));}
 private String csv(Object v){String s=v==null?"":String.valueOf(v);if(s.startsWith("=")||s.startsWith("+")||s.startsWith("-")||s.startsWith("@"))s="'"+s;return "\""+s.replace("\"","\"\"")+"\"";}
}
