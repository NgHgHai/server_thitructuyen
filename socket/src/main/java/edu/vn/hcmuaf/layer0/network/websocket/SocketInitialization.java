package edu.vn.hcmuaf.layer0.network.websocket;

import org.apache.log4j.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class SocketInitialization implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(SocketInitialization.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServerEndpoint.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServerEndpoint.destroy();
    }
}
