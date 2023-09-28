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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.jar.Manifest;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class BndBuilder extends AbstractBuilder {

    private final Logger logger = LoggerFactory.getLogger(BndBuilder.class);

    @Override
    @NotNull
    public InputStream build(@NotNull final Map<String, URL> resources, @NotNull final Map<String, String> headers) {
        final PipedInputStream pin = new PipedInputStream();
        try (PipedOutputStream pout = new PipedOutputStream(pin)) {
            // creating the jar from pre-build stream instead of putting resources one by one
            // allows finer control over added resources
            new Thread(() -> build(resources, headers, pout)).start();
            final Jar jar = new Jar("dot", pin);
            jar.setManifest(createManifest(headers.entrySet()));

            final Properties properties = new Properties();
            properties.putAll(headers);
            final Builder builder = new Builder();
            builder.setJar(jar);
            builder.setProperties(properties);
            // throw away already existing headers that we overwrite:
            builder.mergeManifest(jar.getManifest());
            ensureSanitizedSymbolicName(builder);
            final Manifest manifest = builder.calcManifest();
            jar.setManifest(manifest);
            return createInputStream(jar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a piped input stream for the wrapped jar.
     *
     * @param jar the wrapped jar
     * @return an input stream for the wrapped jar
     */
    private PipedInputStream createInputStream(final Jar jar) throws IOException {
        final PipedInputStream pin = new PipedInputStream();
        final PipedOutputStream pout = new PipedOutputStream(pin);

        new Thread(() -> {
            try {
                jar.write(pout);
            } catch (Exception e) {
                //    LOG.warn( "Bundle cannot be generated",e );
            } finally {
                try {
                    pout.close();
                } catch (IOException e) {
                    logger.warn("Close ?", e);
                }
            }
        }).start();

        return pin;
    }

    /**
     * Sanitizes symbolic name and replaces OSGi spec invalid characters with underscore (_).
     *
     * @param analyzer bnd analyzer
     */
    private void ensureSanitizedSymbolicName(final Analyzer analyzer) {
        final String defaultSymbolicName = String.format("BuildByTinyBundles-%s", UUID.randomUUID());
        final String symbolicName = analyzer.getProperty(Constants.BUNDLE_SYMBOLICNAME, defaultSymbolicName);
        final String sanitizedSymbolicName = symbolicName.replaceAll("[^a-zA-Z_0-9.-]", "_");
        analyzer.setProperty(Constants.BUNDLE_SYMBOLICNAME, sanitizedSymbolicName);
    }

}
