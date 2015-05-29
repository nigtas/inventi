package lt.inventi.jetty;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Used to launch embeded jetty server, for example in Eclipse.
 */
public class JettyLauncher {

    private static final Logger log = LoggerFactory.getLogger(JettyLauncher.class);

    /**
     * Main function, starts the jetty server.
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String context = null;
        String webAppDir = null;
        Integer port = null;

        for(int i=0; i < args.length;i++){
            if("-context".equals(args[i])){
                context=args[i+1];
            } else if("-webapp".equals(args[i])){
                webAppDir=args[i+1];
            } else if("-port".equals(args[i])){
                port=Integer.valueOf(args[i+1]);
            }
        }

        if(context == null){
            context = "/";
        }
        if(webAppDir == null){
            webAppDir = "src/main/webapp";
        }
        if(port == null){
            port = 8080;
        }

        log.info("Starting server context: " +context + ", webapp: " + webAppDir + ", port: "+port);

        Server server = new Server();
        server.setStopAtShutdown(true);

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);

        WebAppContext web = new WebAppContext();
        web.setContextPath(context);
        web.setResourceBase(webAppDir);
        web.setConfigurationClasses(
                ImmutableList.<String>builder()
                    .add(web.getConfigurationClasses())
                    .add(EnvConfiguration.class.getCanonicalName())
                    .add(PlusConfiguration.class.getCanonicalName())
                    .build().toArray(new String[web.getConfigurationClasses().length+1])
        );
        web.setThrowUnavailableOnStartupException(true);

        web.setParentLoaderPriority(true);
        server.setHandler(web);

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        server.getContainer().addEventListener(mBeanContainer);
        mBeanContainer.start();

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
