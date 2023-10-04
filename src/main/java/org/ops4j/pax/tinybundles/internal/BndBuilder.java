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

import java.io.Closeable;
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
        final CloseAwarePipedInputStream pin = new CloseAwarePipedInputStream();
        try (PipedOutputStream pout = new PipedOutputStream(pin)) {
            new Thread(() -> build(resources, headers, pout, pin)).start();
            // creating the jar from pre-built stream instead of putting resources one by one
            // allows finer control over added resources
            final Jar jar = buildJar(pin, headers);
            return write(jar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Jar buildJar(final InputStream inputStream, final Map<String, String> headers) throws Exception {
        final Jar jar = new Jar("tiny bundle", inputStream);
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
        return jar;
    }

    /**
     * Creates a piped input stream for the wrapped jar.
     *
     * @param jar the wrapped jar
     * @return an input stream for the wrapped jar
     */
    private PipedInputStream write(final Jar jar) throws IOException {
        final CloseAwarePipedInputStream pin = new CloseAwarePipedInputStream();
        final PipedOutputStream pout = new PipedOutputStream(pin);

        new Thread(() -> {
            try {
                jar.write(pout);
            } catch (Exception e) {
                handleBuildException(e, pin);
            } finally {
                close(jar, pout);
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

    private void close(final Closeable... closeables) {
        for (final Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

}
