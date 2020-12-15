package com.rkhd.platform.sdk.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

@Slf4j
public class OauthConfig {
    private static final String OAUTH_CONFIG_PROPERTIES = "oauthConfig.properties";
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "password";
    private static final String SECURITY_CODE = "securityCode";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    public static final String DOMAIN = "domain";
    private static Properties properties = new Properties();

    static {
        try {
            InputStream inputStream = getDefaultClassLoader().getResourceAsStream("oauthConfig.properties");
            try {
                if (inputStream != null) {
                    properties.load(inputStream);
                } else {
                    log.error("please config oauthConfig.properties in the resources directory");
                    log.error("Example:\n");
                    log.error("userName=");
                    log.error("password=");
                    log.error("securityCode=");
                    log.error("clientId=");
                    log.error("clientSecret=");
                    log.error("domain=");
                    log.error("modelJarPath=");
                    System.exit(1);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            loadJar(properties.getProperty("modelJarPath", ""));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public OauthConfig() {
        InputStream inputStream = getDefaultClassLoader().getResourceAsStream("oauthConfig.properties");

        try {
            if (inputStream != null) {
                this.properties.load(inputStream);
            } else {
                log.error("please config oauthConfig.properties in the resources directory");
                log.error("Example:\n");
                log.error("userName=");
                log.error("password=");
                log.error("securityCode=");
                log.error("clientId=");
                log.error("clientSecret=");
                log.error("domain=");
                log.error("modelJarPath=");
                System.exit(1);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable throwable) {
        }

        if (cl == null) {
            cl = OauthConfig.class.getClassLoader();
        }
        return cl;
    }

    public static String getUserName() {
        return properties.getProperty("userName", "");
    }

    public static String getPassword() {
        return properties.getProperty("password", "");
    }

    public static String getSecurityCode() {
        return properties.getProperty("securityCode", "");
    }

    public static String getClientId() {
        return properties.getProperty("clientId", "");
    }

    public static String getClientSecret() {
        return properties.getProperty("clientSecret", "");
    }

    public static String getOauthUrl() {
        return getDomain() + "/oauth2/token.action";
    }

    public static String getDomain() {
        return properties.getProperty("domain", "https://api.xiaoshouyi.com");
    }

    public static void loadJar(String jarPath) throws MalformedURLException {
        File jarFile = new File(jarPath); // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹

        if (jarFile.exists() == false) {
            System.out.println("model jar file not found.");
            return;
        }

        //获取类加载器的addURL方法，准备动态调用
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
        }

        // 获取方法的访问权限，保存原始值
        boolean accessible = method.isAccessible();
        try {
            //修改访问权限为可写
            if (accessible == false) {
                method.setAccessible(true);
            }

            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            //获取jar文件的url路径
            URL url = jarFile.toURI().toURL();

            //jar路径加入到系统url路径里
            method.invoke(classLoader, url);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //回写访问权限
            method.setAccessible(accessible);
        }
    }
}