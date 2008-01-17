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
package org.ops4j.pax.swissbox.extender;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.osgi.framework.Bundle;
import org.ops4j.lang.PreConditionException;

/**
 * Scans bundles for entries such as directories of files.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, October 14, 2007
 */
public abstract class BundleEntryScanner<T>
    implements BundleScanner<T>
{

    /**
     * Root path to be scanned.
     */
    private final String m_path;
    /**
     * File pattern to be scanned.
     */
    private String m_filePattern;
    /**
     * True, if the bundle should be scanned recursively.
     */
    private final boolean m_recurse;

    /**
     * Creates a bundle entry scanner that scans all entries from a bundle starting form the root path specified
     *
     * @param path        The path name in which to look. A specified path of Ò/Ó indicates the root of the bundle. Path
     *                    is relative to the root of the bundle and must not be null
     * @param filePattern The file name pattern for selecting entries in the specified path. The pattern is only matched
     *                    against the last element of the entry path and it supports substring matching, as specified in
     *                    the Filter specification, using the wild-card character (Ó*Ó). If null is specified, this is
     *                    equivalent to Ò*Ó and matches all files.
     * @param recurse     If true, recurse into subdirectories. Otherwise only return entries from the given directory
     *
     * @see Bundle#findEntries(String, String, boolean)
     */
    public BundleEntryScanner( final String path,
                               final String filePattern,
                               boolean recurse )
    {
        PreConditionException.validateNotNull( "Path", path );
        PreConditionException.validateNotNull( "File pattern", filePattern );

        m_path = path;
        m_filePattern = filePattern;
        m_recurse = recurse;
    }

    /**
     * @see BundleScanner#scan(org.osgi.framework.Bundle)
     */
    public List<T> scan( final Bundle bundle )
    {
        final List<T> resources = new ArrayList<T>();
        final Enumeration e = bundle.findEntries( m_path, m_filePattern, m_recurse );
        if( e != null )
        {
            while( e.hasMoreElements() )
            {
                final URL entry = (URL) e.nextElement();
                if( entry != null )
                {
                    resources.add( createResource( bundle, entry ) );
                }
            }
        }
        return resources;
    }

    protected abstract T createResource( Bundle bundle, URL entry );

    @Override
    public String toString()
    {
        return new StringBuffer()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "path=" ).append( m_path )
            .append( ",filePattern=" ).append( m_filePattern )
            .append( ",recurse=" ).append( m_recurse )
            .append( "}" )
            .toString();
    }

}
