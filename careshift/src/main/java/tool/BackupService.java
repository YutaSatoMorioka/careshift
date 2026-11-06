package tool;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/** H2ローカルDBのSQLバックアップ。保存先は -Dcareshift.backup.dir で変更する。 */
public final class BackupService {
 private BackupService(){}
 public static Path directory(){return Path.of(System.getProperty("careshift.backup.dir",System.getProperty("user.home")+"/careshift-backups")).toAbsolutePath().normalize();}
 public static synchronized Path create() throws Exception {
  if(!System.getProperty("careshift.db.driver","org.h2.Driver").equals("org.h2.Driver"))throw new IllegalStateException("自動バックアップはH2用です。PostgreSQLではpg_dumpを設定してください。");
  Path dir=directory();Files.createDirectories(dir);String stamp=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));Path file=dir.resolve("careshift-"+stamp+".sql");
  String url=System.getProperty("careshift.db.url","jdbc:h2:file:~/careshift-db/careshift;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
  Class.forName(System.getProperty("careshift.db.driver","org.h2.Driver"));
  try(Connection c=DriverManager.getConnection(url,System.getProperty("careshift.db.user","sa"),System.getProperty("careshift.db.password",""));Statement s=c.createStatement()){
   String escaped=file.toString().replace("'","''");s.execute("SCRIPT TO '"+escaped+"'");
  }
  List<Path> backups;try(var paths=Files.list(dir)){backups=paths.filter(p->p.getFileName().toString().matches("careshift-[0-9]{8}-[0-9]{6}\\.sql")).sorted(Comparator.reverseOrder()).toList();}
  for(int i=7;i<backups.size();i++)Files.deleteIfExists(backups.get(i));return file;
 }
 public static List<Path> list()throws Exception{Path dir=directory();if(!Files.isDirectory(dir))return List.of();try(var p=Files.list(dir)){return p.filter(f->f.getFileName().toString().matches("careshift-[0-9]{8}-[0-9]{6}\\.sql")).sorted(Comparator.reverseOrder()).toList();}}
 public static synchronized void restore(String fileName,String adminPassword)throws Exception {
  if(!System.getProperty("careshift.db.driver","org.h2.Driver").equals("org.h2.Driver"))throw new IllegalStateException("この復元処理はH2用です。");
  String configuredAdminPassword=System.getProperty("careshift.admin.password");if(configuredAdminPassword==null||configuredAdminPassword.isBlank()||adminPassword==null||!java.security.MessageDigest.isEqual(configuredAdminPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8),adminPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8)))throw new SecurityException("管理者パスワードが正しくありません。");
  Path file=directory().resolve(fileName).normalize();if(!file.getParent().equals(directory())||!file.getFileName().toString().matches("careshift-[0-9]{8}-[0-9]{6}\\.sql")||!Files.isRegularFile(file))throw new SecurityException("バックアップファイルを選択してください。");
  String url=System.getProperty("careshift.db.url","jdbc:h2:file:~/careshift-db/careshift;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");Class.forName("org.h2.Driver");
  try(Connection c=DriverManager.getConnection(url,System.getProperty("careshift.db.user","sa"),System.getProperty("careshift.db.password",""));Statement s=c.createStatement()){
   s.execute("DROP ALL OBJECTS");s.execute("RUNSCRIPT FROM '"+file.toString().replace("'","''")+"'");
  }
 }
}
