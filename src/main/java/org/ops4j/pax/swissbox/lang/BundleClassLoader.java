/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.swissbox.lang;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.ops4j.lang.PreConditionException;

/**
 * Class loader that uses the a bundle in order to implement classloader functionality.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, December 29, 2007
 */
public class BundleClassLoader
    extends URLClassLoader
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( BundleClassLoader.class );
    /**
     * Bundle used for class loading.
     */
    private final Bundle m_bundle;

    /**
     * Privileged factory method.
     *
     * @param bundle bundle to be used for class loading. Cannot be null.
     *
     * @return created bundle class loader
     *
     * @see org.ops4j.pax.swissbox.lang.BundleClassLoader#BundleClassLoader(Bundle)
     */
    public static BundleClassLoader newPriviledged( final Bundle bundle )
    {
        return AccessController.doPrivileged(
            new PrivilegedAction<BundleClassLoader>()
            {
                public BundleClassLoader run()
                {
                    return new BundleClassLoader( bundle );
                }
            }
        );
    }

    /**
     * Creates a bundle class loader.
     *
     * @param bundle bundle to be used for class loading. Cannot be null.
     */
    public BundleClassLoader( final Bundle bundle )
    {
        super( new URL[] {} );
        PreConditionException.validateNotNull( bundle, "Bundle" );
        this.m_bundle = bundle;
    }

    @Override
    public URL getResource( final String name )
    {
        return findResource( name );
    }

    @Override
    protected Class findClass( final String name )
        throws ClassNotFoundException
    {
        return m_bundle.loadClass( name );
    }

    @Override
    protected Class loadClass( final String name, final boolean resolve )
        throws ClassNotFoundException
    {
        Class classToLoad = findClass( name );
        if( resolve )
        {
            resolveClass( classToLoad );
        }
        return classToLoad;
    }

    @Override
    public URL findResource( final String name )
    {
        return m_bundle.getResource( name );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Enumeration<URL> findResources( final String name )
        throws IOException
    {
        return m_bundle.getResources( name );
    }

    @Override
    public String toString()
    {
        return new StringBuffer()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "bundle=" ).append( m_bundle )
            .append( "}" )
            .toString();
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        BundleClassLoader that = (BundleClassLoader) o;

        if( m_bundle != null ? !m_bundle.equals( that.m_bundle ) : that.m_bundle != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return ( m_bundle != null ? m_bundle.hashCode() : 0 );
    }

}

