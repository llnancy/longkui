/*
 * Copyright 2023 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.extension;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.llnancy.longkui.core.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * SPI ExtensionLoader
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/14
 */
@Slf4j
public class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = Maps.newConcurrentMap();

    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = Maps.newConcurrentMap();

    private final Class<?> type;

    private final ConcurrentMap<String, Holder<Object>> cachedInstances = Maps.newConcurrentMap();

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * get ExtensionLoader by type
     *
     * @param type Class
     * @param <T>  T
     * @return ExtensionLoader
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        Preconditions.checkNotNull(type, "Extension type == null");
        Preconditions.checkArgument(type.isInterface(), "Extension type (" + type + ") is not an interface!");
        Preconditions.checkArgument(
                Objects.nonNull(type.getAnnotation(SPI.class)),
                "Extension type (" + type + ") is not an extension, because it is NOT annotated with @"
                        + SPI.class.getSimpleName() + "!"
        );
        // find in local cache
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (Objects.isNull(loader)) {
            // can invoke computeIfAbsent?
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    /**
     * Get extension's instance. Return <code>null</code> if extension is not found or is not initialized. Pls. note
     * that this method will not trigger extension load.
     * <p>
     * In order to trigger extension load, call {@link #getExtension(String)} instead.
     *
     * @param name extension name
     * @return Extension instance
     * @see #getExtension(String)
     */
    @SuppressWarnings("unchecked")
    public T getLoadedExtension(String name) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "Extension name == null");
        Holder<Object> holder = cachedInstances.get(name);
        if (Objects.isNull(holder)) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return (T) holder.get();
    }

    public Set<String> getSupportedExtensions() {
        Map<String, Class<?>> classes = getExtensionClasses();
        return Collections.unmodifiableSet(Sets.newTreeSet(classes.keySet()));
    }

    /**
     * Return the list of extensions which are already loaded.
     * <p>
     * Usually {@link #getSupportedExtensions()} should be called in order to get all extensions.
     *
     * @return Set of loaded extension instance.
     * @see #getSupportedExtensions()
     */
    public Set<String> getLoadedExtensions() {
        return Collections.unmodifiableSet(Sets.newTreeSet(cachedInstances.keySet()));
    }

    public T getExtension(Enum<?> em) {
        return getExtension(em.name().replaceAll(Constants.UNDERLINE, Constants.EMPTY).toLowerCase());
    }

    /**
     * Find the extension with the given name. If the specified name is not found, then {@link IllegalStateException}
     * will be thrown.
     *
     * @param name extension name
     * @return extension instance
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "Extension name == null");
        Holder<Object> holder = cachedInstances.get(name);
        if (Objects.isNull(holder)) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        if (Objects.isNull(instance)) {
            synchronized (holder) {
                instance = holder.get();
                if (Objects.isNull(instance)) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * create extension instance by name
     *
     * @param name extension name
     * @return extension instance
     */
    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        Preconditions.checkNotNull(clazz, "No such extension " + type.getName() + " by name " + name);
        try {
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (Objects.isNull(instance)) {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                Object obj = constructor.newInstance();
                EXTENSION_INSTANCES.putIfAbsent(clazz, obj);
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            // no inject
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: " + type + ") could not be instantiated: " + t.getMessage(), t);
        }
    }

    /**
     * get loaded extensions class map
     *
     * @return k: extension name; v: extension class
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (Objects.isNull(classes)) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (Objects.isNull(classes)) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        Map<String, Class<?>> extensionClasses = Maps.newHashMap();
        loadDirectory(extensionClasses, SERVICE_DIRECTORY);
        return extensionClasses;
    }

    /**
     * load directory
     *
     * @param extensionClasses extension class map
     * @param dir              directory
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir) {
        String fileName = dir + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            if (Objects.nonNull(classLoader)) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (Objects.nonNull(urls)) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Exception when load extension class(interface: " + type + ", description file: " + fileName + ").", t);
        }
    }

    /**
     * load resource
     *
     * @param extensionClasses extension class map
     * @param classLoader      ClassLoader
     * @param resourceUrl      URL
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                // 忽略#后面的注释
                int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    int i = line.indexOf('=');
                    String name = null;
                    if (i > 0) {
                        name = line.substring(0, i).trim();
                        line = line.substring(i + 1).trim();
                    }
                    if (StringUtils.isNotBlank(line)) {
                        loadClass(extensionClasses, Class.forName(line, true, classLoader), name);
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Exception when load extension class(interface: " + type + ", class file: " + resourceUrl + ") in " + resourceUrl, t);
        }
    }

    /**
     * load class
     *
     * @param extensionClasses extension class map
     * @param clazz            extension class
     * @param name             extension name
     */
    private void loadClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name) {
        String clazzName = clazz.getName();
        Preconditions.checkState(type.isAssignableFrom(clazz), "Error when load extension class(interface: " + type + ", class line: " + clazzName + "), class " + clazzName + " is not subtype of interface.");
        Class<?> c = extensionClasses.get(name);
        if (Objects.isNull(c)) {
            extensionClasses.put(name, clazz);
        } else if (c != clazz) {
            throw new IllegalStateException("Duplicate extension " + type.getName() + " name " + name + " on " + c.getName() + " and " + clazzName);
        }
    }
}
