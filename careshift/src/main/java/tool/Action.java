package tool;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * すべての「〇〇Action」の親クラス。
 * 画面ごとに Action を継承したクラスを作り、execute に処理を書く。
 */
public abstract class Action {
    public abstract void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
