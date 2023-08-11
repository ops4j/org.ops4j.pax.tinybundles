/*
 * Copyright 2013 Harald Wellmann
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

import java.net.URL;

/**
 * @author Harald Wellmann
 */
public class ClassDescriptor {

    private final String resourcePath;

    private final URL url;

    public ClassDescriptor(final String resourcePath, final URL url) {
        this.resourcePath = resourcePath;
        this.url = url;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("ClassDescriptor{resourcePath='%s', url='%s'}", resourcePath, url);
    }

}
