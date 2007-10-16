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
package org.ops4j.pax.swissbox.bundle;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.ops4j.lang.PreConditionException;

/**
 * Scans bundles for entries such as directories of files.
 *
 * @author Alin Dreghiciu
 * @since October 14, 2007
 */
public abstract class BundleEntryScanner<T>
    implements BundleScanner<T>
{

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog( BundleEntryScanner.class );

    /**
     * Root path to be scanned.
     */
    private final String m_rootPath;
    /**
     * True, if the bundle should be scanned recursively.
     */
    private final boolean m_recursive;

    /**
     * Creates a bundle entry scanner that scans all entries starting from the root path recursively.
     */
    public BundleEntryScanner()
    {
        this( "/", true );
    }

    /**
     * Creates a bundle entry scanner that scans all entries from a bundle starting form the root path specified
     *
     * @param rootPath  starting entry path (e.g. "/" for root of the bundle. Cannot be null.
     * @param recursive if the entry path should be scanned recursively (so if it has a subdirectory also the contet of
     *                  the subdirectory will be scanned
     */
    public BundleEntryScanner( final String rootPath, final boolean recursive )
    {
        PreConditionException.validateNotNull( "Root path", rootPath );

        m_rootPath = rootPath;
        m_recursive = recursive;
    }

    /**
     * @see BundleScanner#scan(org.osgi.framework.Bundle)
     */
    public List<T> scan( final Bundle bundle )
    {
        return internalScan( bundle, m_rootPath );
    }

    /**
     * Scans a specific path for resources.
     *
     * @param bundle bundle to be scanned
     * @param path   path to be scanned
     *
     * @return a list of resources
     */
    private List<T> internalScan( final Bundle bundle, final String path )
    {
        final List<T> resources = new ArrayList<T>();
        final Enumeration e = bundle.getEntryPaths( path );
        if( e != null )
        {
            while( e.hasMoreElements() )
            {
                final Object entry = e.nextElement();
                if( entry != null )
                {
                    resources.add( createResource( bundle, entry.toString() ) );
                    if( m_recursive && entry.toString().endsWith( "/" ) )
                    {
                        resources.addAll( internalScan( bundle, entry.toString() ) );
                    }
                }
            }
        }
        return resources;
    }

    protected abstract T createResource( Bundle bundle, String entryName );

}
