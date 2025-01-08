package org.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {
    private ClassScanner() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    /**
     * 扫描指定的包路径，查找并实例化所有实现了指定接口的类，但不包括接口本身。
     *
     * @param packageName    包的全路径名，例如 "org.herostory.handler.cmd"
     * @param recursive      是否递归扫描子包
     * @param interfaceClass 要查找的接口类
     * @param <T>            接口类型
     * @return 实现了指定接口的所有类的实例列表
     * @throws IOException IO 异常
     */
    public static <T> List<T> scanClasses(String packageName, boolean recursive, Class<T> interfaceClass) throws IOException {
        List<T> instances = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        logger.info("Scanning package: {}", packageName);
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("jar")) {
                scanJar(resource, packageName, recursive, interfaceClass, instances);
            } else if (resource.getProtocol().equals("file")) {
                scanDirectory(resource, packageName, recursive, interfaceClass, instances);
            }
        }
        logger.info("Found {} instances of {}", instances.size(), interfaceClass.getName());
        return instances;
    }
    /**
     * 从JAR文件中扫描类。
     * @param resource       JAR文件的URL
     * @param packageName    包名
     * @param recursive      是否递归
     * @param interfaceClass 接口类
     * @param instances      实例列表
     * @param <T>           接口类型
     * @throws IOException              IO异常
     */
    private static <T> void scanJar(URL resource, String packageName, boolean recursive, Class<T> interfaceClass, List<T> instances) throws IOException {
        // 去除jar:前缀和!后面的部分
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
        logger.info("Scanning JAR file: {}", jarPath);
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.startsWith(packageName.replace('.', '/')) && entryName.endsWith(".class")) {
                String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                try {
                    Class<?> clazz = Class.forName(className);
                    if (interfaceClass.isAssignableFrom(clazz) && !interfaceClass.equals(clazz)) {
                        instances.add(interfaceClass.cast(clazz.getDeclaredConstructor().newInstance()));
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException |
                         InvocationTargetException e) {
                    logger.error("从JAR文件中扫描类异常", e);
                }
            }
        }
    }
    /**
     * 从目录中扫描类。
     * @param resource       目录的URL
     * @param packageName    包名
     * @param recursive      是否递归
     * @param interfaceClass 接口类
     * @param instances      实例列表
     * @param <T>           接口类型
     */
    private static <T> void scanDirectory(URL resource, String packageName, boolean recursive, Class<T> interfaceClass, List<T> instances) {
        File directory = new File(resource.getFile());
        logger.info("Scanning directory: {}", directory.getAbsolutePath());
        scanDirectory(directory, packageName, recursive, interfaceClass, instances);
    }
    /**
     * 递归扫描目录。
     * @param directory      目录
     * @param packageName    包名
     * @param recursive      是否递归
     * @param interfaceClass 接口类
     * @param instances      实例列表
     * @param <T>           接口类型
     */
    private static <T> void scanDirectory(File directory, String packageName, boolean recursive, Class<T> interfaceClass, List<T> instances) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (recursive) {
                        logger.info("Entering subdirectory: {}", file.getAbsolutePath());
                        scanDirectory(file, packageName + "." + file.getName(), recursive, interfaceClass, instances);
                    }
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (interfaceClass.isAssignableFrom(clazz) && !interfaceClass.equals(clazz)) {
                            instances.add(interfaceClass.cast(clazz.getDeclaredConstructor().newInstance()));
                            logger.info("Found instance: {}", clazz.getName());
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException | InvocationTargetException e) {
                        logger.error("Error processing class: {} - {}", className, e.getMessage());
                    }
                }
            }
        }
    }
}