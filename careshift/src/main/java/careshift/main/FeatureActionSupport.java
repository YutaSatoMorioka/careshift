package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/** Care Shift機能画面共通のログイン・家族グループ確認。 */
abstract class FeatureActionSupport extends Action {
    protected boolean requirePost(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        if("POST".equalsIgnoreCase(req.getMethod()))return true;
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);return false;
    }
    protected Member requireMember(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        User user=loginUser(req);
        if(user==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return null;}
        Member member=new GroupDao().findMember(user.getId());
        if(member==null){resp.sendRedirect(req.getContextPath()+"/careshift/main/Group.action");return null;}
        req.setAttribute("currentMember",member);
        req.setAttribute("featureUser",user);
        return member;
    }
    protected long id(String value){try{return Long.parseLong(value);}catch(Exception e){return -1;}}
    protected String value(HttpServletRequest req,String name){String v=req.getParameter(name);return v==null?"":v.trim();}
    protected void redirect(HttpServletRequest req,HttpServletResponse resp,String path)throws Exception{resp.sendRedirect(req.getContextPath()+"/careshift/main/"+path+".action");}
    protected void fail(HttpServletRequest req,HttpServletResponse resp,String message,String path)throws Exception{req.getSession().setAttribute("flashError",message);redirect(req,resp,path);}
}
