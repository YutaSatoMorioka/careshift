package careshift.main;

import bean.Member;
import bean.User;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestRespondAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        if(!requirePost(req,resp))return;
        Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);String choice=value(req,"choice"),message=value(req,"message");long requestId=id(req.getParameter("requestId"));
        if(requestId<1||!java.util.List.of("担当する","今回は難しい","別のタスクならできる").contains(choice)||message.length()>500){fail(req,resp,"回答内容を確認してください。","Request");return;}
        if(!new CareFeatureDao().respond(me.getGroupId(),requestId,user.getId(),choice,message)){fail(req,resp,"依頼が見つからないか、回答できません。","Request");return;}
        req.getSession().setAttribute("flash","依頼に回答しました。");redirect(req,resp,"Request");
    }
}
