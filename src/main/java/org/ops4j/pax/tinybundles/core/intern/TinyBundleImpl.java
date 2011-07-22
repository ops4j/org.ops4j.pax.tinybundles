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
package org.ops4j.pax.tinybundles.core.intern;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import org.ops4j.pax.tinybundles.core.BuildStrategy;
import org.ops4j.pax.tinybundles.core.InnerClassStrategy;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.store.Handle;
import org.ops4j.store.Store;

import static org.ops4j.pax.tinybundles.core.TinyBundles.*;

/**
 * Our default implementation of TinyBundle.
 * An instance should be retrieved via DefaultTinybundleProvider.newBundle() factory method.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class TinyBundleImpl implements TinyBundle {

    private Map<String, URL> m_resources = new HashMap<String, URL>();
    private Map<String, String> m_headers = new HashMap<String, String>();

    final private Store<InputStream> m_store;

    public TinyBundleImpl( Store<InputStream> bstore )
    {
        m_store = bstore;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle read( InputStream in )
    {
        if( in != null ) {
            try {
                // 1. store to disk
                Handle handle = m_store.store( in );

                JarInputStream jarOut = new JarInputStream( m_store.load( handle ) );
                // 2. read meta data and wire with this.
                // TODO: reading out just main headers will remove the other parts. Fix this with
                // TODO change m_headers to type Manifest natively.
                Manifest manifest = jarOut.getManifest();
                Attributes att = manifest.getMainAttributes();
                for( Object o : att.keySet() ) {
                    String k = o.toString();
                    String v = att.getValue( k );
                    set( k, v );
                }

                // 3. read data
                JarEntry entry = null;
                while( ( entry = jarOut.getNextJarEntry() ) != null ) {
                    add( entry.getName(), jarOut );
                }

                // done.
            } catch( IOException e ) {
                throw new RuntimeException( "Problem loading bundle.", e );
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle add( Class<?> clazz )
    {
        add( clazz, InnerClassStrategy.ALL );
        return this;
    }

    public TinyBundle add( Class<?> clazz, InnerClassStrategy strategy )
    {
        String name = mapClassToEntry( clazz.getName() );
        URL resource = clazz.getResource( "/" + name );
        
        if (resource == null) {
            throw new IllegalArgumentException( "Class " + clazz.getName() + " not found! (resource: " + name + " )" );
        }
        add( name, resource );

        final String expression;
        if( strategy == InnerClassStrategy.ALL ) {
            expression = clazz.getSimpleName() + "\\$.+\\.class";
        }
        else if( strategy == InnerClassStrategy.ANONYMOUS ) {
            expression = clazz.getSimpleName() + "\\$[$0-9]+\\.class";
        }
        else {
            return this;
        }

        String path = resource.getPath();
        File classFile = new File( path );
        File[] innerClassFiles = classFile.getParentFile().listFiles( new FileFilter() {
            public boolean accept( File f )
            {
                return f.getName().matches( expression );
            }
        }
        );

        if( innerClassFiles == null ) {
            return this;
        }

        int basePathIndex = path.lastIndexOf( name );
        for( File f : innerClassFiles ) {
            String classPath = f.getPath().substring( basePathIndex );
            add( classPath, getClass().getResource( "/" + classPath ) );
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle remove( Class<?> content )
    {
        String name = mapClassToEntry( content.getName() );
        removeResource( name );
        return this;
    }

    private String mapClassToEntry( String clazzname )
    {
        return clazzname.replaceAll( "\\.", "/" ) + ".class";
    }

    /**
     * {@inheritDoc}
     */
    public TinyBundle add( String name, URL url )
    {
        m_resources.put( name, url );
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle add( String name, InputStream content )
    {
        try {
            return add( name, m_store.getLocation( m_store.store( content ) ).toURL() );
        } catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @{@inheritDoc}
     */
    public InputStream build(  )
    {
        return withClassicBuilder().build( m_resources, m_headers );
    }

    /**
     * @{@inheritDoc}
     */
    public InputStream build( BuildStrategy builder )
    {
        return builder.build( m_resources, m_headers );
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle set( String key, String value )
    {
        m_headers.put( key, value );
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle removeResource( String key )
    {
        m_resources.remove( key );
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    public TinyBundle removeHeader( String key )
    {
        m_headers.remove( key );
        return this;
    }

}