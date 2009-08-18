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
package org.ops4j.pax.swissbox.tinybundles.dp;

import java.io.IOException;
import java.io.InputStream;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;

/**
 * Humane API for constructing Deployment Packages.
 * Capabilities and final format will comply to
 * OSGi Compendium R4 Version 4.2, Deployment Admin Specification Version 1.1
 *
 * @author Toni Menzel (toni@okidokiteam.com)
 * @since May 23, 2009
 */
public interface TinyDP
{

    /**
     * Meta Data that will appear in the Main Section of this DP Meta Inf Manifest
     *
     * @param key   to be used
     * @param value to be used
     *
     * @return this
     */
    TinyDP set( String key, String value );

    /**
     * Shortcut for set( Constants.DEPLOYMENTPACKAGE_SYMBOLICMAME, value )
     *
     * @param value to be used
     *
     * @return this
     */
    TinyDP setSymbolicName( String value );

    /**
     * Shortcut for set( Constants.DEPLOYMENTPACKAGE_VERSION, value )
     *
     * @param value to be used
     *
     * @return this
     */
    TinyDP setVersion( String value );

    TinyDP setResource( String name, InputStream inp )
        throws IOException;

    TinyDP setResource( String name, String url )
        throws IOException;

    InputStream build()
        throws IOException;

    public TinyDP remove( String identifier );

    /**
     * convinient adapter for setBundle(String,InputStream)
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this (fluent api)
     * @throws java.io.IOException in case of an IO error when processing inp
     */
    TinyDP setBundle( String name, BuildableBundle inp )
        throws IOException;

    /**
     * convenient adapter for setBundle(String,InputStream)
     *
     * @param name identifier of Name parameter in DP manifest
     * @param url  content of this resource
     *
     * @return this (fluent api)
     *
     * @throws java.io.IOException if something goes wrong while interpreting the url
     */
    TinyDP setBundle( String name, String url )
        throws IOException;

    /**
     * the very basic way to add a bundle.
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this
     * @throws java.io.IOException in case of an IO error when processing inp
     */
    TinyDP setBundle( String name, InputStream inp )
        throws IOException;
}
