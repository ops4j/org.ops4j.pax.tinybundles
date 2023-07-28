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
package org.ops4j.pax.tinybundles.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Build strategy.
 * Implementors should be stateless so builders can be shared.
 * Usually the "build" action is a atomic, functional operation.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public interface BuildStrategy {

    /**
     * perform the actual build.
     *
     * @param resources resources to be considered in the build.
     * @param headers   headers to be considered in the build
     * @return an {@link InputStream} containing built assembly (usually a jar/bundle in this context)
     */
    InputStream build(Map<String, URL> resources, Map<String, String> headers);

}
