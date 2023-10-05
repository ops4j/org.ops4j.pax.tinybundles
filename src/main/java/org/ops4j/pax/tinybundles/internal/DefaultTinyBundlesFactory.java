/*
 * Copyright 2023 Oliver Lietz
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
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.ops4j.pax.tinybundles.Builder;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.pax.tinybundles.TinyBundlesFactory;
import org.ops4j.store.Store;
import org.ops4j.store.StoreFactory;

/**
 * The default TinyBundlesFactory service.
 */
public class DefaultTinyBundlesFactory implements TinyBundlesFactory {

    private Store<InputStream> store;

    public DefaultTinyBundlesFactory() { //
    }

    private synchronized Store<InputStream> defaultStore() {
        try {
            if (Objects.isNull(store)) {
                store = StoreFactory.anonymousStore();
            }
            return store;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create store", e);
        }
    }

    @Override
    @NotNull
    public TinyBundle bundle() {
        return new TinyBundleImpl(defaultStore());
    }

    @Override
    @NotNull
    public TinyBundle bundle(@NotNull final Store<InputStream> store) {
        return new TinyBundleImpl(store);
    }

    @Override
    @NotNull
    public Builder rawBuilder() {
        return new RawBuilder();
    }

    @Override
    @NotNull
    public Builder bndBuilder() {
        return new BndBuilder();
    }

}
