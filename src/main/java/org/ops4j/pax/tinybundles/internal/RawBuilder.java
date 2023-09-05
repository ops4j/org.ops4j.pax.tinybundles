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
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.ops4j.pax.tinybundles.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public abstract class RawBuilder implements Builder {

    private final Logger logger = LoggerFactory.getLogger(RawBuilder.class);

    private static final String BUILT_BY = "Built-By";

    private static final String ENTRY_MANIFEST = "META-INF/MANIFEST.MF";

    private static final String TOOL = "Tool";

    private static final String CREATED_BY = "Created-By";

    // This is what implementations need to call.
    protected void build(final Map<String, URL> resources, final Map<String, String> headers, final JarOutputStream jarOut) throws IOException {
        addManifest(headers, jarOut);
        for (final Map.Entry<String, URL> entry : resources.entrySet()) {
            addResource(jarOut, entry);
        }
    }

    private void addManifest(final Map<String, String> headers, final JarOutputStream jarOut) throws IOException {
        final JarEntry entry = new JarEntry(ENTRY_MANIFEST);
        jarOut.putNextEntry(entry);
        getManifest(headers.entrySet()).write(jarOut);
        jarOut.closeEntry();
    }

    private void addResource(final JarOutputStream jarOut, final Map.Entry<String, URL> entrySet) throws IOException {
        final JarEntry entry = new JarEntry(entrySet.getKey());
        logger.debug("Copying resource {}, {}", entry.getName(), entrySet.getValue());
        jarOut.putNextEntry(entry);
        try (InputStream inputStream = entrySet.getValue().openStream()) {
            copy(inputStream, jarOut);
        }
    }

    private void copy(final InputStream source, final OutputStream sink) throws IOException {
        final byte[] buffer = new byte[1024];
        int n;
        while ((n = source.read(buffer)) > 0) {
            sink.write(buffer, 0, n);
        }
    }

    /**
     * The calculated manifest for this build output.
     * Relies on input given.
     *
     * @param headers headers will be merged into resulting manifest instance.
     * @return a fresh manifest instance
     */
    private Manifest getManifest(final Set<Map.Entry<String, String>> headers) {
        logger.debug("Creating manifest from added headers.");
        final Manifest manifest = new Manifest();
        final String tool = "pax-tinybundles-" + Info.getPaxTinybundlesVersion();

        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue(BUILT_BY, System.getProperty("user.name"));
        manifest.getMainAttributes().putValue(CREATED_BY, tool);
        manifest.getMainAttributes().putValue(TOOL, tool);
        manifest.getMainAttributes().putValue("TinybundlesVersion", tool);

        for (final Map.Entry<String, String> entry : headers) {
            logger.debug("{} = {}", entry.getKey(), entry.getValue());
            manifest.getMainAttributes().putValue(entry.getKey(), entry.getValue());
        }
        return manifest;
    }

}
