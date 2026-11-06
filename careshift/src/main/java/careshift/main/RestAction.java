package careshift.main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import bean.Member;
import bean.User;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        if("POST".equalsIgnoreCase(req.getMethod())&&!requirePost(req,resp))return;
        Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);CareFeatureDao dao=new CareFeatureDao();
        if("POST".equalsIgnoreCase(req.getMethod())){
            LocalDateTime start,end;try{start=LocalDateTime.parse(value(req,"startsAt"));end=LocalDateTime.parse(value(req,"endsAt"));}catch(Exception e){fail(req,resp,"休息の開始・終了日時を入力してください。","Rest");return;}
            String note=value(req,"note");if(!me.isPrimary()){fail(req,resp,"休息リクエストを登録できるのは主介護者です。","Rest");return;}
            if(!end.isAfter(start)||note.length()>300){fail(req,resp,"終了は開始より後にし、内容は300文字以内にしてください。","Rest");return;}
            dao.createRest(me.getGroupId(),user.getId(),start,end,note);req.getSession().setAttribute("flash","休息リクエストを登録し、家族へ通知しました。");redirect(req,resp,"Rest");return;
        }
        LocalDate from=LocalDate.now().withDayOfMonth(1);req.setAttribute("rests",dao.rests(me.getGroupId()));req.setAttribute("report",dao.restReport(me.getGroupId(),from,from.plusMonths(1).minusDays(1)));req.getRequestDispatcher("/careshift/main/rest.jsp").forward(req,resp);
    }
}
