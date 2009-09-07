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
package org.ops4j.pax.swissbox.tinybundles.core.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.ops4j.pax.swissbox.bnd.BndUtils;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import org.ops4j.pax.swissbox.tinybundles.core.BundleAs;
import org.ops4j.pax.swissbox.tinybundles.core.intern.CoreBuildImpl;

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

    public InputStream build()
    {
        InputStream in = new CoreBuildImpl().make( m_resources, new HashMap<String, String>() );
        try
        {

            return BndUtils.createBundle( in, m_directives, "BuildByTinyBundles" + UIDProvider.getUID() );
        }
        catch( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
