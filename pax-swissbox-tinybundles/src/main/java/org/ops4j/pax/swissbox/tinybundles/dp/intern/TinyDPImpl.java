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
 * Implementation that also allows Fix-Packs (See Constructors)
 */
public class TinyDPImpl implements TinyDP
{

    private static Log LOG = LogFactory.getLog( TinyDPImpl.class );

    // All Contents of Deployment Package are stored here
    final private BinaryStore<InputStream> m_cache;

    // out meta data repository
    final private Bucket m_meta;

    // the one who can finaly build DeploymentPackages
    final private DPBuilder m_builder;

    // The DeploymentPackages.Manifest Instructions
    final private Map<String, String> m_dpHeaders = new HashMap<String, String>();

    public TinyDPImpl( DPBuilder builder, InputStream parent, final Bucket bucket, BinaryStore<InputStream> cache )
    {
        m_cache = cache;
        m_builder = builder;
        m_meta = bucket;

        if( parent != null )
        {
            // replay parent into *this*
            set( Constants.DEPLOYMENTPACKAGE_FIXPACK, "yes" );
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
                    else
                    {
                        resources.add( s );
                    }
                }

                ZipEntry entry;
                while( ( entry = jin.getNextEntry() ) != null )
                {
                    // add to store but  mark entries as missing 
                    if( bundles.contains( entry.getName() ) )
                    {
                        setBundle( entry.getName(), jin, false );
                    }
                    else if( resources.contains( entry.getName() ) )
                    {

                        setResource( entry.getName(), jin, false );
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
        setBundle( name, inp, true );
        return this;
    }

    // just set metadata + add inputstream to some kind of cache.
    public TinyDP setBundle( String name, InputStream inp, boolean includeContent )
        throws IOException
    {
        m_meta.store( name, m_cache.store( inp ), DPContentTypes.BUNDLE, includeContent );
        return this;
    }

    public TinyDP addResource( String name, InputStream inp )
        throws IOException
    {
        setResource( name, inp, true );
        return this;
    }

    private TinyDP setResource( String name, InputStream inp, boolean includeContent )
        throws IOException
    {
        m_meta.store( name, m_cache.store( inp ), DPContentTypes.OTHERRESOURCE, includeContent );

        return this;
    }

    public TinyDP setResource( String name, InputStream inp )
        throws IOException
    {
        return addResource( name, inp );
    }

    public TinyDP setResource( String name, String url )
        throws IOException
    {
        return addResource( name, new URL( url ).openStream() );
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
        return m_builder.build( m_dpHeaders, m_cache, m_meta );
    }
}
