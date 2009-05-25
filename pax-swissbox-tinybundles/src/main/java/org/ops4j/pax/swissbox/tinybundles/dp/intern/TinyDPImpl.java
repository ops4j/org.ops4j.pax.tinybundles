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
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.swissbox.tinybundles.dp.TinyDP;
import org.ops4j.io.StreamUtils;

/**
 * All In one TinyDP implementation.
 * Resource Cache will be exchangable (constructor injection)
 */
public class TinyDPImpl implements TinyDP
{

    private static Log LOG = LogFactory.getLog( TinyDPImpl.class );

    // this jar stream is basically the cached equivalent of the tinal dp.
    // we need to "m_cache" because of
    // - meta data will be constructed out of those artifacts
    // - meta data (MANIFEST) must come first

    // - also, the specification of DeploymentPackages mandate a specific order.
    private StreamCache m_cache = null;

    private Map<String, Properties> metaData;

    public TinyDPImpl( final StreamCache cache )
    {
        // meta Data will map add*** Resource's name to their (partitially calculated) properties.
        // they all will end up in the DP manifest.
        metaData = new HashMap<String, Properties>();
        m_cache = cache;
    }

    public TinyDP addBundle( String name )
    {
        return addBundle( name, (InputStream) null );
    }

    public TinyDP addBundle( String s, BuildableBundle buildableBundle )
    {
        return addBundle( s, buildableBundle.build( asStream() ) );
    }

    public TinyDP addBundle( String name, String inp )
        throws IOException
    {
        return addBundle( name, new URL( inp ).openStream() );
    }

    public TinyDP addBundle( String name, InputStream inp )
    {
        set( name, "added-by-tinybundles", new Date().toString() );
        if( inp == null )
        {
            // set Package Missing header for this NAME
            set( name, "DeploymentPackage-Missing", "true" );
        }
        else
        {
            // stream should be flushed it comes in
            // reading is crucial anyway to retrieve meta data.    
            m_cache.add( name, inp );

            // read parsed data out so that it can be merged with dp meta data
            for( String s : m_cache.getHeaders( name ).keySet() )
            {
                set( name, s, m_cache.getHeaders( name ).get( s ) );
            }
        }

        return this;
    }

    private void set( String name, String key, String value )
    {
        Properties p = metaData.get( name );
        if( p == null )
        {
            p = new Properties();
            metaData.put( name, p );
        }
        p.setProperty( key, value );
    }

    public TinyDP addCustomizer( InputStream inp )
    {
        return null;
    }

    public InputStream build()
        throws IOException
    {
        // 1. Manifest
        Manifest man = new Manifest();
        man.getMainAttributes().putValue( "Content-Type", "application/vnd.osgi.dp" );
        man.getMainAttributes().put( "DeploymentPackage-SymbolicName", "" );
        man.getMainAttributes().put( "DeploymentPackage-DeploymentPackage-Version", "" );

        Map<String, Attributes> entries = man.getEntries();
        for( String nameSection : metaData.keySet() )
        {
            Attributes attr = new Attributes();
            for( Object k : metaData.get( nameSection ).keySet() )
            {
                attr.putValue( (String) k, (String) metaData.get( nameSection ).get( k ) );
            }
            entries.put( nameSection, attr );
        }

        final PipedInputStream pin = new PipedInputStream();
        try
        {
            final PipedOutputStream pout = new PipedOutputStream( pin );
            final JarOutputStream jarOut = new JarOutputStream( pout, man );

            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        // 2. META-INF resources: META-INF/*.SF, META-INF/*.DSA, META-INF/*.RS
                        for( String nameSection : m_cache.getMetaInfResources() )
                        {
                            copyResource( nameSection, jarOut );
                        }
                        // 3. Localization files (usually from OSGI-INF)
                        for( String nameSection : m_cache.getLocalizationFiles() )
                        {
                            copyResource( nameSection, jarOut );
                        }
                        // 4. Bundles
                        for( String nameSection : m_cache.getBundles() )
                        {
                            copyResource( nameSection, jarOut );
                        }

                        // 5. Other Resources
                        for( String nameSection : m_cache.getOtherResources() )
                        {
                            copyResource( nameSection, jarOut );
                        }

                    } catch( IOException ioE )
                    {
                        ioE.printStackTrace();
                    } finally
                    {
                        try
                        {
                            if( jarOut != null )
                            {
                                jarOut.close();
                            }
                            pout.close();

                        }
                        catch( Exception e )
                        {
                            // be quiet.
                        }
                        LOG.info( "Copy thread finished." );
                    }
                }
            }.start();
        } catch( Exception e )
        {
            LOG.error( "problem ! ", e );
        }
        return pin;
    }

    private void copyResource( String nameSection, JarOutputStream jarOut )
        throws IOException
    {
        InputStream inputStream = m_cache.getStream( nameSection );
        ZipEntry zipEntry = new JarEntry( nameSection );
        jarOut.putNextEntry( zipEntry );
        StreamUtils.copyStream( inputStream, jarOut, false );
        jarOut.closeEntry();
    }
}
