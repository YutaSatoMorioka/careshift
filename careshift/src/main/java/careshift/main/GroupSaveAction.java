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
 * グループの作成と名称変更。
 *
 * URL:
 * /careshift/main/GroupSave.action
 *
 * 未所属の場合:
 * グループを作成し、作成者を主介護者として登録する。
 *
 * 所属済みの場合:
 * 主介護者だけが名称を変更できる。
 */
public class GroupSaveAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        if (!"POST".equalsIgnoreCase(
                req.getMethod()
        )) {
            resp.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED
            );

            return;
        }

        User user =
                requireLogin(
                        req,
                        resp
                );

        if (user == null) {
            return;
        }

        String name =
                Validator.trim(
                        req.getParameter("groupName")
                );

        Map<String, String> errors =
                new LinkedHashMap<>();

        if (name.isEmpty()) {
            errors.put(
                    "groupName",
                    "グループ名を入力してください"
            );

        } else if (name.length() > 30) {
            errors.put(
                    "groupName",
                    "グループ名は30文字以内で入力してください"
            );
        }

        if (!errors.isEmpty()) {
            GroupAction.show(
                    req,
                    resp,
                    user,
                    errors,
                    name
            );

            return;
        }

        GroupDao dao =
                new GroupDao();

        Member me =
                dao.findMember(
                        user.getId()
                );

        try {
            if (me == null) {
                dao.createGroup(
                        user.getId(),
                        name
                );

                flash(
                        req,
                        "家族グループ「"
                        + name
                        + "」を作成しました。"
                        + "続けて家族を招待しましょう。"
                );

            } else if (me.isPrimary()) {
                boolean renamed =
                        dao.rename(
                                me.getGroupId(),
                                name
                        );

                if (!renamed) {
                    errors.put(
                            "groupName",
                            "グループ名を変更できませんでした。"
                    );

                    GroupAction.show(
                            req,
                            resp,
                            user,
                            errors,
                            name
                    );

                    return;
                }

                flash(
                        req,
                        "グループ名を変更しました。"
                );

            } else {
                flashError(
                        req,
                        "グループ名を変更できるのは主介護者のみです。"
                );
            }

        } catch (SQLException e) {

            if ("23505".equals(
                    e.getSQLState()
            )) {
                flashError(
                        req,
                        "すでに家族グループに所属しています。"
                );

            } else {
                saveFailed(
                        req,
                        resp,
                        user,
                        name,
                        e
                );

                return;
            }

        } catch (IllegalStateException e) {

            flashError(
                    req,
                    "すでに家族グループに所属しています。"
            );

        } catch (Exception e) {

            saveFailed(
                    req,
                    resp,
                    user,
                    name,
                    e
            );

            return;
        }

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/Group.action"
        );
    }

    /**
     * 保存失敗時、入力内容を保持して再表示する。
     */
    private void saveFailed(
            HttpServletRequest req,
            HttpServletResponse resp,
            User user,
            String name,
            Exception exception
    ) throws Exception {

        exception.printStackTrace();

        Map<String, String> errors =
                new LinkedHashMap<>();

        errors.put(
                "groupName",
                "保存に失敗しました。もう一度お試しください。"
        );

        GroupAction.show(
                req,
                resp,
                user,
                errors,
                name
        );
    }
}