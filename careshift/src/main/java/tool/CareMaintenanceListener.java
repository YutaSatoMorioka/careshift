package tool;

import dao.CareFeatureDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** 日次リマインド、バックアップ、90日を超えたログの整理。 */
@WebListener
public class CareMaintenanceListener implements ServletContextListener {
 private ScheduledExecutorService scheduler;
 @Override public void contextInitialized(ServletContextEvent event){
  scheduler=Executors.newSingleThreadScheduledExecutor(r->{Thread t=new Thread(r,"careshift-maintenance");t.setDaemon(true);return t;});
  scheduler.scheduleAtFixedRate(()->{
   try{CareFeatureDao dao=new CareFeatureDao();dao.createDueReminders();dao.purgeOldLogs();BackupService.create();}
   catch(Exception e){event.getServletContext().log("Care Shift日次処理に失敗しました",e);}
  },1,24,TimeUnit.HOURS);
 }
 @Override public void contextDestroyed(ServletContextEvent event){if(scheduler!=null)scheduler.shutdownNow();}
}
