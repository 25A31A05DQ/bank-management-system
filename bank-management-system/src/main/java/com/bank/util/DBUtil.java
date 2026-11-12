package com.bank.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Creates JDBC connections using the settings in db.properties. */
public final class DBUtil {

    private static Properties config;

    private DBUtil() {
    }

    private static synchronized Properties config() throws SQLException {
        if (config == null) {
            Properties p = new Properties();
            try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in == null) {
                    throw new SQLException("db.properties was not found on the classpath.");
                }
                p.load(in);
                Class.forName(p.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            } catch (IOException e) {
                throw new SQLException("Could not read db.properties.", e);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC driver not found.", e);
            }
            config = p;
        }
        return config;
    }

    /** Opens a new connection. The caller must close it (use try-with-resources). */
    public static Connection getConnection() throws SQLException {
        Properties p = config();
        return DriverManager.getConnection(
                p.getProperty("db.url"),
                p.getProperty("db.user"),
                p.getProperty("db.password"));
    }
}
