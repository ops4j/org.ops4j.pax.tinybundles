package org.ops4j.pax.swissbox.tinybundles.core.metadata;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import org.ops4j.pax.swissbox.tinybundles.core.BundleAs;
import org.ops4j.pax.swissbox.tinybundles.core.intern.CoreBuildImpl;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class RawBuilder implements BuildableBundle
{

    private Map<String, String> m_headers = new HashMap<String, String>();
    private Map<String, URL> m_resources;

    public BuildableBundle set( String key, String value )
    {
        m_headers.put( key, value );
        return this;
    }

    public BuildableBundle setResources( Map<String, URL> resources )
    {
        m_resources = resources;
        return this;
    }

    public <T> T build( BundleAs<T> type )
    {
        return type.make( new CoreBuildImpl().make( m_resources, m_headers ) );
    }
}
