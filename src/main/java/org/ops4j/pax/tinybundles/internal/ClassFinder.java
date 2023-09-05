/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.tinybundles.internal;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 */
public class ClassFinder {

    private final Logger logger = LoggerFactory.getLogger(ClassFinder.class);

    public Collection<ClassDescriptor> findAllEmbeddedClasses(final Class<?> clazz) throws IOException {
        final String resourcePrefix = clazz.getName().replace('.', '/') + "\\$.*";
        return findAllEmbeddedClasses(clazz, resourcePrefix);
    }

    public Collection<ClassDescriptor> findAnonymousClasses(final Class<?> clazz) throws IOException {
        final String resourcePrefix = clazz.getName().replace('.', '/') + "\\$\\d.*";
        return findAllEmbeddedClasses(clazz, resourcePrefix);
    }

    @SuppressWarnings("unchecked")
    public Collection<ClassDescriptor> findAllEmbeddedClasses(final Class<?> clazz, final String pattern) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        if (Objects.isNull(classLoader)) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        final URL classUrl = classLoader.getResource(asResource(clazz));
        logger.debug("Finding all embedded classes for class '{}' with pattern '{}'", classUrl, pattern);
        switch (classUrl.getProtocol()) {
            case "jar":
                String jarPath = classUrl.getFile();
                final int bang = jarPath.indexOf("!");
                if (bang > -1) {
                    jarPath = jarPath.substring(0, bang);
                }
                final URL url = new URL(String.format("jar:%s!/", jarPath));
                return findEmbeddedClasses(url, pattern);
            case "file":
                final File classFile;
                try {
                    classFile = new File(classUrl.toURI());
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
                return findEmbeddedClasses(classFile, pattern);
            case "jrt":
                return findEmbeddedClassesInJavaModule(classUrl, pattern);
            case "bundle":
            case "bundleresource":
                final Bundle bundle = FrameworkUtil.getBundle(clazz);
                if (!Objects.isNull(bundle)) {
                    final String path = clazz.getPackage().getName().replace('.', '/');
                    final String filePattern = String.format("%s$*", clazz.getSimpleName());
                    final Enumeration<URL> urls = bundle.findEntries(path, filePattern, false);
                    if (!Objects.isNull(urls)) {
                        return findEmbeddedClasses(urls, pattern);
                    } else {
                        return new ArrayList<>();
                    }
                } else {
                    throw new IllegalArgumentException(String.format("No bundle found for class %s. Unsupported woven or system package class.", clazz));
                }
            default:
                throw new IllegalStateException(String.format("Unsupported protocol %s", classUrl.getProtocol()));
        }
    }

    public List<ClassDescriptor> findEmbeddedClasses(final Enumeration<URL> urls, final String pattern) {
        final String filePattern = String.format("/%s", pattern);
        final List<ClassDescriptor> descriptors = new ArrayList<>();
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            if (url.getFile().matches(filePattern)) {
                final ClassDescriptor descriptor = new ClassDescriptor(url.getFile(), url);
                logger.debug("Adding {}", descriptor);
                descriptors.add(descriptor);
            }
        }
        return descriptors;
    }

    public List<ClassDescriptor> findEmbeddedClasses(final File file, final String pattern) throws MalformedURLException {
        final List<ClassDescriptor> descriptors = new ArrayList<>();
        final File dir = file.getParentFile();
        if (Objects.nonNull(dir)) {
            final int slash = pattern.lastIndexOf('/');
            final String path = pattern.substring(0, slash + 1);
            final String filePattern = pattern.substring(slash + 1);
            for (final File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.getName().matches(filePattern)) {
                    final ClassDescriptor descriptor = new ClassDescriptor(path.concat(f.getName()), f.toURI().toURL());
                    logger.debug("Adding {}", descriptor);
                    descriptors.add(descriptor);
                }
            }
        }
        return descriptors;
    }

    public List<ClassDescriptor> findEmbeddedClasses(final URL jarUrl, final String pattern) throws IOException {
        final JarURLConnection connection = (JarURLConnection) jarUrl.openConnection();
        final JarFile jarFile = connection.getJarFile();
        final List<ClassDescriptor> descriptors = new ArrayList<>();
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            if (entry.getName().matches(pattern)) {
                final String entryUrl = jarUrl.toExternalForm() + entry.getName();
                final ClassDescriptor descriptor = new ClassDescriptor(entry.getName(), new URL(entryUrl));
                logger.debug("Adding {}", descriptor);
                descriptors.add(descriptor);
            }
        }
        jarFile.close();
        return descriptors;
    }

    public List<ClassDescriptor> findEmbeddedClassesInJavaModule(final URL classUrl, final String pattern) throws IOException {
        final List<ClassDescriptor> descriptors = new ArrayList<>();
        final String classPath = classUrl.getPath();
        final String packagePath = classPath.substring(0, classPath.lastIndexOf("/"));
        final String module = classPath.substring(0, classPath.indexOf("/", 1)).substring(1);
        final String jrtPattern = String.format("/modules/%s/%s", module, pattern);
        final FileSystem fileSystem = FileSystems.getFileSystem(URI.create("jrt:/"));
        try (Stream<Path> stream = Files.list(fileSystem.getPath("/modules", packagePath))) {
            stream.forEach(path -> {
                try {
                    if (path.toString().matches(jrtPattern)) {
                        final String resourcePath = removeJrtModulePrefix(path, module);
                        final URL url = sanitizeJrtUrl(path.toUri().toURL());
                        final ClassDescriptor descriptor = new ClassDescriptor(resourcePath, url);
                        logger.debug("Adding {}", descriptor);
                        descriptors.add(descriptor);
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return descriptors;
    }

    private static String removeJrtModulePrefix(final Path path, final String module) {
        return path.toString().replaceFirst(String.format("/modules/%s/", module), "");
    }

    // Fix for bug in JDK 9, 10, 11 and 12. https://bugs.openjdk.org/browse/JDK-8227076
    private static URL sanitizeJrtUrl(final URL url) throws MalformedURLException {
        if (url.toString().startsWith("jrt:/modules/")) {
            return new URL(url.toString().replaceFirst("jrt:/modules/", "jrt:/"));
        } else {
            return url;
        }
    }

    public static String asResource(final Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }

}
