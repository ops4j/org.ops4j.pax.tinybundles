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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.io.StreamUtils;
import org.ops4j.pax.swissbox.tinybundles.core.BuildableBundle;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;
import org.ops4j.pax.swissbox.tinybundles.dp.TinyDP;

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

    private Map<String, Properties> m_sectionMetaData;

    private Map<String, String> m_dpHeaders;

    public TinyDPImpl( final StreamCache cache )
    {
        // meta Data will map addBundle*** Resource's name to their (partitially calculated) m_dpHeaders.
        // they all will end up in the DP manifest.
        m_sectionMetaData = new HashMap<String, Properties>();
        m_cache = cache;
        m_dpHeaders = new HashMap<String, String>();
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
        setSection( name, "added-by-tinybundles", new Date().toString() );
        if( inp == null )
        {
            // setSection Package Missing header for this NAME
            setSection( name, "DeploymentPackage-Missing", "true" );
        }
        else
        {
            // stream should be flushed it comes in
            // reading is crucial anyway to retrieve meta data.    
            try
            {
                m_cache.addBundle( name, inp );

                // read parsed data out so that it can be merged with dp meta data
                for( String s : m_cache.getHeaders( name ).keySet() )
                {
                    setSection( name, s, m_cache.getHeaders( name ).get( s ) );
                }
            } catch( IOException e )
            {
                LOG.error( "Problem while copying resource..", e );
            }
        }

        return this;
    }

    public TinyDP addResource( String name, InputStream inputStream, String resourceProcessorPID )
        throws IOException
    {
        m_cache.addResource( name, inputStream, resourceProcessorPID );
        setSection( name, "added-by-tinybundles", new Date().toString() );

        for( String s : m_cache.getHeaders( name ).keySet() )
        {
            setSection( name, s, m_cache.getHeaders( name ).get( s ) );
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

    private void setSection( String name, String key, String value )
    {
        Properties p = m_sectionMetaData.get( name );
        if( p == null )
        {
            p = new Properties();
            m_sectionMetaData.put( name, p );
        }

        p.setProperty( key, value );
    }

    public InputStream build()
        throws IOException
    {
        // 1. Manifest
        Manifest man = new Manifest();
        // defaults
        man.getMainAttributes().putValue( "Manifest-Version", "1.0" );
        man.getMainAttributes().putValue( "Content-Type", "application/vnd.osgi.dp" );

        // user defined
        for( String key : m_dpHeaders.keySet() )
        {
            man.getMainAttributes().putValue( key, m_dpHeaders.get( key ) );
        }

        Map<String, Attributes> entries = man.getEntries();
        for( String nameSection : m_sectionMetaData.keySet() )
        {
            Attributes attr = new Attributes();
            for( Object k : m_sectionMetaData.get( nameSection ).keySet() )
            {
                attr.putValue( (String) k, (String) m_sectionMetaData.get( nameSection ).get( k ) );
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

    private void copyResource( String nameSection, JarOutputStream jarOut )
        throws IOException
    {
        LOG.info( "copying " + nameSection );
        InputStream inputStream = m_cache.getStream( nameSection );
        ZipEntry zipEntry = new JarEntry( nameSection );
        jarOut.putNextEntry( zipEntry );
        StreamUtils.copyStream( inputStream, jarOut, false );
        jarOut.closeEntry();
    }
}
