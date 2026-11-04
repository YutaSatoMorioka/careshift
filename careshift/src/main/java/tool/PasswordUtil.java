package tool;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** パスワードのハッシュ化。平文では絶対に保存しない。形式: 反復回数:salt:hash */
public final class PasswordUtil {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return ITERATIONS + ":" + b64(salt) + ":" + b64(pbkdf2(password.toCharArray(), salt, ITERATIONS));
    }

    public static boolean verify(String password, String stored) {
        String[] p = stored.split(":");
        if (p.length != 3) return false;
        int iterations = Integer.parseInt(p[0]);
        byte[] salt = Base64.getDecoder().decode(p[1]);
        byte[] expected = Base64.getDecoder().decode(p[2]);
        byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);
        return MessageDigest.isEqual(expected, actual); // 比較時間が一定になる
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return f.generateSecret(new PBEKeySpec(password, salt, iterations, KEY_BITS)).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String b64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }
}
