/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.common.extension;

import java.lang.reflect.Constructor;
import org.dromara.hodor.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Extension loader.
 * This is done by loading the properties file.
 * https://github.com/apache/dubbo/blob/master/dubbo-common/src/main/java/org/apache/dubbo/common/extension/ExtensionLoader.java
 *
 * @param <T> the type parameter.
 * @author xiaoyu(Myth)
 */
@SuppressWarnings("all")
public class ExtensionLoader<T> {
    private Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String EXTENSION_DIRECTORY = "META-INF/hodor/";

    private static final Map<Class<?>, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<T> clazz;

    private final Class<?> argClass;

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final Holder<Map<String, String>> cachedClassesMap = new Holder<>();

    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> joinInstances = new ConcurrentHashMap<>();

    private String cachedDefaultName;

    /**
     * Instantiates a new Extension loader.
     *
     * @param clazz the clazz.
     */
    private ExtensionLoader(Class<T> clazz, Class<?> argClass) {
        this.clazz = clazz;
        this.argClass = argClass;
        if (clazz != ExtensionFactory.class) {
            ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getExtensionClasses();
        }
    }

    /**
     * Gets extension loader.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the extension loader.
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz, Class<?> argClass) {
        if (clazz == null) {
            throw new NullPointerException("extension clazz is null");
        }
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("extension clazz (" + clazz + "is not interface!");
        }
        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("extension clazz (" + clazz + " without @" + SPI.class + " Annotation");
        }
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) LOADERS.get(clazz);
        if (extensionLoader != null) {
            return extensionLoader;
        }
        LOADERS.putIfAbsent(clazz, new ExtensionLoader<>(clazz, argClass));
        return (ExtensionLoader<T>) LOADERS.get(clazz);
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        return getExtensionLoader(clazz, null);
    }

    /**
     * Gets default join.
     *
     * @return the default join.
     */
    public T getDefaultJoin() {
        getExtensionClasses();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }
        return getJoin(cachedDefaultName);
    }

    /**
     * Gets join.
     *
     * @param name the name
     * @return the join.
     */
    @SuppressWarnings("unchecked")
    public T getJoin(String name) {
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("get join name is null");
        }
        try {
            Holder<Object> objectHolder = cachedInstances.get(name);
            if (objectHolder == null) {
                cachedInstances.putIfAbsent(name, new Holder<>());
                objectHolder = cachedInstances.get(name);
            }
            Object value = objectHolder.getValue();
            if (value == null) {
                synchronized (cachedInstances) {
                    value = objectHolder.getValue();
                    if (value == null) {
                        value = createExtension(name);
                        objectHolder.setValue(value);
                    }
                }
            }
            return (T) value;
        } catch (Exception e) {
            throw new IllegalArgumentException("According to the name [" + name + "] Can't find class");
        }
    }

    public T getProtoJoin(String name, Object...args) {
        Class<?> aClass = getExtensionClasses().get(name);
        if (aClass == null) {
            throw new IllegalArgumentException("name is error");
        }
        try {
            if (argClass != null && getConstructor(aClass, argClass) != null) {
                Constructor<?> constructor = getConstructor(aClass, argClass);
                return (T) constructor.newInstance(args);
            }
            Object instance = aClass.newInstance();
            return (T) instance;
        } catch (Exception e) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: " +
                aClass + ")  could not be instantiated: " + e.getMessage(), e);
        }
    }

    private static Constructor<?> getConstructor(Class<?> c, Class<?>... args) {
        try {
            Constructor<?> cons = c.getConstructor(args);
            return cons;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> aClass = getExtensionClasses().get(name);
        if (aClass == null) {
            throw new IllegalArgumentException("name is error");
        }
        Object o = joinInstances.get(aClass);
        if (o == null) {
            try {
                joinInstances.putIfAbsent(aClass, aClass.newInstance());
                o = joinInstances.get(aClass);
            } catch (Exception e) {
                throw new IllegalStateException("Extension instance(name: " + name + ", class: " +
                    aClass + ")  could not be instantiated: " + e.getMessage(), e);
            }
        }
        return (T) o;
    }

    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.getValue();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (classes == null) {
                    classes = loadExtensionClass();
                    cachedClasses.setValue(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClass() {
        SPI annotation = clazz.getAnnotation(SPI.class);
        if (annotation != null) {
            String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                cachedDefaultName = value;
            }
        }
        Map<String, Class<?>> classes = new HashMap<>(16);
        loadDirectory(classes);
        return classes;
    }

    /**
     * Load files under HODOR_DIRECTORY.
     */
    private void loadDirectory(Map<String, Class<?>> classes) {
        String fileName = EXTENSION_DIRECTORY + clazz.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader != null ? classLoader.getResources(fileName)
                : ClassLoader.getSystemResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadResources(classes, url);
                }
            }
        } catch (Throwable t) {
            logger.error("load extension class error {}", fileName, t);
        }
    }

    private void loadResources(Map<String, Class<?>> classes, URL url) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = url.openStream()) {
            properties.load(inputStream);
            properties.forEach((k, v) -> {
                String name = (String) k, classPath = (String) v;
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classPath)) {
                    try {
                        loadClass(classes, name, classPath);
                    } catch (Exception e) {
                        throw new IllegalStateException("load extension resources error", e);
                    }
                }

            });
        } catch (IOException e) {
            throw new IllegalStateException("load extension resources error", e);
        }
    }

    private void loadClass(Map<String, Class<?>> classes,
                           String name, String classPath) throws ClassNotFoundException {
        Class<?> subClass = Class.forName(classPath);
        if (!clazz.isAssignableFrom(subClass)) {
            throw new IllegalStateException("load extension resources error," + subClass + " subtype is not of " + clazz);
        }
        Join annotation = subClass.getAnnotation(Join.class);
        if (annotation == null) {
            throw new IllegalStateException("load extension resources error," + subClass + "with Join annotation");
        }
        Class<?> oldClass = classes.get(name);
        if (oldClass == null) {
            classes.put(name, subClass);
        } else if (oldClass != subClass) {
            throw new IllegalStateException("load extension resources error,Duplicate class " + clazz.getName() + "name " + name + " on " + oldClass.getName() + " or" + subClass.getName());
        }
    }

    /**
     * The type Holder.
     *
     * @param <T> the type parameter.
     */
    public static class Holder<T> {
        private volatile T value;

        /**
         * Gets value.
         *
         * @return the value
         */
        public T getValue() {
            return value;
        }

        /**
         * Sets value.
         *
         * @param value the value
         */
        public void setValue(T value) {
            this.value = value;
        }
    }
}
