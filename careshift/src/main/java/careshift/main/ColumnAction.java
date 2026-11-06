package careshift.main;

import java.util.List;import java.util.Map;
import bean.Member;import bean.User;import dao.CareFeatureDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;

/** 固定コラムと利用者別チェックリスト。医療助言は掲載せず、一般的な家族分担の案内に限定。 */
public class ColumnAction extends FeatureActionSupport {
 private static final List<Map<String,String>> ARTICLES=List.of(
  Map.of("category","家族の分担","title","最初に家族で決めること","body","担当・期限・所要時間を決め、無理のない分担から始めましょう。"),
  Map.of("category","遠距離介護","title","現地に行かなくてもできる支援","body","電話、予約、必要品の購入、情報収集など、離れていてもできる支援があります。"),
  Map.of("category","休息","title","休息を予定に入れる","body","休みたい時間を具体的に共有し、家族と代わりの担当を相談しましょう。"));
 private static final List<String> KEYS=List.of("family_talk","task_share","rest_schedule");
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{
  Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);CareFeatureDao dao=new CareFeatureDao();
  if("POST".equalsIgnoreCase(req.getMethod())){if(!"1".equals(req.getParameter("saveChecklist"))){resp.sendError(400);return;}for(String key:KEYS)dao.saveChecklistItem(user.getId(),key,req.getParameter(key)!=null);req.getSession().setAttribute("flash","チェックリストを保存しました。");redirect(req,resp,"Column");return;}
  req.setAttribute("articles",ARTICLES);req.setAttribute("checklist",dao.checklist(user.getId()));req.getRequestDispatcher("/careshift/main/columns.jsp").forward(req,resp);
 }
}
