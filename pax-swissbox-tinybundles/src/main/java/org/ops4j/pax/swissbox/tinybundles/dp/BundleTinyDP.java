package org.ops4j.pax.swissbox.tinybundles.dp;

import java.io.IOException;
import java.io.InputStream;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;

/**
 * @author Toni Menzel (tonit)
 * @since Jun 24, 2009
 */
public interface BundleTinyDP
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
    TinyDP addBundle( String name )
        throws IOException;

    /**
     * convinient adapter for addBundle(String,InputStream)
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this (fluent api)
     */
    TinyDP addBundle( String name, BuildableBundle inp )
        throws IOException;

    /**
     * convinient adapter for addBundle(String,InputStream)
     *
     * @param name identifier of Name parameter in DP manifest
     * @param url  content of this resource
     *
     * @return this (fluent api)
     *
     * @throws java.io.IOException if something goes wrong while interpreting the url
     */
    TinyDP addBundle( String name, String url )
        throws IOException;

    /**
     * the very basic way to add a bundle.
     *
     * @param name identifier of Name parameter in DP manifest
     * @param inp  content of this resource
     *
     * @return this
     */
    TinyDP addBundle( String name, InputStream inp )
        throws IOException;
}
