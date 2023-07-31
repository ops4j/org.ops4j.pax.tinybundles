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
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.ops4j.pax.tinybundles.BuildStrategy;
import org.ops4j.pax.tinybundles.InnerClassStrategy;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.store.Store;
import org.osgi.framework.Constants;

import static org.ops4j.pax.tinybundles.TinyBundles.withClassicBuilder;

/**
 * Our default implementation of TinyBundle.
 * An instance should be retrieved via DefaultTinybundleProvider.newBundle() factory method.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class TinyBundleImpl implements TinyBundle {

    private Map<String, URL> m_resources = new HashMap<String, URL>();
    private Map<String, String> m_headers = new HashMap<String, String>();

    final private Store<InputStream> m_store;

    public TinyBundleImpl(Store<InputStream> bstore) {
        m_store = bstore;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle read(InputStream in, boolean readData) {
        if (in != null) {
            try (JarInputStream jarIn = new JarInputStream(in)) {
                addManifestAttribs(jarIn);
                if (readData) {
                    addContents(jarIn);
                }
            } catch (IOException e) {
                throw new RuntimeException("Problem loading bundle.", e);
            }
        }
        return this;
    }

    private void addManifestAttribs(JarInputStream jarIn) {
        // TODO: reading out just main headers will remove the other parts. Fix this with
        // TODO change m_headers to type Manifest natively.
        Manifest manifest = jarIn.getManifest();
        if (manifest == null) {
            return;
        }
        Attributes att = manifest.getMainAttributes();
        for (Object o : att.keySet()) {
            String k = o.toString();
            String v = att.getValue(k);
            set(k, v);
        }
    }

    private void addContents(JarInputStream jarIn) throws IOException {
        JarEntry entry = null;
        while ((entry = jarIn.getNextJarEntry()) != null) {
            add(entry.getName(), jarIn);
        }
    }

    public TinyBundle read(InputStream in) {
        return read(in, true);
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle add(Class<?> clazz) {
        add(clazz, InnerClassStrategy.ALL);
        return this;
    }

    public TinyBundle add(Class<?> clazz, InnerClassStrategy strategy) {
        String name = ClassFinder.asResource(clazz);
        URL resource = clazz.getResource("/" + name);

        if (resource == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " not found! (resource: " + name + " )");
        }
        add(name, resource);

        Collection<ClassDescriptor> innerClasses = findInnerClasses(clazz, strategy);
        for (ClassDescriptor descriptor : innerClasses) {
            m_resources.put(descriptor.getResourcePath(), descriptor.getUrl());
        }
        return this;
    }

    private Collection<ClassDescriptor> findInnerClasses(Class<?> clazz, InnerClassStrategy strategy) {
        try {
            ClassFinder finder = new ClassFinder();
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


    /**
     * {@inheritDoc}
     */
    public TinyBundle activator(Class<?> activator) {
        this.add(activator);
        this.set(Constants.BUNDLE_ACTIVATOR, activator.getName());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle symbolicName(String name) {
        this.set(Constants.BUNDLE_SYMBOLICNAME, name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle remove(Class<?> content) {
        String name = ClassFinder.asResource(content);
        removeResource(name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle add(String name, URL url) {
        m_resources.put(name, url);
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle add(String name, InputStream content) {
        try {
            return add(name, m_store.getLocation(m_store.store(content)).toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @{@inheritDoc}
     */
    public InputStream build() {
        return withClassicBuilder().build(m_resources, m_headers);
    }

    /**
     * @{@inheritDoc}
     */
    public InputStream build(BuildStrategy builder) {
        return builder.build(m_resources, m_headers);
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle set(String key, String value) {
        m_headers.put(key, value);
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle removeResource(String key) {
        m_resources.remove(key);
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle removeHeader(String key) {
        m_headers.remove(key);
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public String getHeader(String key) {
        return m_headers.get(key);
    }

}
