package tool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dao.Dao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * アプリの起動・停止の処理。
 *  - 起動時: 毎日の自動バックアップ（S90）の見張りを始める（1時間ごとに「24時間以内の成功がなければ取る」）
 *  - 停止時: 見張りを止め、DB を閉じる（H2のファイルロックが残らないようにする）
 */
@WebListener
public class AppListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "careshift-auto-backup");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (BackupManager.isSupported() && BackupManager.autoBackupDue()) {
                    BackupManager.backup("AUTO");
                }
            } catch (Exception e) {
                // 失敗は BackupManager が履歴とログに残している
            }
        }, 1, 60, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        Dao.shutdown();
    }
}
