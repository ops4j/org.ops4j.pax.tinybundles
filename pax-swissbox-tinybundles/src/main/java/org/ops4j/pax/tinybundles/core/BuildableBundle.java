package org.ops4j.pax.tinybundles.core;

import java.net.URL;
import java.util.Map;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public interface BuildableBundle
{

    BuildableBundle set( String key, String value );

    <T> T build( BundleAs<T> type );

    BuildableBundle setResources( Map<String, URL> resources );
}
