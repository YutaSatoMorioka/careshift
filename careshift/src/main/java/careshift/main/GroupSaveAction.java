package careshift.main;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S05 グループの作成・名称変更（group.jsp の save ボタンの送信先）。URL: /careshift/main/GroupSave.action
 * 未所属なら新規作成（作成者を主介護者として登録）、所属済みなら名称変更（主介護者のみ）。
 */
public class GroupSaveAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String name = Validator.trim(req.getParameter("groupName"));

        Map<String, String> errors = new LinkedHashMap<>();
        if (name.isEmpty()) {
            errors.put("groupName", "グループ名を入力してください");
        } else if (name.length() > 30) {
            errors.put("groupName", "グループ名は30文字以内で入力してください");
        }
        if (!errors.isEmpty()) {
            GroupAction.show(req, resp, user, errors, name); // 入力を保持して再表示
            return;
        }

        GroupDao dao = new GroupDao();
        Member me = dao.findMember(user.getId());
        try {
            if (me == null) {
                dao.createGroup(user.getId(), name);
                flash(req, "家族グループ「" + name + "」を作成しました。続けて家族を招待しましょう。");
            } else if (me.isPrimary()) {
                dao.rename(me.getGroupId(), name);
                flash(req, "グループ名を変更しました。");
            } else {
                flashError(req, "グループ名を変更できるのは主介護者のみです。");
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // 二重送信などで既に所属していた場合
                flashError(req, "すでに家族グループに所属しています。");
            } else {
                saveFailed(req, resp, user, name, e);
                return;
            }
        } catch (Exception e) {
            saveFailed(req, resp, user, name, e);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/careshift/main/Group.action");
    }

    /** 保存失敗時は、入力を保持したまま再試行できるようにする（設計書の指定）。 */
    private void saveFailed(HttpServletRequest req, HttpServletResponse resp, User user, String name, Exception e)
            throws Exception {
        e.printStackTrace();
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("groupName", "保存に失敗しました。もう一度お試しください。");
        GroupAction.show(req, resp, user, errors, name);
    }
}
