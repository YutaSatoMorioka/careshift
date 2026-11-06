package careshift.admin;
import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import tool.Action;import tool.BackupService;
public class AdminRestoreAction extends Action {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{
  if(!"POST".equalsIgnoreCase(req.getMethod())){resp.sendError(405);return;}
  try{BackupService.restore(req.getParameter("file"),req.getParameter("adminPassword"));req.getSession().setAttribute("flash","復元処理が完了しました。画面を再読み込みしてください。");}
  catch(Exception e){req.getSession().setAttribute("flashError","復元できませんでした。バックアップを確認し、必要ならTomcatを停止してから復元してください。");}
  resp.sendRedirect(req.getContextPath()+"/careshift/admin/AdminDashboard.action");
 }
}
