package tool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 障害ログなどの運用ログ（S90 で閲覧）。DBではなくファイルに書くので、データを復元しても消えない。
 * 種類: ERROR（障害）/ BACKUP / RESTORE / AUTH（管理者の再認証の失敗など）。90日分を保持する。
 * 記録するのは固定の短い文だけ。パスワードや相談文の本文は、呼び出し側でも絶対に渡さないこと。
 */
public final class AppLog {

    public static final List<String> TYPES = List.of("ERROR", "BACKUP", "RESTORE", "AUTH");
    static final int RETENTION_DAYS = 90;

    /** 1行分 */
    public static final class Entry {
        private final LocalDateTime at;
        private final String type;
        private final String message;

        Entry(LocalDateTime at, String type, String message) {
            this.at = at;
            this.type = type;
            this.message = message;
        }

        public LocalDateTime getAt() { return at; }
        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getAtLabel() { return at.toString().replace('T', ' ').substring(0, 19); }
    }

    private AppLog() {}

    /** バックアップ・ログを置くフォルダ（既定はユーザーフォルダの careshift-backups） */
    public static Path dir() {
        return Paths.get(System.getProperty("careshift.backup.dir",
                System.getProperty("user.home") + "/careshift-backups"));
    }

    private static Path file() {
        return dir().resolve("app.log");
    }

    public static synchronized void write(String type, String message) {
        try {
            Files.createDirectories(dir());
            String line = LocalDateTime.now().withNano(0) + "\t" + type + "\t" + clean(message) + "\n";
            Files.writeString(file(), line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace(); // ログが書けなくても本処理は止めない
        }
    }

    private static String clean(String s) {
        String t = s == null ? "" : s.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
        return t.length() > 300 ? t.substring(0, 300) : t;
    }

    /** 期間（両端を含む。null は指定なし）と種類（空は全種類）で絞り込み、新しい順に最大 limit 件。 */
    public static synchronized List<Entry> read(LocalDate from, LocalDate to, String type, int limit) {
        List<Entry> all = new ArrayList<>();
        Path f = file();
        if (!Files.exists(f)) return all;
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(RETENTION_DAYS);
            List<String> keep = new ArrayList<>();
            boolean purged = false;
            for (String line : Files.readAllLines(f, StandardCharsets.UTF_8)) {
                String[] p = line.split("\t", 3);
                if (p.length < 3) continue;
                LocalDateTime at;
                try {
                    at = LocalDateTime.parse(p[0]);
                } catch (Exception e) {
                    continue;
                }
                if (at.isBefore(cutoff)) { // 90日より古いものは捨てる
                    purged = true;
                    continue;
                }
                keep.add(line);
                if (from != null && at.toLocalDate().isBefore(from)) continue;
                if (to != null && at.toLocalDate().isAfter(to)) continue;
                if (type != null && !type.isEmpty() && !p[1].equals(type)) continue;
                all.add(new Entry(at, p[1], p[2]));
            }
            if (purged) {
                Files.write(f, keep, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(all); // 同じ秒のものは、あとに書いたほうを先にする
        all.sort((x, y) -> y.getAt().compareTo(x.getAt())); // 新しい順（安定ソート）
        return all.size() > limit ? all.subList(0, limit) : all;
    }
}
