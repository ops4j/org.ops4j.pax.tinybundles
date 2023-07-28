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
package org.ops4j.pax.tinybundles.core.intern;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.ops4j.pax.tinybundles.core.BuildStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public abstract class RawBuilder implements BuildStrategy {

    private static Logger LOG = LoggerFactory.getLogger(RawBuilder.class);
    private static final String BUILT_BY = "Built-By";
    private static final String ENTRY_MANIFEST = "META-INF/MANIFEST.MF";

    private static final String TOOL = "Tool";
    private static final String CREATED_BY = "Created-By";

    abstract public InputStream build(Map<String, URL> resources, Map<String, String> headers);

    // This is what implementations need to call.
    protected void build(Map<String, URL> resources, Map<String, String> headers, JarOutputStream jarOut) throws IOException {
        addManifest(headers, jarOut);
        for (Map.Entry<String, URL> entry : resources.entrySet()) {
            addResource(jarOut, entry);
        }
    }

    private void addManifest(Map<String, String> headers, JarOutputStream jarOut) throws IOException {
        JarEntry entry = new JarEntry(ENTRY_MANIFEST);
        jarOut.putNextEntry(entry);
        getManifest(headers.entrySet()).write(jarOut);
        jarOut.closeEntry();
    }

    private void addResource(JarOutputStream jarOut, Map.Entry<String, URL> entryset) throws IOException {
        JarEntry entry = new JarEntry(entryset.getKey());
        LOG.debug("Copying resource " + entry.getName());
        jarOut.putNextEntry(entry);
        try (InputStream inp = entryset.getValue().openStream()) {
            copy(inp, jarOut);
        }
    }

    private void copy(InputStream source, OutputStream sink) throws IOException {
        byte[] buf = new byte[1024];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
        }
    }

    /**
     * The calculated manifest for this build output.
     * Relies on input given.
     *
     * @param headers headers will be merged into resulting manifest instance.
     * @return a fresh manifest instance
     */
    private Manifest getManifest(Set<Map.Entry<String, String>> headers) {
        LOG.debug("Creating manifest from added headers.");
        Manifest man = new Manifest();
        String cre = "pax-tinybundles-" + Info.getPaxTinybundlesVersion();

        man.getMainAttributes().putValue("Manifest-Version", "1.0");
        man.getMainAttributes().putValue(BUILT_BY, System.getProperty("user.name"));
        man.getMainAttributes().putValue(CREATED_BY, cre);
        man.getMainAttributes().putValue(TOOL, cre);
        man.getMainAttributes().putValue("TinybundlesVersion", cre);

        for (Map.Entry<String, String> entry : headers) {
            LOG.debug(entry.getKey() + " = " + entry.getValue());

            man.getMainAttributes().putValue(entry.getKey(), entry.getValue());
        }
        return man;
    }

}
