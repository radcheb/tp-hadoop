package com.radcheb.sysdis.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfUtils {
    private static final String JAR_PATH = "jar.path";
    private static final String USERNAME = "ssh.username";
    private static final String SPLITS_TEMPLATE = "splits.template";
    private static final String SPLITS_DIR = "splits.dir";
    private static final String SPLITS_NBR = "splits.nbr";
    private final static Logger logger = Logger.getLogger(ConfUtils.class);

    private static final Properties prop = new Properties();

    public static void loadConf(InputStream confFileIs) {
        try {
            prop.load(confFileIs);
        } catch (IOException e) {
            logger.error("Failed to load properties from file: {}", e);
        }
    }

    public static int getSplitsNbr() {
        return Integer.valueOf(prop.getProperty(SPLITS_NBR));
    }

    public static String getSplitsTemplate() {
        return prop.getProperty(SPLITS_TEMPLATE);
    }

    public static String getSplitsDir() {
        return prop.getProperty(SPLITS_DIR);
    }


    public static String getUsername() {
        return prop.getProperty(USERNAME);
    }

    public static String getJarPath() { return prop.getProperty(JAR_PATH); }
}
