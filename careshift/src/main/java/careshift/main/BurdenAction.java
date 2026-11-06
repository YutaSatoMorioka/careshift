package careshift.main;

import java.util.List;
import java.util.Map;
import bean.Member;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BurdenAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        Member me=requireMember(req,resp);if(me==null)return;CareFeatureDao dao=new CareFeatureDao();List<Map<String,Object>> workload=dao.workload(me.getGroupId());
        long total=workload.stream().mapToLong(r->((Number)r.get("minutes")).longValue()).sum();
        if(me.isPrimary()&&total>0&&!workload.isEmpty()){
            Map<String,Object> top=workload.get(0);long mins=((Number)top.get("minutes")).longValue();long count=((Number)top.get("task_count")).longValue();
            if(count>=3&&mins*100>=total*70)dao.notifyOverloadOnce(me.getGroupId(),me.getUserId(),"タスクの負担が一人に偏っています。分担を確認してください。");
        }
        req.setAttribute("workload",workload);req.setAttribute("totalMinutes",total);req.getRequestDispatcher("/careshift/main/burden.jsp").forward(req,resp);
    }
}
