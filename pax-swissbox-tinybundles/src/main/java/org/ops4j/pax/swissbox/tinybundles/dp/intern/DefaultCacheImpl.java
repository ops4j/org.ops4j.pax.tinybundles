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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.osgi.framework.Constants;
import org.ops4j.io.FileUtils;
import org.ops4j.io.StreamUtils;

/**
 * Default implementation will (not done yet) write incoming streams to disk.
 */
public class DefaultCacheImpl implements StreamCache
{

    private File m_dir;

    private List<String> bundleRepository;

    private List<String> resourceRepository;

    private List<String> localizationRepository;

    private List<String> metainfResourcesRepository;

    private Map<String, CacheData> idx;

    public DefaultCacheImpl()
    {
        bundleRepository = new ArrayList<String>();
        resourceRepository = new ArrayList<String>();
        localizationRepository = new ArrayList<String>();
        metainfResourcesRepository = new ArrayList<String>();

        idx = new HashMap<String, CacheData>();

        m_dir = new File( System.getProperty( "java.io.tmpdir" ) + "/tb" );
        if( m_dir.exists() )
        {
            FileUtils.delete( m_dir );
        }
        m_dir.mkdirs();


    }

    /**
     * name to cache file mapping is very fluffy == by convention.
     * Not called to be secure for now..
     *
     * @param name            logical name of resource given by InputStream
     * @param resourceContent content
     */
    public void addBundle( String name, InputStream resourceContent )
        throws IOException
    {
        bundleRepository.add( name );

        store( name, resourceContent );
    }

    private CacheData store( final String name, final InputStream resourceContent )
        throws IOException
    {
        // TODO: do SHA1 mappign instead: 
        File target = new File( m_dir, name );
        StreamUtils.copyStream( resourceContent, new FileOutputStream( target ), true );
        // reaching here means that resource has been copied successfully.
        CacheData dat = new CacheData( target );

        idx.put( name, dat );

        // read stream to find out probably meta bundleRepository:
        JarInputStream jout = null;
        try
        {
            jout = new JarInputStream( new FileInputStream( target ) );
            Manifest man = jout.getManifest();
            // those m_headers are meant to show up in DP Manifest.
            dat.set( Constants.BUNDLE_SYMBOLICNAME, man.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME ) );
            dat.set( Constants.BUNDLE_VERSION, man.getMainAttributes().getValue( Constants.BUNDLE_VERSION ) );

        } catch( Exception e )
        {
            //
        } finally
        {
            if( jout != null )
            {
                try
                {
                    jout.close();
                } catch( Exception e )
                {

                }
            }
        }
        return dat;
    }

    public String[] getBundles()
    {
        return bundleRepository.toArray( new String[bundleRepository.size()] );
    }

    public String[] getOtherResources()
    {
        return resourceRepository.toArray( new String[resourceRepository.size()] );
    }

    public String[] getLocalizationFiles()
    {
        return localizationRepository.toArray( new String[localizationRepository.size()] );
    }

    public String[] getMetaInfResources()
    {
        return metainfResourcesRepository.toArray( new String[metainfResourcesRepository.size()] );
    }

    public Map<String, String> getHeaders( String name )
    {
        return idx.get( name ).getHeaders();
    }

    public InputStream getStream( String name )
    {
        return idx.get( name ).getStream();
    }

    public void addResource( String name, InputStream inputStream, String resourceProcessorPID )
        throws IOException
    {

        CacheData dat = store( name, inputStream );
        if( resourceProcessorPID != null )
        {
            dat.set( "Resource-Processor", resourceProcessorPID );
        }
    }

    private class CacheData
    {

        private Map<String, String> m_headers = new HashMap<String, String>();
        private File m_target;

        public CacheData( File content )
        {
            m_target = content;
        }

        public void set( String key, String value )
        {
            m_headers.put( key, value );
        }

        public Map<String, String> getHeaders()
        {
            return m_headers;
        }

        public InputStream getStream()
        {
            try
            {
                return new FileInputStream( m_target );
            } catch( FileNotFoundException e )
            {
                // should not happen
                throw new RuntimeException( e );
            }
        }
    }
}
