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
package org.ops4j.pax.tinybundles;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Builder for TinyBundles. The builder is used by {@link TinyBundle} internally.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
@ProviderType
public interface Builder {

    /**
     * Builds the bundle with given resources and headers.
     *
     * @param resources the resources to be considered in the build
     * @param headers   the headers to be considered in the build
     * @return the built assembly (bundle or jar)
     */
    @NotNull
    InputStream build(@NotNull final Map<String, URL> resources, @NotNull final Map<String, String> headers);

}
