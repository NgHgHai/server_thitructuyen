package edu.vn.hcmuaf.layer0.network.websocket;

import jakarta.servlet.annotation.WebListener;
import org.apache.log4j.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.File;

@WebListener

public class SocketInitialization implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(SocketInitialization.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        System.out.println("SocketInitialization.contextInitialized");
        System.out.println("host : "+System.getenv("DB_HOST")+" port : "+System.getenv("DB_PORT"));
        logger.info("SocketInitialization.contextInitialized");
        logger.info("host : "+System.getenv("DB_HOST")+" port : "+System.getenv("DB_PORT"));

//        File uploadDir = new File("/usr/local/tomcat/webapps/ROOT/uploads");
//        if (!uploadDir.exists()) {
//            uploadDir.mkdirs();
//        }
//        System.out.println(uploadDir.getAbsolutePath());
        ServerEndpoint.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServerEndpoint.destroy();
    }
}
