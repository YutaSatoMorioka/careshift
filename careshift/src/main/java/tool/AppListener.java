package tool;

import dao.Dao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** アプリの停止・再読み込み時に DB を閉じる（H2のファイルロックが残らないようにする）。 */
@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Dao.shutdown();
    }
}
