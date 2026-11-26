package careshift.main;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.FamilyGroup;
import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.SettingsDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AppLog;
import tool.Validator;

/**
 * S20 設定の保存。
 *
 * URL:
 * /careshift/main/SettingsSave.action
 *
 * POSTだけを受け付ける。
 *
 * 保存対象:
 *
 * ・メール通知
 * ・期限リマインド
 * ・グループ名（主介護者のみ）
 *
 * アプリ内通知は常にオンで、変更を受け付けない。
 */
public class SettingsSaveAction extends Action {

    private static final int MAX_GROUP_NAME_LENGTH =
            30;

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

        Member me =
                requireMember(
                        req,
                        resp,
                        user
                );

        if (me == null) {
            return;
        }

        String back =
                req.getContextPath()
                + "/careshift/main/Settings.action";

        boolean emailNotice =
                req.getParameter(
                        "emailNotice"
                ) != null;

        boolean reminder =
                req.getParameter(
                        "reminder"
                ) != null;

        /*
         * appNoticeは常にオン。
         * リクエスト値が送信されても使用しない。
         */

        String groupName =
                req.getParameter("groupName") == null
                        ? null
                        : Validator.trim(
                                req.getParameter(
                                        "groupName"
                                )
                        );

        GroupDao groupDao =
                new GroupDao();

        FamilyGroup currentGroup =
                groupDao.findGroup(
                        me.getGroupId()
                );

        if (currentGroup == null) {
            flashError(
                    req,
                    "家族グループが見つかりません。"
            );

            resp.sendRedirect(back);

            return;
        }

        boolean rename =
                false;

        if (groupName != null) {
            if (!me.isPrimary()) {
                flashError(
                        req,
                        "グループ名を変更できるのは主介護者のみです。"
                        + "設定は変更していません。"
                );

                resp.sendRedirect(back);

                return;
            }

            Map<String, String> errors =
                    new LinkedHashMap<>();

            if (groupName.isEmpty()) {
                errors.put(
                        "groupName",
                        "グループ名を入力してください"
                );

            } else if (groupName.length()
                    > MAX_GROUP_NAME_LENGTH) {

                errors.put(
                        "groupName",
                        "グループ名は30文字以内で入力してください"
                );
            }

            if (!errors.isEmpty()) {
                SettingsAction.show(
                        req,
                        resp,
                        user,
                        me,
                        errors,
                        groupName
                );

                return;
            }

            rename =
                    !groupName.equals(
                            currentGroup.getName()
                    );
        }

        SettingsDao settingsDao =
                new SettingsDao();

        boolean oldEmailNotice =
                settingsDao.isEmailNoticeOn(
                        user.getId()
                );

        boolean oldReminder =
                settingsDao.isReminderOn(
                        user.getId()
                );

        String oldGroupName =
                currentGroup.getName();

        boolean settingsSaved =
                false;

        boolean groupRenamed =
                false;

        try {
            settingsDao.save(
                    user.getId(),
                    emailNotice,
                    reminder
            );

            settingsSaved =
                    true;

            if (rename) {
                groupRenamed =
                        groupDao.rename(
                                me.getGroupId(),
                                groupName
                        );

                if (!groupRenamed) {
                    throw new IllegalStateException(
                            "グループ名を変更できませんでした"
                    );
                }
            }

        } catch (Exception e) {
            /*
             * 現在のDAO構成では通知設定とグループ名変更が
             * 別接続になるため、成功済みの処理を可能な範囲で戻す。
             */
            if (settingsSaved) {
                try {
                    settingsDao.save(
                            user.getId(),
                            oldEmailNotice,
                            oldReminder
                    );

                } catch (Exception rollbackError) {
                    AppLog.write(
                            "ERROR",
                            "通知設定を保存前の状態へ戻せませんでした"
                            + "（"
                            + rollbackError.getClass()
                                    .getSimpleName()
                            + "）"
                    );
                }
            }

            if (groupRenamed) {
                try {
                    groupDao.rename(
                            me.getGroupId(),
                            oldGroupName
                    );

                } catch (Exception rollbackError) {
                    AppLog.write(
                            "ERROR",
                            "グループ名を保存前の状態へ戻せませんでした"
                            + "（"
                            + rollbackError.getClass()
                                    .getSimpleName()
                            + "）"
                    );
                }
            }

            AppLog.write(
                    "ERROR",
                    "設定の保存に失敗しました"
                    + "（"
                    + e.getClass()
                            .getSimpleName()
                    + "）"
            );

            flashError(
                    req,
                    "設定を保存できませんでした。"
                    + "通信状態を確認して、もう一度お試しください。"
            );

            resp.sendRedirect(back);

            return;
        }

        flash(
                req,
                "設定を保存しました。"
        );

        resp.sendRedirect(back);
    }
}