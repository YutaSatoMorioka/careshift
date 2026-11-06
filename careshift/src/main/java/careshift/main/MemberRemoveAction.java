package careshift.main;

import bean.Member;import dao.CareFeatureDao;import dao.GroupDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import tool.Action;

/** 主介護者による家族メンバー削除。本人・主介護者は削除できない。 */
public class MemberRemoveAction extends Action {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{
  if(!"POST".equalsIgnoreCase(req.getMethod())){resp.sendError(405);return;}
  bean.User user=loginUser(req);if(user==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
  GroupDao groups=new GroupDao();Member me=groups.findMember(user.getId());long targetId;try{targetId=Long.parseLong(req.getParameter("userId"));}catch(Exception e){targetId=-1;}
  if(me==null||!me.isPrimary()||targetId<1||targetId==user.getId()){req.getSession().setAttribute("flashError","そのメンバーは削除できません。");resp.sendRedirect(req.getContextPath()+"/careshift/main/MemberList.action");return;}
  Member target=groups.findMember(targetId);if(target==null||target.getGroupId()!=me.getGroupId()||target.isPrimary()||!new CareFeatureDao().removeMember(me.getGroupId(),targetId)){req.getSession().setAttribute("flashError","対象が見つからないか、削除できません。");}else{req.getSession().setAttribute("flash","メンバーをグループから削除しました。担当タスクは未割当になりました。");}
  resp.sendRedirect(req.getContextPath()+"/careshift/main/MemberList.action");
 }
}
