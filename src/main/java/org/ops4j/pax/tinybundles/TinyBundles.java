/*
 * Copyright 2011 Toni Menzel. OPS4J.org
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
package org.ops4j.pax.tinybundles;

import java.io.InputStream;
import java.util.ServiceLoader;

import org.ops4j.store.Store;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * Statically usable TinyBundles factory.
 *
 * @author Toni Menzel
 * @since 1.0.0
 */
@ProviderType
public class TinyBundles {

    private TinyBundles() { //
    }

    private static TinyBundlesFactory factory() {
        try {
            return ServiceLoader.load(TinyBundlesFactory.class).iterator().next();
        } catch (Exception e) { //
        }
        try {
            final Bundle bundle = FrameworkUtil.getBundle(TinyBundles.class.getClassLoader()).get();
            final ServiceReference<TinyBundlesFactory> serviceReference = bundle.getBundleContext().getServiceReference(TinyBundlesFactory.class);
            return bundle.getBundleContext().getService(serviceReference);
        } catch (Exception e) { //
        }
        throw new IllegalStateException("Unable to get an instance of " + TinyBundlesFactory.class.getName());
    }

    /**
     * Creates a new {@link TinyBundle}.
     *
     * @return the new tiny bundle
     */
    public static TinyBundle bundle() {
        return factory().bundle();
    }

    /**
     * Creates a new {@link TinyBundle}.
     *
     * @param store the cache backend to use
     * @return the new tiny bundle
     */
    public static TinyBundle bundle(final Store<InputStream> store) {
        return factory().bundle(store);
    }

    /**
     * Creates a new bnd builder.
     *
     * @return the new bnd builder
     */
    public static Builder bndBuilder() {
        return factory().bndBuilder();
    }

    /**
     * Creates a new raw builder.
     *
     * @return the new raw builder
     */
    public static Builder rawBuilder() {
        return factory().rawBuilder();
    }

}
