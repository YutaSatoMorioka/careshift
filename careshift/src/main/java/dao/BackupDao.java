package dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.Statement;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * S90：データベースの書き出し（バックアップ）と読み込み（復元）。H2 の SCRIPT / RUNSCRIPT を使う。
 * 書き出した SQL は、Java 側で標準の gzip に圧縮して保存する（H2 の圧縮方式に依存しない）。
 * 途中でできる一時ファイル（圧縮前の SQL）は、本人だけが読める権限で作り、終わったらすぐ消す。
 * PostgreSQL など H2 以外のときは使えない（pg_dump などを使う）。
 */
public class BackupDao extends Dao {

    /** 全データを SQL 文として書き出し、gzip 圧縮して file に保存する。 */
    public void scriptTo(Path file) throws Exception {
        Path tmp = tempFile(file);
        try {
            try (Connection con = getConnection(); Statement st = con.createStatement()) {
                st.execute("SCRIPT TO '" + quote(tmp) + "'");
            }
            try (InputStream in = Files.newInputStream(tmp);
                    OutputStream out = new GZIPOutputStream(Files.newOutputStream(file))) {
                in.transferTo(out);
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    /** いまのデータをすべて消して、バックアップ（gzip）の内容に置き換える。 */
    public void restoreFrom(Path file) throws Exception {
        Path tmp = tempFile(file);
        try {
            try (InputStream in = new GZIPInputStream(Files.newInputStream(file));
                    OutputStream out = Files.newOutputStream(tmp)) {
                in.transferTo(out);
            }
            try (Connection con = getConnection(); Statement st = con.createStatement()) {
                st.execute("DROP ALL OBJECTS");
                st.execute("RUNSCRIPT FROM '" + quote(tmp) + "'");
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    /** 一時ファイルの場所（バックアップと同じフォルダ）。作ったら本人だけが読める権限にする。 */
    private static Path tempFile(Path near) throws IOException {
        Path tmp = near.resolveSibling(near.getFileName() + ".tmp");
        Files.deleteIfExists(tmp);
        Files.createFile(tmp);
        try {
            Files.setPosixFilePermissions(tmp, PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException | IOException ignore) {
            // Windows などでは設定できない
        }
        return tmp;
    }

    private static String quote(Path file) {
        return file.toAbsolutePath().toString().replace("'", "''");
    }
}
