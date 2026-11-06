package careshift.main;

import java.time.LocalDateTime;
import bean.Member;
import bean.User;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);CareFeatureDao dao=new CareFeatureDao();
        if("POST".equalsIgnoreCase(req.getMethod())){
            String title=value(req,"title"),body=value(req,"body");String type=value(req,"type");
            if(!java.util.List.of("TASK","REST").contains(type)||title.isEmpty()||title.length()>100||body.length()>1000){fail(req,resp,"依頼の種類とタイトル（100文字以内）を入力してください。","Request");return;}
            LocalDateTime at=null;Integer mins=null;
            if(!value(req,"requestedAt").isEmpty())try{at=LocalDateTime.parse(value(req,"requestedAt"));}catch(Exception e){fail(req,resp,"希望日時の形式を確認してください。","Request");return;}
            if(!value(req,"durationMinutes").isEmpty())try{mins=Integer.valueOf(value(req,"durationMinutes"));}catch(Exception e){fail(req,resp,"所要時間を数字で入力してください。","Request");return;}
            dao.createRequest(me.getGroupId(),user.getId(),type,title,body,at,mins);req.getSession().setAttribute("flash","家族へ依頼を登録しました。");redirect(req,resp,"Request");return;
        }
        var requests=dao.requests(me.getGroupId());for(var row:requests){long requestId=((Number)row.get("id")).longValue();row.put("responses",dao.responses(requestId));}req.setAttribute("requests",requests);req.setAttribute("members",dao.members(me.getGroupId()));
        req.getRequestDispatcher("/careshift/main/request.jsp").forward(req,resp);
    }
}
