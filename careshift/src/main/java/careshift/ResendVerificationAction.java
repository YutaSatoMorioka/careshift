package careshift;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import bean.User;import dao.UserDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import tool.Action;import tool.AuthMail;import tool.Validator;

/** メール確認リンクの再送。登録有無を画面から推測できない同一メッセージにする。 */
public class ResendVerificationAction extends Action {
 private static final ConcurrentMap<String,Long> LAST=new ConcurrentHashMap<>();private static final long INTERVAL=60_000L;
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{
  if(!"POST".equalsIgnoreCase(req.getMethod())){resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);return;}
  String email=Validator.trim(req.getParameter("email")).toLowerCase(Locale.ROOT);long now=System.currentTimeMillis();java.util.concurrent.atomic.AtomicBoolean allowed=new java.util.concurrent.atomic.AtomicBoolean(false);LAST.compute(email,(key,last)->{if(last==null||now-last>=INTERVAL){allowed.set(true);return now;}return last;});
  try{if(allowed.get()&&Validator.isEmail(email)){User user=new UserDao().findByEmail(email);if(user!=null&&!user.isEmailVerified())AuthMail.sendVerification(req,user);}}catch(Exception e){req.getServletContext().log("確認メール再送に失敗しました");}
  req.setAttribute("email",email);req.getRequestDispatcher("/resend_verification_done.jsp").forward(req,resp);
 }
}
