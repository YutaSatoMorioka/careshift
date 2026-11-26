package tool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dao.CareFeatureDao;
import dao.Dao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Care Shiftの日次メンテナンス。
 *
 * 次の処理を24時間ごとに実行する。
 *
 * ・期限リマインドの作成
 * ・90日を超えたログの整理
 * ・必要に応じた自動バックアップ
 *
 * アプリケーション停止時には、
 * メンテナンススレッドとH2データベースを終了する。
 */
@WebListener
public class CareMaintenanceListener
        implements ServletContextListener {

    /**
     * Tomcat起動後、最初の処理までの待ち時間。
     */
    private static final long INITIAL_DELAY_HOURS =
            1L;

    /**
     * 日次処理の実行間隔。
     */
    private static final long PERIOD_HOURS =
            24L;

    /**
     * アプリ停止時にスケジューラーの終了を待つ秒数。
     */
    private static final long SHUTDOWN_WAIT_SECONDS =
            10L;

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
                             * このスレッドがTomcat停止を
                             * 妨げないようにする。
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
     * 各処理を個別に実行し、1つの処理が失敗しても
     * 後続処理を続行する。
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
     * 期限リマインドを作成する。
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
     * 保存期間を過ぎた旧ログを整理する。
     */
    private void purgeOldLogs(
            ServletContextEvent event,
            CareFeatureDao dao
    ) {

        try {
            dao.purgeOldLogs();

            AppLog.write(
                    "MAINTENANCE",
                    "保存期間を過ぎた操作ログを整理しました"
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
     * 必要な場合だけ自動バックアップを作成する。
     *
     * 直近24時間以内に成功した自動または手動バックアップが
     * 存在する場合は作成しない。
     *
     * H2以外のデータベースでは、
     * アプリ内バックアップ処理を実行しない。
     */
    private void createAutomaticBackup(
            ServletContextEvent event
    ) {

        try {
            if (!BackupManager.isSupported()) {
                return;
            }

            if (!BackupManager.autoBackupDue()) {
                return;
            }

            BackupManager.Record record =
                    BackupManager.backup(
                            "AUTO"
                    );

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
     * 日次処理の失敗をTomcatログと
     * Care Shiftの操作ログへ記録する。
     *
     * メールアドレス、トークン、パスワード、
     * 入力本文などは記録しない。
     */
    private void logFailure(
            ServletContextEvent event,
            String processName,
            Exception exception
    ) {

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
                + exception.getClass()
                        .getSimpleName()
                + "）"
        );
    }

    /**
     * アプリケーション停止時の処理。
     *
     * 1. 新しい定期処理の受付を停止
     * 2. 実行中処理の終了を最大10秒待機
     * 3. 終了しない場合は割り込み
     * 4. H2ファイルデータベースを終了
     */
    @Override
    public void contextDestroyed(
            ServletContextEvent event
    ) {

        stopScheduler(event);

        /*
         * メンテナンス処理の終了後にDBを閉じる。
         *
         * 先にDBを閉じると、実行中のバックアップや
         * リマインド作成と競合する可能性がある。
         */
        Dao.shutdown();

        event.getServletContext().log(
                "Care Shiftのデータベース終了処理が完了しました"
        );
    }

    /**
     * 定期実行スケジューラーを停止する。
     */
    private void stopScheduler(
            ServletContextEvent event
    ) {

        if (scheduler == null) {
            return;
        }

        scheduler.shutdown();

        try {
            boolean terminated =
                    scheduler.awaitTermination(
                            SHUTDOWN_WAIT_SECONDS,
                            TimeUnit.SECONDS
                    );

            if (!terminated) {
                scheduler.shutdownNow();

                event.getServletContext().log(
                        "Care Shift日次メンテナンスを"
                        + "強制停止しました"
                );
            }

        } catch (InterruptedException e) {
            scheduler.shutdownNow();

            /*
             * 割り込み状態を復元する。
             */
            Thread.currentThread()
                    .interrupt();

            event.getServletContext().log(
                    "Care Shift日次メンテナンスの"
                    + "終了待機が中断されました"
            );
        }

        scheduler =
                null;
    }
}