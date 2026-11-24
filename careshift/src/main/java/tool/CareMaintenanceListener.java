package tool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dao.CareFeatureDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Care Shiftの日次メンテナンス。
 *
 * 次の処理を24時間ごとに実行する。
 *
 * ・タスク期限リマインドの作成
 * ・90日を超えた操作ログの削除
 * ・24時間以内に成功したバックアップがなければ自動バックアップ
 *
 * 各処理は個別に例外を処理する。
 * 1つの処理に失敗しても、ほかの処理は続行する。
 */
@WebListener
public class CareMaintenanceListener
        implements ServletContextListener {

    /**
     * Tomcat起動後、最初の日次処理を開始するまでの時間。
     */
    private static final long INITIAL_DELAY_HOURS =
            1L;

    /**
     * 日次処理の実行間隔。
     */
    private static final long PERIOD_HOURS =
            24L;

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(
            ServletContextEvent event
    ) {

        scheduler =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {

                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "careshift-maintenance"
                                    );

                            /*
                             * Tomcat停止時に、このスレッドが
                             * JVMの終了を妨げないようにする。
                             */
                            thread.setDaemon(true);

                            return thread;
                        }
                );

        scheduler.scheduleAtFixedRate(
                () -> runMaintenance(event),
                INITIAL_DELAY_HOURS,
                PERIOD_HOURS,
                TimeUnit.HOURS
        );

        event.getServletContext().log(
                "Care Shift日次メンテナンスを開始しました"
        );
    }

    /**
     * 日次メンテナンスを実行する。
     *
     * 各処理を分離し、1つの失敗によって
     * 後続処理が実行されなくなることを防ぐ。
     */
    private void runMaintenance(
            ServletContextEvent event
    ) {

        CareFeatureDao dao =
                new CareFeatureDao();

        createDueReminders(
                event,
                dao
        );

        purgeOldLogs(
                event,
                dao
        );

        createAutomaticBackup(
                event
        );
    }

    /**
     * 翌日期限のタスクに対するリマインドを作成する。
     */
    private void createDueReminders(
            ServletContextEvent event,
            CareFeatureDao dao
    ) {

        try {
            dao.createDueReminders();

            AppLog.write(
                    "MAINTENANCE",
                    "期限リマインドの日次処理が完了しました"
            );

        } catch (Exception e) {

            logFailure(
                    event,
                    "期限リマインドの日次処理",
                    e
            );
        }
    }

    /**
     * 90日を超えた操作ログを削除する。
     */
    private void purgeOldLogs(
            ServletContextEvent event,
            CareFeatureDao dao
    ) {

        try {
            dao.purgeOldLogs();

            AppLog.write(
                    "MAINTENANCE",
                    "90日を超えた操作ログを整理しました"
            );

        } catch (Exception e) {

            logFailure(
                    event,
                    "操作ログの整理",
                    e
            );
        }
    }

    /**
     * 必要な場合のみ自動バックアップを作成する。
     *
     * 直近24時間以内に成功した自動または手動バックアップが
     * 存在する場合は、新しいバックアップを作成しない。
     *
     * バックアップ未対応のデータベースの場合も実行しない。
     */
    private void createAutomaticBackup(
            ServletContextEvent event
    ) {

        try {
            if (!BackupManager.isSupported()) {

                AppLog.write(
                        "MAINTENANCE",
                        "現在のデータベースはアプリ内自動バックアップの対象外です"
                );

                return;
            }

            if (!BackupManager.autoBackupDue()) {

                AppLog.write(
                        "MAINTENANCE",
                        "直近24時間以内に成功したバックアップがあるため、"
                        + "自動バックアップを省略しました"
                );

                return;
            }

            BackupManager.Record record =
                    BackupManager.backup("AUTO");

            AppLog.write(
                    "MAINTENANCE",
                    "自動バックアップが完了しました"
                    + "（"
                    + record.getAtLabel()
                    + "・"
                    + record.getSizeLabel()
                    + "）"
            );

        } catch (Exception e) {

            logFailure(
                    event,
                    "自動バックアップ",
                    e
            );
        }
    }

    /**
     * 日次処理の失敗をTomcatログとCare Shiftの操作ログへ記録する。
     *
     * パスワード、相談文、トークンなどの入力値は記録しない。
     */
    private void logFailure(
            ServletContextEvent event,
            String processName,
            Exception exception
    ) {

        String exceptionName =
                exception.getClass()
                        .getSimpleName();

        event.getServletContext().log(
                "Care Shiftの"
                + processName
                + "に失敗しました",
                exception
        );

        AppLog.write(
                "ERROR",
                processName
                + "に失敗しました"
                + "（"
                + exceptionName
                + "）"
        );
    }

    @Override
    public void contextDestroyed(
            ServletContextEvent event
    ) {

        if (scheduler == null) {
            return;
        }

        scheduler.shutdown();

        try {
            /*
             * 実行中の処理がある場合は最大10秒待つ。
             */
            if (!scheduler.awaitTermination(
                    10L,
                    TimeUnit.SECONDS
            )) {
                scheduler.shutdownNow();
            }

        } catch (InterruptedException e) {

            scheduler.shutdownNow();

            Thread.currentThread()
                    .interrupt();
        }

        event.getServletContext().log(
                "Care Shift日次メンテナンスを停止しました"
        );
    }
}