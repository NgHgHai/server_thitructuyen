package edu.vn.hcmuaf.layer2.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.vn.hcmuaf.layer2.dao.bean.UserBean;
import org.jdbi.v3.core.Jdbi;

import java.util.Properties;


public abstract class PoolConnectDAO {
    private static final Properties prop = new Properties();
    private static final HikariConfig config;
    public static HikariDataSource dataSource;
    private static Jdbi jdbi;

    static {
        try {
            prop.load(PoolConnectDAO.class.getClassLoader().getResourceAsStream("db.properties"));
            config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + getDbHost() + ":" + getDbPort() + "/" + getDbName());
            System.out.println("jdbc:mysql://" + getDbHost() + ":" + getDbPort() + "/" + getDbName());
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(getUsername());
            config.setPassword(getPassword());
            config.setPoolName("db-pool-jdbi");
            config.setMinimumIdle(10);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            System.out.println("connecting...");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    protected static Jdbi getJdbi() {
        if (dataSource == null || dataSource.isClosed()) {
            dataSource = new HikariDataSource(config);
            jdbi = Jdbi.create(dataSource);
        }
        return jdbi;
    }

    private static String getPassword() {
        return prop.getProperty("db.password");
    }

    private static String getUsername() {
        return prop.getProperty("db.username");
    }

    private static String getDbName() {
        return prop.getProperty("db.name");
    }

    private static String getDbPort() {
        return prop.getProperty("db.port");
    }

    private static String getDbHost() {
        return prop.getProperty("db.host");
    }

}
