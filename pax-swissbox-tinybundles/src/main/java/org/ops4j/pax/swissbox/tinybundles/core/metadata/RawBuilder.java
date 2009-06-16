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
