package org.ops4j.pax.swissbox.tinybundles.core.metadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import org.ops4j.pax.swissbox.tinybundles.core.BundleAs;
import org.ops4j.pax.swissbox.tinybundles.core.intern.CoreBuildImpl;
import org.ops4j.pax.url.bnd.BndUtils;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class BndBuilder implements BuildableBundle
{

    private Properties m_directives = new Properties();
    private Map<String, URL> m_resources;

    public BuildableBundle set( String key, String value )
    {
        m_directives.put( key, value );
        return this;
    }

    public BuildableBundle setResources( Map<String, URL> resources )
    {
        m_resources = resources;
        return this;
    }

    public <T> T build( BundleAs<T> type )
    {
        InputStream in = new CoreBuildImpl().make( m_resources, new HashMap<String, String>() );
        try
        {
            return type.make( BndUtils.createBundle( in, m_directives, "BuildByTinyBundles" ) );
        }
        catch( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
