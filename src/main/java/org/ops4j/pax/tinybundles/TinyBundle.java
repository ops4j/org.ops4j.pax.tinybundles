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
package org.ops4j.pax.tinybundles;

import java.io.InputStream;
import java.net.URL;

/**
 * Main type when making bundles with the {@link TinyBundles} library.
 * Get an instance from {@link TinyBundles} Factory, add resources and call {@link #build()} to go to the final step.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public interface TinyBundle {

    /**
     * Add a resource to the current bundle (to be built).
     *
     * @param name    final path inside the jar
     * @param content content to be copied into bundle.
     * @return *this*
     */
    TinyBundle add(String name, URL content);

    /**
     * Add a resource to the current bundle (to be built).
     *
     * @param name    final path inside the jar
     * @param content content to be copied into bundle.
     * @return *this*
     */
    TinyBundle add(String name, InputStream content);

    /**
     * Add a class to the current bundle. Uses InnerClassStrategy.ALL
     *
     * @param content content to be copied into bundle.
     * @return {@literal this}
     */
    TinyBundle add(Class<?> content);

    /**
     * Add a class to the current bundle.
     *
     * @param content
     */
    TinyBundle add(Class<?> content, InnerClassStrategy strategy);

    /**
     * Add a class to the current bundle and set it as Activator
     *
     * @param content
     */
    TinyBundle activator(Class<?> activator);

    /**
     * Set symbolic name of bundle
     *
     * @param name
     */
    TinyBundle symbolicName(String name);

    /**
     * remove a class to the current bundle.
     *
     * @param content class to be removed
     * @return *this*
     */
    TinyBundle remove(Class<?> content);

    /**
     * When you are done adding stuff to *this* you can call this method to go to next step.
     * The BND based builder will be used for your convenience.
     *
     * @param builder builder to be used.
     * @return Next step in the bundle making process.
     */
    InputStream build(BuildStrategy builder);

    /**
     * Shortcut to {@link TinyBundle#build(BuildStrategy)} witth buildstrategy = TinyBundles.withClassicBuilder().
     *
     * @return Next step in the bundle making process.
     */
    InputStream build();

    /**
     * Set header values that go into the Manifest.
     * Note that if BND is used to build this bundle, those instructions will be passed to BND as is.
     *
     * @param key   a key
     * @param value a value
     * @return {@literal this}
     */
    TinyBundle set(String key, String value);

    /**
     * Remove a previously added resource.
     * Usually usefull if you loaded an existing bundle into {@link TinyBundles} before.
     *
     * @param key a key as String
     * @return {@literal this}
     */
    TinyBundle removeResource(String key);

    /**
     * Remove a previously added header.
     * Usually usefull if you loaded an existing bundle into {@link TinyBundles} before.
     *
     * @param key a key as String
     * @return {@literal this}
     */
    TinyBundle removeHeader(String key);

    /**
     * Read an existing bundle or jar into Tinybundles for modification.
     *
     * @param input stream of JarInputStream
     * @return {@literal this}
     */
    TinyBundle read(InputStream input);

    /**
     * Read an existing bundle or jar into Tinybundles for modification.
     *
     * @param input stream of JarInputStream
     * @return {@literal this}
     */
    TinyBundle read(InputStream input, boolean readData);

    /**
     * Get header value.
     */
    public String getHeader(String key);

}
