package careshift.main;

import bean.Member;
import bean.User;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TaskDeleteAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        if(!requirePost(req,resp))return;
        Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);long taskId=id(req.getParameter("id"));CareFeatureDao dao=new CareFeatureDao();
        if(taskId<1||(!me.isPrimary()&&!dao.isTaskCreator(me.getGroupId(),taskId,user.getId()))){fail(req,resp,"削除できるタスクではありません。","TaskList");return;}
        if(!dao.deleteTask(me.getGroupId(),taskId)){fail(req,resp,"タスクが見つかりません。","TaskList");return;}
        req.getSession().setAttribute("flash","タスクを削除しました。");redirect(req,resp,"TaskList");
    }
}
