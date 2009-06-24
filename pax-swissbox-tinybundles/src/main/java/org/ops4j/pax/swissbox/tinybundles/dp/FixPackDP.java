package org.ops4j.pax.swissbox.tinybundles.dp;

/**
 * API extension when dealing with Fix Packages.
 * 
 * @author Toni Menzel (tonit)
 * @since Jun 24, 2009
 */
public interface FixPackDP extends TinyDP
{
    public TinyDP removeBundle( String identifier );

    public TinyDP removeResource( String identifier );
}
