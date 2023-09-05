/*
 * Copyright 2009 Toni Menzel.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.ops4j.pax.tinybundles.Builder;
import org.ops4j.pax.tinybundles.InnerClassStrategy;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.store.Store;
import org.osgi.framework.Constants;

import static org.ops4j.pax.tinybundles.TinyBundles.bndBuilder;

/**
 * Our default implementation of TinyBundle.
 * An instance should be retrieved via TinyBundles.bundle() factory method.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class TinyBundleImpl implements TinyBundle {

    private final Map<String, URL> resources = new HashMap<>();

    private final Map<String, String> headers = new HashMap<>();

    private final Store<InputStream> store;

    public TinyBundleImpl(final Store<InputStream> store) {
        this.store = store;
    }

    private void addManifestAttributes(final JarInputStream jarIn) {
        // TODO: reading out just main headers will remove the other parts. Fix this with
        // TODO change m_headers to type Manifest natively.
        final Manifest manifest = jarIn.getManifest();
        if (Objects.isNull(manifest)) {
            return;
        }
        final Attributes attributes = manifest.getMainAttributes();
        for (final Object key : attributes.keySet()) {
            final String k = key.toString();
            final String v = attributes.getValue(k);
            set(k, v);
        }
    }

    private void addContents(final JarInputStream jarIn) throws IOException {
        JarEntry entry;
        while (!Objects.isNull(entry = jarIn.getNextJarEntry())) {
            add(entry.getName(), jarIn);
        }
    }

    private Collection<ClassDescriptor> findInnerClasses(final Class<?> clazz, final InnerClassStrategy strategy) {
        try {
            final ClassFinder finder = new ClassFinder();
            if (strategy == InnerClassStrategy.NONE) {
                return Collections.emptyList();
            } else if (strategy == InnerClassStrategy.ALL) {
                return finder.findAllEmbeddedClasses(clazz);
            } else if (strategy == InnerClassStrategy.ANONYMOUS) {
                return finder.findAnonymousClasses(clazz);
            } else {
                throw new IllegalArgumentException("Unsupported strategy" + strategy);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TinyBundle read(final InputStream inputStream, final boolean readContent) {
        if (!Objects.isNull(inputStream)) {
            try (JarInputStream jarIn = new JarInputStream(inputStream)) {
                addManifestAttributes(jarIn);
                if (readContent) {
                    addContents(jarIn);
                }
            } catch (IOException e) {
                throw new RuntimeException("Problem loading bundle.", e);
            }
        }
        return this;
    }

    @Override
    public TinyBundle read(final InputStream inputStream) {
        return read(inputStream, true);
    }

    @Override
    public TinyBundle add(final Class<?> clazz) {
        add(clazz, InnerClassStrategy.ALL);
        return this;
    }

    @Override
    public TinyBundle add(final Class<?> clazz, final InnerClassStrategy strategy) {
        final String name = ClassFinder.asResource(clazz);
        final URL resource = clazz.getResource("/" + name);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException(String.format("Class %s not found! (resource: %s )", clazz.getName(), name));
        }
        add(name, resource);

        final Collection<ClassDescriptor> innerClasses = findInnerClasses(clazz, strategy);
        for (final ClassDescriptor descriptor : innerClasses) {
            resources.put(descriptor.getResourcePath(), descriptor.getUrl());
        }
        return this;
    }

    @Override
    public TinyBundle activator(final Class<?> activator) {
        this.add(activator);
        this.set(Constants.BUNDLE_ACTIVATOR, activator.getName());
        return this;
    }

    @Override
    public TinyBundle symbolicName(final String name) {
        this.set(Constants.BUNDLE_SYMBOLICNAME, name);
        return this;
    }

    @Override
    public TinyBundle remove(final Class<?> clazz) {
        final String name = ClassFinder.asResource(clazz);
        removeResource(name);
        return this;
    }

    @Override
    public TinyBundle add(final String name, final URL url) {
        resources.put(name, url);
        return this;
    }

    @Override
    public TinyBundle add(final String name, final InputStream content) {
        try {
            return add(name, store.getLocation(store.store(content)).toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream build() {
        return bndBuilder().build(resources, headers);
    }

    @Override
    public InputStream build(final Builder builder) {
        return builder.build(resources, headers);
    }

    @Override
    public TinyBundle set(final String key, final String value) {
        headers.put(key, value);
        return this;
    }

    @Override
    public TinyBundle removeResource(final String key) {
        resources.remove(key);
        return this;
    }

    @Override
    public TinyBundle removeHeader(final String key) {
        headers.remove(key);
        return this;
    }

    @Override
    public String getHeader(final String key) {
        return headers.get(key);
    }

}
