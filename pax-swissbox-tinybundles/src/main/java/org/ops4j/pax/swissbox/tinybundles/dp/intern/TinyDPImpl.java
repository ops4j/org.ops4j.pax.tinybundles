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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;
import org.ops4j.pax.swissbox.tinybundles.dp.TinyDP;
import org.ops4j.pax.swissbox.tinybundles.dp.store.BinaryStore;

/**
 * 1. Allow Reading of another DeploymentPackage as "base". (probably to be moved out)
 * 2. Normalize various method sugar to apriate calls to metadataStore.
 */
public class TinyDPImpl implements TinyDP
{

    private static Log LOG = LogFactory.getLog( TinyDPImpl.class );

    // All Contents of Deployment Package are stored here
    private BinaryStore<InputStream> m_cache = null;

    private Bucket m_meta;

    // The DeploymentPackages.Manifest Instructions
    private Map<String, String> m_dpHeaders = new HashMap<String, String>();

    public TinyDPImpl( InputStream parent, final Bucket bucket, BinaryStore<InputStream> cache )
    {
        m_meta = bucket;
        m_cache = cache;

        if( parent != null )
        {
            // replay parent into *this*
            JarInputStream jin = null;
            try
            {
                jin = new JarInputStream( parent );
                // first entry must be manifest
                Manifest man = jin.getManifest();

                Attributes att = man.getMainAttributes();
                for( Object o : att.keySet() )
                {
                    String k = o.toString();
                    String v = att.getValue( k );
                    System.out.println( k + " = " + v );
                    set( k, v );
                }

                Set<String> bundles = new HashSet<String>();
                Set<String> resources = new HashSet<String>();

                for( String s : man.getEntries().keySet() )
                {

                    Attributes attrs = man.getAttributes( s );
                    if( attrs.getValue( "Bundle-SymbolicName" ) != null )
                    {
                        bundles.add( s );
                    }
                    else if( attrs.getValue( "Resource-Processor" ) != null )
                    {
                        resources.add( s );
                    }
                }

                ZipEntry entry;
                while( ( entry = jin.getNextEntry() ) != null )
                {
                    if( bundles.contains( entry.getName() ) )
                    {
                        setBundle( entry.getName(), jin );
                    }
                    else if( resources.contains( entry.getName() ) )
                    {
                        setResource( entry.getName(), jin );
                    }

                }

            } catch( IOException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                if( jin != null )
                {
                    try
                    {
                        jin.close();
                    } catch( IOException e )
                    {
                        //
                    }
                }
            }
        }
    }

    public TinyDP setBundle( String s, BuildableBundle buildableBundle )
        throws IOException
    {
        return setBundle( s, buildableBundle.build( asStream() ) );
    }

    public TinyDP setBundle( String name, String inp )
        throws IOException
    {
        return setBundle( name, new URL( inp ).openStream() );
    }

    // just set metadata + add inputstream to some kind of cache.
    public TinyDP setBundle( String name, InputStream inp )
        throws IOException
    {
        m_meta.store( name, m_cache.store( inp ), DPContentTypes.BUNDLE );
        return this;
    }

    public TinyDP addResource( String name, InputStream inp, String resourceProcessorPID )
        throws IOException
    {
        m_meta.store( name, m_cache.store( inp ), DPContentTypes.OTHERRESOURCE );

        return this;
    }

    public TinyDP setResource( String name, InputStream inp )
        throws IOException
    {
        return addResource( name, inp, null );
    }

    public TinyDP setResource( String name, String url )
        throws IOException
    {
        return addResource( name, new URL( url ).openStream(), null );
    }

    public TinyDP setResource( String name, String url, String resourceProcessorPID )
        throws IOException
    {
        return addResource( name, new URL( url ).openStream(), resourceProcessorPID );
    }

    public TinyDP set( String key, String value )
    {
        m_dpHeaders.put( key, value );
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

    public TinyDP remove( String s )
    {
        m_meta.remove( s );
        return this;
    }

    public InputStream build()
        throws IOException
    {
        return new DPBuilder().build( m_dpHeaders, m_cache, m_meta );
    }
}
