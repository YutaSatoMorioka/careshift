package tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import dao.BackupDao;
import dao.Dao;

/**
 * S90 バックアップ・復元。データを SQL 文に書き出した圧縮ファイルを、ファイルとして世代管理する。
 *  - 種類: AUTO（毎日の自動）/ MANUAL（手動）/ PRE_RESTORE（復元の直前に自動で取る退避）
 *  - AUTO と MANUAL は直近7世代を保持し、PRE_RESTORE は直近3世代を保持する
 *  - 履歴（日時・結果・サイズ）は、DBではなくファイルに残す（復元しても消えない）
 * バックアップのファイルにはパスワードのハッシュや個人情報が入るため、フォルダの管理に注意すること。
 */
public final class BackupManager {

    public static final int GENERATIONS = 7;
    static final int PRE_RESTORE_KEEP = 3;
    private static final Pattern NAME = Pattern.compile("^careshift-(\\d{8}-\\d{9})-(AUTO|MANUAL|PRE_RESTORE)\\.sql\\.gz$");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS");

    /** 履歴の1行 */
    public static final class Record {
        private final LocalDateTime at;
        private final String kind;
        private final boolean ok;
        private final String fileName;
        private final long size;
        private final String note;

        Record(LocalDateTime at, String kind, boolean ok, String fileName, long size, String note) {
            this.at = at;
            this.kind = kind;
            this.ok = ok;
            this.fileName = fileName;
            this.size = size;
            this.note = note;
        }

        public LocalDateTime getAt() { return at; }
        public String getAtLabel() { return at.toString().replace('T', ' ').substring(0, 19); }
        public String getKind() { return kind; }
        public String getKindLabel() { return "AUTO".equals(kind) ? "自動" : "MANUAL".equals(kind) ? "手動" : "復元前の退避"; }
        public boolean isOk() { return ok; }
        public String getResultLabel() { return ok ? "成功" : "失敗"; }
        public String getFileName() { return fileName; }
        public long getSize() { return size; }
        public String getSizeLabel() { return ok ? (size < 1024 ? size + " B" : (size / 1024) + " KB") : "―"; }
        public String getNote() { return note; }
        /** 復元に使えるか（成功した自動・手動のバックアップで、ファイルが残っている） */
        public boolean isRestorable() { return ok && !"PRE_RESTORE".equals(kind); }
    }

    /** 復元の失敗 */
    public static final class RestoreException extends Exception {
        private static final long serialVersionUID = 1L;
        private final boolean rolledBack;

        RestoreException(String message, boolean rolledBack) {
            super(message);
            this.rolledBack = rolledBack;
        }

        /** 失敗したが、直前の退避から元の状態に戻せた */
        public boolean isRolledBack() { return rolledBack; }
    }

    private BackupManager() {}

    private static Path history() {
        return AppLog.dir().resolve("history.tsv");
    }

    public static boolean isSupported() {
        return Dao.isH2();
    }

    // ------------------------------------------------------------------ バックアップ

    /** バックアップを1つ取る。結果は履歴とログに残す。失敗したら例外。 */
    public static synchronized Record backup(String kind) throws Exception {
        LocalDateTime now = LocalDateTime.now().withNano((LocalDateTime.now().getNano() / 1_000_000) * 1_000_000);
        String name = "careshift-" + now.format(STAMP) + "-" + kind + ".sql.gz";
        Path file = AppLog.dir().resolve(name);
        try {
            if (!isSupported()) {
                throw new IllegalStateException("このデータベースでは、アプリからのバックアップに対応していません");
            }
            Files.createDirectories(AppLog.dir());
            new BackupDao().scriptTo(file);
            long size = Files.size(file);
            if (size == 0) {
                throw new IOException("バックアップのファイルが空です");
            }
            try {
                Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------")); // 本人だけが読める
            } catch (UnsupportedOperationException | IOException ignore) {
                // Windows などでは設定できない
            }
            Record r = new Record(now, kind, true, name, size, "");
            append(r);
            rotate();
            AppLog.write("BACKUP", kind + " バックアップに成功しました");
            return r;
        } catch (Exception e) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignore) {
                // 何もしない
            }
            append(new Record(now, kind, false, name, 0, e.getClass().getSimpleName()));
            AppLog.write("ERROR", kind + " バックアップに失敗しました（" + e.getClass().getSimpleName() + "）");
            throw e;
        }
    }

    private static void append(Record r) {
        try {
            Files.createDirectories(AppLog.dir());
            String line = r.at + "\t" + r.kind + "\t" + (r.ok ? "OK" : "NG") + "\t" + r.fileName + "\t" + r.size + "\t"
                    + r.note.replace('\t', ' ').replace('\n', ' ') + "\n";
            Files.writeString(history(), line, StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 履歴を新しい順に。AUTO・MANUAL だけを、直近 limit 件。ファイルが消えている成功分は復元不可として扱う。 */
    public static synchronized List<Record> recent(int limit) {
        List<Record> list = new ArrayList<>();
        Path h = history();
        if (!Files.exists(h)) return list;
        try {
            for (String line : Files.readAllLines(h, StandardCharsets.UTF_8)) {
                String[] p = line.split("\t", -1);
                if (p.length < 6 || "PRE_RESTORE".equals(p[1])) continue;
                boolean ok = "OK".equals(p[2]) && Files.exists(AppLog.dir().resolve(p[3]));
                list.add(new Record(LocalDateTime.parse(p[0]), p[1], ok, p[3], Long.parseLong(p[4]), p[5]));
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list.size() > limit ? list.subList(0, limit) : list;
    }

    /** 古い世代を消す。 */
    private static void rotate() throws IOException {
        keepNewest("(AUTO|MANUAL)", GENERATIONS);
        keepNewest("PRE_RESTORE", PRE_RESTORE_KEEP);
    }

    private static void keepNewest(String kindPattern, int keep) throws IOException {
        List<String> names = new ArrayList<>();
        try (Stream<Path> s = Files.list(AppLog.dir())) {
            s.map(p -> p.getFileName().toString()).filter(n -> NAME.matcher(n).matches() && n.matches(".*-" + kindPattern + "\\.sql\\.gz")).forEach(names::add);
        }
        Collections.sort(names); // 日時が名前に入っているので、名前順＝古い順
        for (int i = 0; i < names.size() - keep; i++) {
            Files.deleteIfExists(AppLog.dir().resolve(names.get(i)));
        }
    }

    /** 自動バックアップの要否：成功した AUTO/MANUAL が、直近24時間にないときだけ true。 */
    public static boolean autoBackupDue() {
        for (Record r : recent(GENERATIONS)) {
            if (r.isOk() && r.getAt().isAfter(LocalDateTime.now().minusHours(24))) return false;
        }
        return true;
    }

    // ------------------------------------------------------------------ 復元

    /**
     * 指定した世代から復元する。
     * 手順: ファイル名の確認 → 破損チェック → 復元前の退避（PRE_RESTORE）→ 復元。
     * 復元に失敗したら、退避から元の状態に戻す。破損・退避の失敗は、何も変えずに中断する。
     */
    public static synchronized void restore(String fileName) throws RestoreException {
        Matcher m = NAME.matcher(fileName == null ? "" : fileName);
        if (!m.matches() || "PRE_RESTORE".equals(m.group(2))) {
            throw new RestoreException("復元できる世代ではありません", false);
        }
        Path file = AppLog.dir().resolve(fileName);
        if (!Files.isRegularFile(file)) {
            throw new RestoreException("バックアップのファイルが見つかりません", false);
        }
        if (!isSupported()) {
            throw new RestoreException("このデータベースでは、アプリからの復元に対応していません", false);
        }
        if (!isIntact(file)) {
            AppLog.write("ERROR", "復元を中断しました：バックアップのファイルが壊れています");
            throw new RestoreException("バックアップのファイルが壊れているため、復元を中断しました（データは変更していません）", false);
        }

        Record pre;
        try {
            pre = backup("PRE_RESTORE");
        } catch (Exception e) {
            throw new RestoreException("復元前の退避に失敗したため、復元を中断しました（データは変更していません）", false);
        }

        try {
            new BackupDao().restoreFrom(file);
            Dao.reinit();
            AppLog.write("RESTORE", "復元に成功しました");
        } catch (Exception e) {
            AppLog.write("ERROR", "復元に失敗しました（" + e.getClass().getSimpleName() + "）。退避から元に戻します");
            try {
                new BackupDao().restoreFrom(AppLog.dir().resolve(pre.getFileName()));
                Dao.reinit();
                AppLog.write("RESTORE", "復元の失敗後、退避から元の状態に戻しました");
                throw new RestoreException("復元に失敗したため、直前の状態に戻しました", true);
            } catch (RestoreException re) {
                throw re;
            } catch (Exception again) {
                AppLog.write("ERROR", "退避からの巻き戻しにも失敗しました（" + again.getClass().getSimpleName() + "）");
                throw new RestoreException("復元に失敗し、元の状態にも戻せませんでした。退避ファイルから手動で復旧してください", false);
            }
        }
    }

    /** 壊れていないか：最後まで読め（圧縮の検査が通り）、SQL として中身がある。 */
    static boolean isIntact(Path file) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(file)), StandardCharsets.UTF_8))) {
            boolean hasCreate = false;
            boolean hasUsers = false;
            String line;
            while ((line = r.readLine()) != null) {
                String u = line.toUpperCase();
                if (u.startsWith("CREATE ") && u.contains("TABLE")) hasCreate = true;
                if (u.contains("\"USERS\"") || u.contains(" USERS")) hasUsers = true;
            }
            return hasCreate && hasUsers;
        } catch (IOException e) {
            return false;
        }
    }
}
