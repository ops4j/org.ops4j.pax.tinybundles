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

import java.io.IOException;
import java.io.InputStream;

import org.ops4j.pax.tinybundles.internal.AsyncRawBuilder;
import org.ops4j.pax.tinybundles.internal.BndBuilder;
import org.ops4j.pax.tinybundles.internal.TinyBundleImpl;
import org.ops4j.store.Store;
import org.ops4j.store.StoreFactory;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Statically usable Tinybundles API.
 * Usually the entry point for using Tinybundles.
 *
 * @author Toni Menzel
 * @since 1.0.0
 */
@ProviderType
public class TinyBundles {

    public final static Builder ASYNC_RAW_BUILDER = new AsyncRawBuilder();

    private static Store<InputStream> m_store;

    /**
     * {@see #bundle(BuildableBundle, org.ops4j.store.Store)}
     *
     * @return a new instance of {@link TinyBundle}.
     */
    public static TinyBundle bundle() {
        return bundle(getDefaultStore());
    }

    /**
     * Start with a fresh bundle with this factory method.
     * You can then chain method calls thanks to the humane nature of {@link TinyBundle} interface.
     *
     * @param store cache backend
     * @return a new instance of {@link TinyBundle}.
     */
    public static TinyBundle bundle(Store<InputStream> store) {
        return new TinyBundleImpl(store);
    }

    /**
     * @param inner builder when using bnd builder.
     * @return a builder to be used with {@link TinyBundle#build(Builder)} using BND with underlying (given) builder overwrite.
     */
    public static Builder withBndBuilder(Builder inner) {
        return new BndBuilder(inner);
    }

    /**
     * @return a builder to be used with {@link TinyBundle#build(Builder)} using BND with default builder.
     */
    public static Builder withBndBuilder() {
        return new BndBuilder(withRawBuilder());
    }

    /**
     * @return a builder to be used with {@link TinyBundle#build(Builder)} using no extra manifest computation logic.
     */
    public static Builder withRawBuilder() {
        return ASYNC_RAW_BUILDER;
    }

    /**
     * Access to the default store instance. (this is low level. Don't bother).
     * The default store is unique per VM.
     *
     * @return store instance that is used when user does not give its own Store instance upon {@link #bundle()}
     */
    public static synchronized Store<InputStream> getDefaultStore() {
        try {
            if (m_store == null) {
                m_store = StoreFactory.anonymousStore();
            }
            return m_store;
        } catch (IOException e) {
            throw new RuntimeException("Error creating Store", e);
        }
    }

}
