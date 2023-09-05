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
import aQute.bnd.osgi.Jar;
import org.ops4j.pax.tinybundles.Builder;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class BndBuilder implements Builder {

    private final Logger logger = LoggerFactory.getLogger(BndBuilder.class);

    private final Builder builder;

    public BndBuilder(final Builder builder) {
        this.builder = builder;
    }

    @Override
    public InputStream build(final Map<String, URL> resources, final Map<String, String> headers) {
        return wrapWithBnd(headers, builder.build(resources, headers));
    }

    private InputStream wrapWithBnd(final Map<String, String> headers, final InputStream inputStream) {
        try {
            final Properties instructions = new Properties();
            instructions.putAll(headers);
            return createBundle(inputStream, instructions, String.format("BuildByTinyBundles%s", UUID.randomUUID()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * All the bnd magic happens here.
     *
     * @param inputStream  On what to operate.
     * @param instructions bnd instructions from user API
     * @param symbolicName Mandatory Header. In case user does not set it.
     * @return Bundle Jar Stream
     * @throws Exception Problems go here
     */
    private InputStream createBundle(final InputStream inputStream, final Properties instructions, final String symbolicName) throws Exception {
        final Jar jar = new Jar("dot", inputStream);
        final Properties properties = new Properties();
        properties.putAll(instructions);

        aQute.bnd.osgi.Builder builder = new aQute.bnd.osgi.Builder();
        builder.setJar(jar);
        builder.setProperties(properties);
        // throw away already existing headers that we overwrite:
        builder.mergeManifest(jar.getManifest());
        ensureSanitizedSymbolicName(builder, symbolicName);
        final Manifest manifest = builder.calcManifest();
        jar.setManifest(manifest);

        return createInputStream(jar);
    }

    /**
     * Creates a piped input stream for the wrapped jar.
     * This is done in a thread, so we can return quickly.
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
     * Processes symbolic name and replaces OSGi spec invalid characters with "_".
     *
     * @param analyzer            bnd analyzer
     * @param defaultSymbolicName bundle symbolic name
     */
    private void ensureSanitizedSymbolicName(final Analyzer analyzer, final String defaultSymbolicName) {
        final String symbolicName = analyzer.getProperty(Constants.BUNDLE_SYMBOLICNAME, defaultSymbolicName);
        final String sanitizedSymbolicName = symbolicName.replaceAll("[^a-zA-Z_0-9.-]", "_");
        analyzer.setProperty(Constants.BUNDLE_SYMBOLICNAME, sanitizedSymbolicName);
    }

}
