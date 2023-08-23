/*
 * Copyright 2011 Toni Menzel.
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
import java.util.jar.JarOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Async Builder that uses PipedOutputStream. Bundle is not built until it gets flushed.
 */
public class AsyncRawBuilder extends RawBuilder {

    private final Logger logger = LoggerFactory.getLogger(AsyncRawBuilder.class);

    public InputStream build(final Map<String, URL> resources, final Map<String, String> headers) {
        logger.debug("building...");
        try {
            final PipedInputStream pin = new PipedInputStream();
            final PipedOutputStream pout = new PipedOutputStream(pin);
            new Thread(() -> buildFrom(resources, headers, pout)).start();
            return pin;
        } catch (IOException e) {
            throw new RuntimeException("Error opening pipe.", e);
        }
    }

    private void buildFrom(final Map<String, URL> resources, final Map<String, String> headers, final PipedOutputStream pout) {
        try (JarOutputStream jarOut = new JarOutputStream(pout)) {
            build(resources, headers, jarOut);
        } catch (IOException e) {
            if (!"Pipe closed".equals(e.getMessage())) {
                logger.error("Problem while writing jar.", e);
            }
        } finally {
            logger.trace("Copy thread finished.");
        }
    }

}
