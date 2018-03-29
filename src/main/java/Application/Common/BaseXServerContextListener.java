package Application.Common;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.basex.BaseXServer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Bono
 */
public class BaseXServerContextListener implements ServletContextListener{
	
        private ScheduledExecutorService scheduler;
        

        @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
            try {
                scheduler.scheduleAtFixedRate(new BaseXServer("-p1984"), 0,1, TimeUnit.MINUTES);
            } catch (IOException ex) {
                Logger.getLogger(BaseXServerContextListener.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

        @Override
    public void contextDestroyed(ServletContextEvent sce){
        try {
            BaseXServer.stop(1984);
        } catch (Exception ex) {
        }
    }
}
