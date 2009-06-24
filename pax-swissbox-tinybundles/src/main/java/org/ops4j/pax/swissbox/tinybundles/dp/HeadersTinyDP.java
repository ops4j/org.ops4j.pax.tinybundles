package org.ops4j.pax.swissbox.tinybundles.dp;

/**
 * @author Toni Menzel (tonit)
 * @since Jun 24, 2009
 */
public interface HeadersTinyDP
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
}
