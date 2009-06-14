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
import java.net.MalformedURLException;
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
     * Convinient adapter for bundles that are not
     * included in this Fix Package.
     * Header will contain DeploymentPackage-Missing = true
     *
     * @param name identifier of Name parameter in DP manifest
     *
     * @return this (fluent api)
     */
    TinyDP addBundle( String name );

    /**
     * convinient adapter for addBundle(String,InputStream)
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this (fluent api)
     */
    TinyDP addBundle( String name, BuildableBundle inp );

    /**
     * convinient adapter for addBundle(String,InputStream)
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this (fluent api)
     *
     * @throws java.io.IOException if something goes wrong while interpreting the url
     */
    TinyDP addBundle( String name, String inp )
        throws IOException;

    /**
     * the very basic way to add a bundle.
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this
     */
    TinyDP addBundle( String name, InputStream inp );

    TinyDP addResource( String name, InputStream inp )
        throws IOException;

    TinyDP addResource( String name, InputStream inputStream, String resourceProcessorPID )
        throws IOException;

    TinyDP addResource( String name, String url )
        throws IOException;

    /**
     * @param name                 identifier of name section
     * @param url                  to be used to get the content
     * @param resourceProcessorPID a resource pid.
     *
     * @return this
     */
    TinyDP addResource( String name, String url, String resourceProcessorPID )
        throws IOException;

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

    /**
     * Will kick off the build of a deployment package.
     *
     * @return the Deployment Package (JarInputStream)
     *
     * @throws java.io.IOException If something goes wrong while building.
     */
    InputStream build()
        throws IOException;
}
