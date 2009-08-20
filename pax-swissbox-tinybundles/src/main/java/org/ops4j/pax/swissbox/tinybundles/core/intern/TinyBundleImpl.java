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
package org.ops4j.pax.swissbox.tinybundles.core.intern;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundle;
import org.ops4j.pax.swissbox.tinybundles.core.metadata.RawBuilder;
import org.ops4j.pax.swissbox.tinybundles.store.TemporaryBinaryStore;
import org.ops4j.pax.swissbox.tinybundles.store.BinaryStore;

/**
 * Our default implementation of TinyBundle.
 * An instance should be retrieved via TinyBundles.newBundle() factory method.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class TinyBundleImpl implements TinyBundle
{

    private static Log LOG = LogFactory.getLog( TinyBundleImpl.class );

    private Map<String, URL> m_resources = new HashMap<String, URL>();
    private BinaryStore<InputStream> m_store;

    public TinyBundleImpl( BinaryStore<InputStream> bstore )
    {
        m_store = bstore;
    }

    public TinyBundle add( Class clazz )
    {
        String name = clazz.getName().replaceAll( "\\.", "/" ) + ".class";
        getClass().getResource( "/" + name );
        add( name, getClass().getResource( "/" + name ) );
        return this;
    }

    public TinyBundle add( String name, URL url )
    {
        m_resources.put( name, url );
        return this;
    }

    public TinyBundle add( String name, InputStream content )
    {
        try
        {
            return add( name, m_store.getLocation( m_store.store( content ) ).toURL() );
        } catch( IOException e )
        {
            throw new RuntimeException( e );
        }

    }

    public BuildableBundle prepare( BuildableBundle builder )
    {
        return builder.setResources( m_resources );
    }

    public BuildableBundle prepare()
    {
        return new RawBuilder().setResources( m_resources );
    }

}
