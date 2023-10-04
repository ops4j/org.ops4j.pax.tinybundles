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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ops4j.pax.tinybundles.Builder;
import org.ops4j.pax.tinybundles.InnerClassStrategy;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.store.Store;
import org.osgi.framework.BundleActivator;
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
            setHeader(k, v);
        }
    }

    private void addContents(final JarInputStream jarIn) throws IOException {
        JarEntry entry;
        while (!Objects.isNull(entry = jarIn.getNextJarEntry())) {
            addResource(entry.getName(), jarIn);
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
                throw new IllegalArgumentException(String.format("Unsupported strategy: %s", strategy));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public TinyBundle readIn(@NotNull final JarInputStream jar, final boolean skipContent) {
        addManifestAttributes(jar);
        try {
            if (!skipContent) {
                addContents(jar);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem reading jar.", e);
        }
        return this;
    }

    @Override
    @NotNull
    public TinyBundle readIn(@NotNull final JarInputStream jar) {
        return readIn(jar, false);
    }

    @Override
    @NotNull
    public TinyBundle addClass(@NotNull final Class<?> clazz) {
        addClass(clazz, InnerClassStrategy.ALL);
        return this;
    }

    @Override
    @NotNull
    public TinyBundle addClass(@NotNull final Class<?> clazz, @NotNull final InnerClassStrategy strategy) {
        final String path = ClassFinder.asResource(clazz);
        final URL resource = clazz.getResource("/" + path);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException(String.format("Class %s not found! (resource: %s)", clazz.getName(), path));
        }
        addResource(path, resource);

        final Collection<ClassDescriptor> innerClasses = findInnerClasses(clazz, strategy);
        for (final ClassDescriptor descriptor : innerClasses) {
            resources.put(descriptor.getResourcePath(), descriptor.getUrl());
        }
        return this;
    }

    @Override
    @NotNull
    public TinyBundle activator(@NotNull final Class<? extends BundleActivator> activator) {
        this.addClass(activator);
        this.setHeader(Constants.BUNDLE_ACTIVATOR, activator.getName());
        return this;
    }

    @Override
    @NotNull
    public TinyBundle symbolicName(@NotNull final String symbolicName) {
        this.setHeader(Constants.BUNDLE_SYMBOLICNAME, symbolicName);
        return this;
    }

    @Override
    @NotNull
    public TinyBundle removeClass(@NotNull final Class<?> clazz) {
        final String name = ClassFinder.asResource(clazz);
        removeResource(name);
        return this;
    }

    @Override
    @NotNull
    public TinyBundle addResource(@NotNull final String path, @NotNull final URL resource) {
        resources.put(path, resource);
        return this;
    }

    @Override
    @NotNull
    public TinyBundle addResource(@NotNull final String path, @NotNull final InputStream resource) {
        try {
            return addResource(path, store.getLocation(store.store(resource)).toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public InputStream build() {
        return bndBuilder().build(resources, headers);
    }

    @Override
    @NotNull
    public InputStream build(@NotNull final Builder builder) {
        return builder.build(resources, headers);
    }

    @Override
    @NotNull
    public TinyBundle setHeader(@NotNull final String name, @NotNull final String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    @NotNull
    public TinyBundle removeResource(@NotNull final String key) {
        resources.remove(key);
        return this;
    }

    @Override
    @NotNull
    public TinyBundle removeHeader(@NotNull final String name) {
        headers.remove(name);
        return this;
    }

    @Override
    @Nullable
    public String getHeader(@NotNull final String name) {
        return headers.get(name);
    }

}
