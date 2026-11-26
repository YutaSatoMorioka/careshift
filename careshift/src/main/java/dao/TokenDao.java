package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import bean.EmailToken;

/**
 * email_tokensテーブルの操作。
 *
 * メール確認用トークンと
 * パスワード再設定用トークンを扱う。
 */
public class TokenDao extends Dao {

    /**
     * メール確認などのトークン処理結果。
     */
    public enum TokenProcessResult {

        /**
         * 処理成功。
         */
        SUCCESS,

        /**
         * トークンが存在しない。
         */
        NOT_FOUND,

        /**
         * トークンが使用済み。
         */
        USED,

        /**
         * トークンの有効期限切れ。
         */
        EXPIRED,

        /**
         * 対応するユーザーが存在しない。
         */
        USER_NOT_FOUND
    }

    /**
     * パスワード再設定処理の結果。
     */
    public static final class PasswordResetResult {

        private final TokenProcessResult result;

        private final long userId;

        private PasswordResetResult(
                TokenProcessResult result,
                long userId
        ) {
            this.result = result;
            this.userId = userId;
        }

        /**
         * 成功結果を作成する。
         */
        public static PasswordResetResult success(
                long userId
        ) {
            return new PasswordResetResult(
                    TokenProcessResult.SUCCESS,
                    userId
            );
        }

        /**
         * 失敗結果を作成する。
         */
        public static PasswordResetResult failure(
                TokenProcessResult result
        ) {
            return new PasswordResetResult(
                    result,
                    -1L
            );
        }

        public boolean isSuccess() {
            return result == TokenProcessResult.SUCCESS;
        }

        public TokenProcessResult getResult() {
            return result;
        }

        public long getUserId() {
            return userId;
        }
    }

    /**
     * 同じ目的の未使用トークンを無効にしてから、
     * 新しいトークンを登録する。
     *
     * 無効化と新規登録は同一トランザクションで実行する。
     */
    public void issue(
            long userId,
            String purpose,
            String tokenHash,
            LocalDateTime expiresAt
    ) throws Exception {

        try (Connection con = getConnection()) {

            boolean originalAutoCommit =
                    con.getAutoCommit();

            con.setAutoCommit(false);

            try {
                String invalidateSql =
                        "UPDATE email_tokens"
                        + " SET used = TRUE"
                        + " WHERE user_id = ?"
                        + " AND purpose = ?"
                        + " AND used = FALSE";

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    invalidateSql
                            )
                ) {
                    ps.setLong(
                            1,
                            userId
                    );

                    ps.setString(
                            2,
                            purpose
                    );

                    ps.executeUpdate();
                }

                String insertSql =
                        "INSERT INTO email_tokens"
                        + " ("
                        + " user_id,"
                        + " purpose,"
                        + " token_hash,"
                        + " expires_at"
                        + " )"
                        + " VALUES (?, ?, ?, ?)";

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    insertSql
                            )
                ) {
                    ps.setLong(
                            1,
                            userId
                    );

                    ps.setString(
                            2,
                            purpose
                    );

                    ps.setString(
                            3,
                            tokenHash
                    );

                    ps.setTimestamp(
                            4,
                            Timestamp.valueOf(
                                    expiresAt
                            )
                    );

                    ps.executeUpdate();
                }

                con.commit();

            } catch (Exception e) {
                rollbackQuietly(con);

                throw e;

            } finally {
                restoreAutoCommit(
                        con,
                        originalAutoCommit
                );
            }
        }
    }

    /**
     * ハッシュと目的からトークンを取得する。
     *
     * 使用済みや期限切れのトークンも返す。
     * 有効性は呼び出し側で判定する。
     */
    public EmailToken find(
            String tokenHash,
            String purpose
    ) throws Exception {

        String sql =
                "SELECT"
                + " id,"
                + " user_id,"
                + " purpose,"
                + " expires_at,"
                + " used"
                + " FROM email_tokens"
                + " WHERE token_hash = ?"
                + " AND purpose = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setString(
                    1,
                    tokenHash
            );

            ps.setString(
                    2,
                    purpose
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                if (!rs.next()) {
                    return null;
                }

                Timestamp expiresAt =
                        rs.getTimestamp(
                                "expires_at"
                        );

                if (expiresAt == null) {
                    return null;
                }

                return new EmailToken(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("purpose"),
                        expiresAt.toLocalDateTime(),
                        rs.getBoolean("used")
                );
            }
        }
    }

    /**
     * トークンを使用済みにする。
     *
     * まだ未使用だった場合だけtrueを返す。
     *
     * メール確認にはverifyEmail()、
     * パスワード再設定にはresetPassword()を使用する。
     */
    public boolean consume(
            long tokenId
    ) throws Exception {

        String sql =
                "UPDATE email_tokens"
                + " SET used = TRUE"
                + " WHERE id = ?"
                + " AND used = FALSE";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    tokenId
            );

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * メールアドレス確認を完了する。
     *
     * 次の処理を同一トランザクションで実行する。
     *
     * 1. トークン行をロックする
     * 2. 使用済みと有効期限を確認する
     * 3. users.email_verifiedをTRUEにする
     * 4. トークンを使用済みにする
     */
    public TokenProcessResult verifyEmail(
            String tokenHash,
            String purpose
    ) throws Exception {

        if (isBlank(tokenHash)
                || isBlank(purpose)) {

            return TokenProcessResult.NOT_FOUND;
        }

        try (Connection con = getConnection()) {

            boolean originalAutoCommit =
                    con.getAutoCommit();

            con.setAutoCommit(false);

            try {
                LockedToken token =
                        findForUpdate(
                                con,
                                tokenHash,
                                purpose
                        );

                if (token == null) {
                    rollbackQuietly(con);

                    return TokenProcessResult.NOT_FOUND;
                }

                TokenProcessResult invalidReason =
                        validateLockedToken(token);

                if (invalidReason != null) {
                    rollbackQuietly(con);

                    return invalidReason;
                }

                String updateUserSql =
                        "UPDATE users"
                        + " SET email_verified = TRUE"
                        + " WHERE id = ?";

                int updatedUser;

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    updateUserSql
                            )
                ) {
                    ps.setLong(
                            1,
                            token.userId
                    );

                    updatedUser =
                            ps.executeUpdate();
                }

                if (updatedUser != 1) {
                    rollbackQuietly(con);

                    return TokenProcessResult.USER_NOT_FOUND;
                }

                if (!consume(
                        con,
                        token.id
                )) {
                    rollbackQuietly(con);

                    return TokenProcessResult.USED;
                }

                con.commit();

                return TokenProcessResult.SUCCESS;

            } catch (Exception e) {
                rollbackQuietly(con);

                throw e;

            } finally {
                restoreAutoCommit(
                        con,
                        originalAutoCommit
                );
            }
        }
    }

    /**
     * パスワードを再設定する。
     *
     * 次の処理を同一トランザクションで実行する。
     *
     * 1. トークン行をロックする
     * 2. 使用済みと有効期限を確認する
     * 3. users.password_hashを更新する
     * 4. users.email_verifiedをTRUEにする
     * 5. トークンを使用済みにする
     */
    public PasswordResetResult resetPassword(
            String tokenHash,
            String purpose,
            String newPasswordHash
    ) throws Exception {

        if (isBlank(tokenHash)
                || isBlank(purpose)
                || isBlank(newPasswordHash)) {

            return PasswordResetResult.failure(
                    TokenProcessResult.NOT_FOUND
            );
        }

        try (Connection con = getConnection()) {

            boolean originalAutoCommit =
                    con.getAutoCommit();

            con.setAutoCommit(false);

            try {
                LockedToken token =
                        findForUpdate(
                                con,
                                tokenHash,
                                purpose
                        );

                if (token == null) {
                    rollbackQuietly(con);

                    return PasswordResetResult.failure(
                            TokenProcessResult.NOT_FOUND
                    );
                }

                TokenProcessResult invalidReason =
                        validateLockedToken(token);

                if (invalidReason != null) {
                    rollbackQuietly(con);

                    return PasswordResetResult.failure(
                            invalidReason
                    );
                }

                String updateUserSql =
                        "UPDATE users"
                        + " SET password_hash = ?,"
                        + " email_verified = TRUE"
                        + " WHERE id = ?";

                int updatedUser;

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    updateUserSql
                            )
                ) {
                    ps.setString(
                            1,
                            newPasswordHash
                    );

                    ps.setLong(
                            2,
                            token.userId
                    );

                    updatedUser =
                            ps.executeUpdate();
                }

                if (updatedUser != 1) {
                    rollbackQuietly(con);

                    return PasswordResetResult.failure(
                            TokenProcessResult.USER_NOT_FOUND
                    );
                }

                if (!consume(
                        con,
                        token.id
                )) {
                    rollbackQuietly(con);

                    return PasswordResetResult.failure(
                            TokenProcessResult.USED
                    );
                }

                con.commit();

                return PasswordResetResult.success(
                        token.userId
                );

            } catch (Exception e) {
                rollbackQuietly(con);

                throw e;

            } finally {
                restoreAutoCommit(
                        con,
                        originalAutoCommit
                );
            }
        }
    }

    /**
     * トークン行をロックして取得する。
     */
    private LockedToken findForUpdate(
            Connection con,
            String tokenHash,
            String purpose
    ) throws Exception {

        String sql =
                "SELECT"
                + " id,"
                + " user_id,"
                + " expires_at,"
                + " used"
                + " FROM email_tokens"
                + " WHERE token_hash = ?"
                + " AND purpose = ?"
                + " FOR UPDATE";

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setString(
                    1,
                    tokenHash
            );

            ps.setString(
                    2,
                    purpose
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                if (!rs.next()) {
                    return null;
                }

                Timestamp expiresAt =
                        rs.getTimestamp(
                                "expires_at"
                        );

                return new LockedToken(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        expiresAt == null
                                ? null
                                : expiresAt.toLocalDateTime(),
                        rs.getBoolean("used")
                );
            }
        }
    }

    /**
     * ロック済みトークンが使用可能か確認する。
     *
     * 使用可能な場合はnullを返す。
     */
    private TokenProcessResult validateLockedToken(
            LockedToken token
    ) {

        if (token.used) {
            return TokenProcessResult.USED;
        }

        if (token.expiresAt == null
                || LocalDateTime.now()
                        .isAfter(
                                token.expiresAt
                        )) {

            return TokenProcessResult.EXPIRED;
        }

        return null;
    }

    /**
     * 同じ接続を使用してトークンを消費する。
     */
    private boolean consume(
            Connection con,
            long tokenId
    ) throws Exception {

        String sql =
                "UPDATE email_tokens"
                + " SET used = TRUE"
                + " WHERE id = ?"
                + " AND used = FALSE";

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    tokenId
            );

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * nullまたは空文字か判定する。
     */
    private boolean isBlank(
            String value
    ) {
        return value == null
                || value.isEmpty();
    }

    /**
     * 例外を追加送出せずにロールバックする。
     */
    private void rollbackQuietly(
            Connection con
    ) {

        try {
            con.rollback();

        } catch (Exception ignore) {
            /*
             * 元の例外または処理結果を優先する。
             */
        }
    }

    /**
     * autoCommit設定を接続取得時の状態へ戻す。
     */
    private void restoreAutoCommit(
            Connection con,
            boolean originalAutoCommit
    ) {

        try {
            if (!con.isClosed()
                    && con.getAutoCommit()
                    != originalAutoCommit) {

                con.setAutoCommit(
                        originalAutoCommit
                );
            }

        } catch (Exception ignore) {
            /*
             * 接続はtry-with-resourcesで閉じられるため、
             * ここでは追加の例外を送出しない。
             */
        }
    }

    /**
     * 行ロック中のトークン情報。
     */
    private static final class LockedToken {

        private final long id;

        private final long userId;

        private final LocalDateTime expiresAt;

        private final boolean used;

        private LockedToken(
                long id,
                long userId,
                LocalDateTime expiresAt,
                boolean used
        ) {
            this.id = id;
            this.userId = userId;
            this.expiresAt = expiresAt;
            this.used = used;
        }
    }
}