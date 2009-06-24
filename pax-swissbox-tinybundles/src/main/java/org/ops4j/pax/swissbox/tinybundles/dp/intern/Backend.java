package org.ops4j.pax.swissbox.tinybundles.dp.intern;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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

/**
 * @author Toni Menzel (tonit)
 * @since Jun 24, 2009
 */
public class Backend
{

    private static Log LOG = LogFactory.getLog( Backend.class );

    // this jar stream is basically the cached equivalent of the tinal dp.
    // we need to "m_cache" because of
    // - meta data will be constructed out of those artifacts
    // - meta data (MANIFEST) must come first

    // - also, the specification of DeploymentPackages mandate a specific order.
    private StreamCache m_cache = null;

    protected Map<String, Properties> m_sectionMetaData;

    protected Map<String, String> m_dpHeaders;

    public Backend( final StreamCache cache )
    {
        // meta Data will map addBundle*** Resource's name to their (partitially calculated) m_dpHeaders.
        // they all will end up in the DP manifest.
        m_sectionMetaData = new HashMap<String, Properties>();
        m_cache = cache;
        m_dpHeaders = new HashMap<String, String>();
    }

    public void setSection( String name, String key, String value )
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

    public void addBundle( String name, InputStream inp )
        throws IOException
    {
        m_cache.addBundle( name, inp );
    }

    public Map<String, String> getHeaders( String name )
    {
        return m_cache.getHeaders( name );
    }

    public void addResource( String name, InputStream inputStream, String resourceProcessorPID )
        throws IOException
    {
        m_cache.addResource( name,inputStream,resourceProcessorPID );
    }

    public void putHeader( String key, String value )
    {
        m_dpHeaders.put(key,value);
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
