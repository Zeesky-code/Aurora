package com.aurora.core.config;

import java.io.InputStream;
import java.util.Properties;

public class AuroraConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AuroraConfig.class.getClassLoader()
                .getResourceAsStream("aurora.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load aurora.properties", e);
        }
    }

    public static String getZookeeperConnectString() {
        return properties.getProperty("aurora.zookeeper.connectString", "localhost:2181");
    }

    public static int getWorkerThreads() {
        return Integer.parseInt(properties.getProperty("aurora.worker.threads", "4"));
    }
}
