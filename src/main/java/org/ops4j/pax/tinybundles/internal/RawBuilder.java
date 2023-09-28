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

import org.jetbrains.annotations.NotNull;
import org.ops4j.pax.tinybundles.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawBuilder extends AbstractBuilder implements Builder {

    private final Logger logger = LoggerFactory.getLogger(RawBuilder.class);

    @Override
    @NotNull
    public InputStream build(@NotNull final Map<String, URL> resources, @NotNull final Map<String, String> headers) {
        logger.debug("building...");
        try {
            final PipedInputStream pin = new PipedInputStream();
            final PipedOutputStream pout = new PipedOutputStream(pin);
            new Thread(() -> build(resources, headers, pout)).start();
            return pin;
        } catch (IOException e) {
            throw new RuntimeException("Error opening pipe.", e);
        }
    }

}
