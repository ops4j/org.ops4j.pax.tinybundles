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
package org.ops4j.pax.tinybundles.core;

import java.io.InputStream;
import org.ops4j.pax.tinybundles.core.intern.AsyncRawBuilder;
import org.ops4j.pax.tinybundles.core.intern.BndBuilder;
import org.ops4j.pax.tinybundles.core.intern.SynchronousRawBuilder;
import org.ops4j.pax.tinybundles.core.intern.TinyBundleImpl;
import org.ops4j.store.Store;
import org.ops4j.store.StoreFactory;

/**
 * Statically usable Tinybundles API.
 * Usually the entry point for using Tinybundles.
 *
 * @author Toni Menzel
 * @since 1.0.0
 */
public class TinyBundles {

    public final static BuildStrategy STRATEGY_ASYNC = new AsyncRawBuilder();
    public final static BuildStrategy STRATEGY_SYNCHRONUOUS = new SynchronousRawBuilder();

    private static final Store<InputStream> m_store = StoreFactory.defaultStore();

    /**
     * {@see #bundle(BuildableBundle, org.ops4j.store.Store)}
     *
     * @return a new instance of {@link TinyBundle}.
     */
    public static TinyBundle bundle()
    {
        return bundle( getDefaultStore() );
    }

    /**
     * Start with a fresh bundle with this factory method.
     * You can then chain methodcalls thanks to the humane nature of {@link TinyBundle} interface.
     *
     * @param store    cache backend
     *
     * @return a new instance of {@link TinyBundle}.
     */
    public static TinyBundle bundle( Store<InputStream> store )
    {
        return new TinyBundleImpl( store );
    }

    /**
     * @param inner builder strategy when using bnd.
     *
     * @return a strategy to be used with {@link TinyBundle#build(BuildStrategy)} using BND with underying (given) strategy overwrite.
     */
    public static BuildStrategy withBnd( BuildStrategy inner )
    {
        return new BndBuilder( inner );
    }

    /**
     * @return a strategy to be used with {@link TinyBundle#build(BuildStrategy)} using BND with default strategy.
     */
    public static BuildStrategy withBnd()
    {
        return new BndBuilder( withClassicBuilder() );
    }

     /**
     * @return a strategy to be used with {@link TinyBundle#build(BuildStrategy)} using no extra manifest computation logic.
     */
    public static BuildStrategy withClassicBuilder()
    {
        return STRATEGY_ASYNC;
    }

    /**
     * Access to the default store instance. (this is low level. Don't bother).
     *
     * @return store instance that is used when user does not give its own Store instance upon {@link #bundle()}
     */
    public static Store<InputStream> getDefaultStore()
    {
        return m_store;
    }
}
