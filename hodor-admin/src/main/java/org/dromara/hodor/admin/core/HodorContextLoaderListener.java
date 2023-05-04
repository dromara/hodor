package org.dromara.hodor.admin.core;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.context.ContextLoaderListener;

@Slf4j
public class HodorContextLoaderListener extends ContextLoaderListener {

    @Override
    public void contextDestroyed(@NonNull ServletContextEvent event) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    log.info("DeRegistry JDBC driver {}", driver);
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    log.error("Error DeRegistry JDBC driver {}", driver, ex);
                }
            } else {
                // driver was not registered by the webapp's ClassLoader and may
                // be in use elsewhere
                log.error("Not DeRegistry JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }

}
