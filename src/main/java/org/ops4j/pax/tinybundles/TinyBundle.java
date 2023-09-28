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
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleActivator;

/**
 * TinyBundle provides a fluent API to create and modify OSGi bundles on the fly.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
@ProviderType
public interface TinyBundle {

    /**
     * Adds the class into the bundle and sets it as bundle activator.
     *
     * @param activator the bundle activator
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle activator(@NotNull final Class<? extends BundleActivator> activator);

    /**
     * Sets the bundle symbolic name.
     *
     * @param symbolicName the bundle symbolic name
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle symbolicName(@NotNull final String symbolicName);

    /**
     * Adds the resource into the bundle.
     *
     * @param path     the path where the resource gets stored in the bundle
     * @param resource the resource to be added
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle addResource(@NotNull final String path, @NotNull final URL resource);

    /**
     * Adds the resource into the bundle.
     *
     * @param path     the path where the resource gets stored in the bundle
     * @param resource the resource to be added
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle addResource(@NotNull final String path, @NotNull final InputStream resource);

    /**
     * Removes a resource from the bundle.
     *
     * @param path the path of the to be removed resource
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle removeResource(@NotNull final String path);

    /**
     * Adds the class into the bundle with default {@link InnerClassStrategy#ALL} strategy.
     *
     * @param clazz the class to be added
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle addClass(@NotNull final Class<?> clazz);

    /**
     * Adds the class into the bundle.
     *
     * @param clazz    the class to be added
     * @param strategy the inner class strategy for the given class
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle addClass(@NotNull final Class<?> clazz, @NotNull final InnerClassStrategy strategy);

    /**
     * Removes the class from the bundle.
     *
     * @param clazz the class to be removed
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle removeClass(@NotNull final Class<?> clazz);

    /**
     * Sets the {@link Manifest}  header.
     *
     * @param name  the header name
     * @param value the header value
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle setHeader(@NotNull final String name, @NotNull final String value);

    /**
     * Gets the {@link Manifest} header value for the given name.
     *
     * @param name the header name
     * @return the header value or null if header is not present
     */
    @Nullable
    String getHeader(@NotNull final String name);

    /**
     * Removes the {@link Manifest} header.
     *
     * @param name the name of the to be removed header
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle removeHeader(@NotNull final String name);

    /**
     * Reads an existing bundle or jar into this TinyBundle.
     *
     * @param jar the stream of JarInputStream
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle readIn(@NotNull final JarInputStream jar);

    /**
     * Reads an existing jar or bundle into this tiny bundle.
     *
     * @param jar         the source jar or bundle
     * @param skipContent true to read jar content also, false to read {@link Manifest} only
     * @return the tiny bundle
     */
    @NotNull
    TinyBundle readIn(@NotNull final JarInputStream jar, final boolean skipContent);

    /**
     * Builds the bundle with default bnd {@link Builder}.
     *
     * @return the built bundle
     */
    @NotNull
    InputStream build();

    /**
     * Builds the bundle with given {@link Builder}.
     *
     * @param builder the builder to be used for building
     * @return the built bundle
     */
    @NotNull
    InputStream build(@NotNull final Builder builder);

}
