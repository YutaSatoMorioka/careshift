package careshift.main;

import bean.Member;
import bean.User;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TaskStatusAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        if(!requirePost(req,resp))return;
        Member me=requireMember(req,resp);if(me==null)return;bean.User user=loginUser(req);String status=value(req,"status");long taskId=id(req.getParameter("id"));
        if(!java.util.List.of("TODO","DOING","DONE").contains(status)||taskId<1){fail(req,resp,"進捗の指定が正しくありません。","TaskList");return;}
        CareFeatureDao dao=new CareFeatureDao();var task=dao.task(me.getGroupId(),taskId);if(task==null){fail(req,resp,"タスクが見つかりません。","TaskList");return;}
        Object assignee=task.get("assignee_user_id");if(!me.isPrimary()&&((Number)task.get("created_by")).longValue()!=user.getId()&&(assignee==null||((Number)assignee).longValue()!=user.getId())){fail(req,resp,"このタスクの進捗を変更する権限がありません。","TaskList");return;}
        if(!dao.updateTaskStatus(me.getGroupId(),taskId,status)){fail(req,resp,"タスクが見つかりません。","TaskList");return;}
        req.getSession().setAttribute("flash","進捗を更新しました。");redirect(req,resp,"TaskList");
    }
}
