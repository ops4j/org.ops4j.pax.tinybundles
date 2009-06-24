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
package org.ops4j.pax.swissbox.tinybundles.dp.intern;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;
import org.ops4j.pax.swissbox.tinybundles.dp.FixPackDP;
import org.ops4j.pax.swissbox.tinybundles.dp.TinyDP;

/**
 * All In one TinyDP implementation.
 * Resource Cache will be exchangable (constructor injection)
 */
public class TinyDPImpl implements FixPackDP
{

    private static Log LOG = LogFactory.getLog( TinyDPImpl.class );

    private Backend m_backend;

    public TinyDPImpl( InputStream parent, final Backend back)
    {
        m_backend =back;
    }

    public TinyDP addBundle( String name )
        throws IOException
    {
        return addBundle( name, (InputStream) null );
    }

    public TinyDP addBundle( String s, BuildableBundle buildableBundle )
        throws IOException
    {
        return addBundle( s, buildableBundle.build( asStream() ) );
    }

    public TinyDP addBundle( String name, String inp )
        throws IOException
    {
        return addBundle( name, new URL( inp ).openStream() );
    }

    public TinyDP addBundle( String name, InputStream inp )
        throws IOException
    {
        m_backend.setSection( name, "added-by-tinybundles", new Date().toString() );
        if( inp == null )
        {
            // setSection Package Missing header for this NAME
            m_backend.setSection( name, Constants.DEPLOYMENTPACKAGE_MISSING, "true" );
        }
        else
        {
            // stream should be flushed it comes in
            // reading is crucial anyway to retrieve meta data.    
            m_backend.addBundle( name, inp );

            // read parsed data out so that it can be merged with dp meta data
            for( String s : m_backend.getHeaders( name ).keySet() )
            {
                m_backend.setSection( name, s, m_backend.getHeaders( name ).get( s ) );
            }

        }

        return this;
    }

    public TinyDP addResource( String name, InputStream inputStream, String resourceProcessorPID )
        throws IOException
    {
        m_backend.addResource( name, inputStream, resourceProcessorPID );
        m_backend.setSection( name, "added-by-tinybundles", new Date().toString() );

        for( String s : m_backend.getHeaders( name ).keySet() )
        {
            m_backend.setSection( name, s, m_backend.getHeaders( name ).get( s ) );
        }
        return this;
    }

    public TinyDP addResource( String name, InputStream inp )
        throws IOException
    {
        return addResource( name, inp, null );
    }

    public TinyDP addResource( String name, String url )
        throws IOException
    {
        return addResource( name, new URL( url ).openStream(), null );
    }

    public TinyDP addResource( String name, String url, String resourceProcessorPID )
        throws IOException
    {
        return addResource( name, new URL( url ).openStream(), resourceProcessorPID );
    }

    public TinyDP set( String key, String value )
    {
        m_backend.putHeader( key, value );
        return this;
    }

    public TinyDP setSymbolicName( String value )
    {
        return set( Constants.DEPLOYMENTPACKAGE_SYMBOLICMAME, value );
    }

    public TinyDP setVersion( String value )
    {
        return set( Constants.DEPLOYMENTPACKAGE_VERSION, value );
    }

    public TinyDP removeBundle( String s )
    {
        return null;
    }

    public TinyDP removeResource( String identifier )
    {
        return null;
    }

    public InputStream build()
        throws IOException
    {
        return m_backend.build();
    }
}
