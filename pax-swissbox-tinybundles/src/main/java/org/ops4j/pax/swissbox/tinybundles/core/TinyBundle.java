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
package org.ops4j.pax.swissbox.tinybundles.core;

import java.net.URL;
import java.io.InputStream;

/**
 * Main type when making bundles with the {@link TinyBundles} library.
 * Get an instance from {@link TinyBundles} Factory, add resources and call "prepare" to go to the next finalization step.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public interface TinyBundle
{

    /**
     * Add a resource to the current bundle (to be built).
     *
     * @param name    final path inside the jar
     * @param content content to be copied into bundle.
     *
     * @return *this*
     */
    TinyBundle add( String name, URL content );

    /**
     * Add a resource to the current bundle (to be built).
     *
     * @param name    final path inside the jar
     * @param content content to be copied into bundle.
     *
     * @return *this*
     */
    TinyBundle add( String name, InputStream content );

    /**
     * Add a class to the current bundle.
     *
     * @param content content to be copied into bundle.
     *
     * @return *this*
     */
    TinyBundle add( Class content );

    /**
     * When you are done adding stuff to *this* you can call this method to go to next step.
     *
     * @param builder Instance may be retrieved from {TinyBundles} factory methods.
     *
     * @return Next step in the bundle making process.
     */
    BuildableBundle prepare( BuildableBundle builder );

    /**
     * When you are done adding stuff to *this* you can call this method to go to next step.
     * The BND based builder will be used for your convenience.
     *
     * @return Next step in the bundle making process.
     */
    BuildableBundle prepare();

}
