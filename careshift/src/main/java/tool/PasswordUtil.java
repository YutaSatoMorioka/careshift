package tool;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * パスワードのハッシュ化と照合。
 *
 * パスワードを平文では保存しない。
 *
 * 保存形式:
 *
 * 反復回数:salt:hash
 */
public final class PasswordUtil {

    private static final int ITERATIONS =
            120_000;

    private static final int KEY_BITS =
            256;

    private static final int SALT_BYTES =
            16;

    /**
     * DBから読み込む反復回数の安全な上限。
     *
     * 異常な値による過度なCPU負荷を防ぐ。
     */
    private static final int MAX_STORED_ITERATIONS =
            1_000_000;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private PasswordUtil() {
    }

    /**
     * パスワードをPBKDF2WithHmacSHA256でハッシュ化する。
     */
    public static String hash(
            String password
    ) {

        if (password == null) {
            throw new IllegalArgumentException(
                    "passwordはnullにできません"
            );
        }

        byte[] salt =
                new byte[SALT_BYTES];

        RANDOM.nextBytes(salt);

        char[] chars =
                password.toCharArray();

        try {
            byte[] hash =
                    pbkdf2(
                            chars,
                            salt,
                            ITERATIONS
                    );

            return ITERATIONS
                    + ":"
                    + b64(salt)
                    + ":"
                    + b64(hash);

        } finally {
            Arrays.fill(
                    chars,
                    '\0'
            );
        }
    }

    /**
     * 入力されたパスワードを保存済みハッシュと照合する。
     *
     * 保存形式が不正な場合や、ハッシュの解析に失敗した場合は
     * 例外を画面へ伝播させずfalseを返す。
     */
    public static boolean verify(
            String password,
            String stored
    ) {

        if (password == null
                || stored == null
                || stored.isEmpty()) {

            return false;
        }

        char[] chars =
                password.toCharArray();

        try {
            String[] parts =
                    stored.split(
                            ":",
                            -1
                    );

            if (parts.length != 3) {
                return false;
            }

            int iterations =
                    Integer.parseInt(
                            parts[0]
                    );

            if (iterations < 1
                    || iterations
                    > MAX_STORED_ITERATIONS) {

                return false;
            }

            byte[] salt =
                    Base64.getDecoder()
                            .decode(parts[1]);

            byte[] expected =
                    Base64.getDecoder()
                            .decode(parts[2]);

            if (salt.length < 8
                    || expected.length < 16) {

                return false;
            }

            byte[] actual =
                    pbkdf2(
                            chars,
                            salt,
                            iterations
                    );

            return MessageDigest.isEqual(
                    expected,
                    actual
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException e
        ) {
            return false;

        } finally {
            Arrays.fill(
                    chars,
                    '\0'
            );
        }
    }

    /**
     * PBKDF2WithHmacSHA256を実行する。
     */
    private static byte[] pbkdf2(
            char[] password,
            byte[] salt,
            int iterations
    ) {

        PBEKeySpec specification =
                new PBEKeySpec(
                        password,
                        salt,
                        iterations,
                        KEY_BITS
                );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return factory
                    .generateSecret(
                            specification
                    )
                    .getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "パスワードを処理できません",
                    e
            );

        } finally {
            specification.clearPassword();
        }
    }

    /**
     * Base64文字列へ変換する。
     */
    private static String b64(
            byte[] value
    ) {

        return Base64.getEncoder()
                .encodeToString(value);
    }
}