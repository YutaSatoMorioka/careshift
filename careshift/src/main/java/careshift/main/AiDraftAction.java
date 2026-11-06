package careshift.main;

import bean.Member;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 開発用のローカル下書き支援。外部生成AIには接続せず、送信前の人による確認を必須にする。 */
public class AiDraftAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        Member me=requireMember(req,resp);if(me==null)return;
        String source=value(req,"consultation");if(source.length()>500){fail(req,resp,"相談文は500文字以内で入力してください。","AiDraft");return;}
        String safe=source.replaceAll("(?i)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}","[メールアドレス]").replaceAll("(?<!\\d)\\d{2,4}-?\\d{2,4}-?\\d{3,4}(?!\\d)","[電話番号]");
        String draft=source.isBlank()?"": "相談内容を確認し、家族にお願いしたいことを具体的に入力してください。\n\n相談メモ（個人情報は伏せ字）: "+safe;
        req.setAttribute("draft",draft);req.setAttribute("consultation",source);req.setAttribute("redacted",!safe.equals(source));
        req.getRequestDispatcher("/careshift/main/ai_draft.jsp").forward(req,resp);
    }
}
