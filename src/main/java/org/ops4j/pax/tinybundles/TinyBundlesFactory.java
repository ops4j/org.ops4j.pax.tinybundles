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
package org.ops4j.pax.tinybundles;

import java.io.InputStream;

import org.jetbrains.annotations.NotNull;
import org.ops4j.store.Store;
import org.osgi.annotation.versioning.ProviderType;

/**
 * TinyBundles factory (OSGi) service.
 */
@ProviderType
public interface TinyBundlesFactory {

    /**
     * Creates a new {@link TinyBundle}.
     *
     * @return the new tiny bundle
     */
    @NotNull
    TinyBundle bundle();

    /**
     * Creates a new {@link TinyBundle}.
     *
     * @param store the cache backend to use
     * @return the new tiny bundle
     */
    @NotNull
    TinyBundle bundle(@NotNull final Store<InputStream> store);

    /**
     * Creates a new bnd builder.
     *
     * @return the new bnd builder
     */
    @NotNull
    Builder bndBuilder();

    /**
     * Creates a new raw builder.
     *
     * @return the new raw builder
     */
    @NotNull
    Builder rawBuilder();

}
