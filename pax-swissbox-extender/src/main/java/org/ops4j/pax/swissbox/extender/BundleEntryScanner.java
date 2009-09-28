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

import org.osgi.framework.Bundle;
import org.ops4j.lang.NullArgumentException;

/**
 * Scans bundles for entries such as directories of files.
 * The bundle entry scanner is abstract in order to allow subclasses to create specific resources out of the found urls.
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
    private final String m_filePattern;
    /**
     * True, if the bundle should be scanned recursively.
     */
    private final boolean m_recurse;
    /**
     * Manifest header name of path.
     */
    private final String m_pathManifestHeader;
    /**
     * Manifest header name of file pattern.
     */
    private final String m_filePatternManifestHeader;
    /**
     * Manifest header name of recurse.
     */
    private final String m_recurseManifestHeader;

    /**
     * Creates a bundle entry scanner that scans all entries from a bundle starting form the root path specified
     * without looking for bundle manifest headers.
     *
     * @param path        The path name in which to look. A specified path of "/" indicates the root of
     *                    the bundle. Path is relative to the root of the bundle. If the path is null then
     *                    it is considered to be root of the bundle.
     * @param filePattern The file name pattern for selecting entries in the specified path. The pattern
     *                    is only matched against the last element of the entry path and it supports
     *                    sub-string matching, as specified in the Filter specification, using the
     *                    wild-card character ("*"). If null is specified, this is
     *                    equivalent to "*" and matches all files.
     * @param recurse     If true, recurse into sub-directories. Otherwise only return entries from the
     *                    given directory
     *
     * @see BundleEntryScanner#BundleEntryScanner(String, String, String, String, String, boolean)
     */
    public BundleEntryScanner( final String path,
                               final String filePattern,
                               boolean recurse )
    {
        this( null, null, null, path, filePattern, recurse );
        NullArgumentException.validateNotNull( "Path", path );
        NullArgumentException.validateNotNull( "File pattern", filePattern );
    }

    /**
     * Creates a bundle entry scanner that scans all entries from a bundle starting form the root path specified. The
     * path / filePattern / recurse can be specified in the manifest of the bundle. The provided path / filePattern /
     * recurse are used as defaults if there are no corresponding manifest headers.
     *
     * @param pathManifestHeader        name of the manifest header for path
     * @param filePatternManifestHeader name of the manifest header for file pattern
     * @param recurseManifestHeader     name of the manifest header for recurse
     * @param path                      The path name in which to look. A specified path of "/" indicates the root of
     *                                  the bundle. Path is relative to the root of the bundle. If the path is null then
     *                                  it is considered to be root of the bundle.
     * @param filePattern               The file name pattern for selecting entries in the specified path. The pattern
     *                                  is only matched against the last element of the entry path and it supports
     *                                  sub-string matching, as specified in the Filter specification, using the
     *                                  wild-card character ("*"). If null is specified, this is
     *                                  equivalent to "*" and matches all files.
     * @param recurse                   If true, recurse into sub-directories. Otherwise only return entries from the
     *                                  given directory
     *
     * @see Bundle#findEntries(String, String, boolean)
     */
    public BundleEntryScanner( final String pathManifestHeader,
                               final String filePatternManifestHeader,
                               final String recurseManifestHeader,
                               final String path,
                               final String filePattern,
                               boolean recurse )
    {
        m_pathManifestHeader = pathManifestHeader;
        m_filePatternManifestHeader = filePatternManifestHeader;
        m_recurseManifestHeader = recurseManifestHeader;

        m_path = path == null ? "/" : ( path.endsWith( "/" ) ? path : path + "/" );
        m_filePattern = filePattern;
        m_recurse = recurse;
    }

    /**
     * Returns the path to be searched by first looking for an entry in the manifest of the bundle specified by
     * path manifest header. It will return the default path if:
     * - path manifest header is null
     * - header is not set
     * - header is not a string
     * - header is empty
     *
     * @param bundle bundle containing the manifest
     *
     * @return found path
     */
    protected String getPath( final Bundle bundle )
    {
        if( m_pathManifestHeader != null )
        {
            final Object value = bundle.getHeaders().get( m_pathManifestHeader );
            if( value instanceof String && ( (String) value ).trim().length() > 0 )
            {
                String path = (String) value;
                if( !path.endsWith( "/" ) )
                {
                    path = path + "/";
                }
                return path;
            }
        }
        return m_path;
    }

    /**
     * Returns the file pattern to be searched by first looking for an entry in the manifest of the bundle specified by
     * file pattern manifest header. It will return the default file pattern if:
     * - file pattern manifest header is null
     * - header is not set
     * - header is not a string
     * - header is empty
     *
     * @param bundle bundle containing the manifest
     *
     * @return found file pattern
     */
    protected String getFilePattern( final Bundle bundle )
    {
        if( m_filePatternManifestHeader != null )
        {
            final Object value = bundle.getHeaders().get( m_filePatternManifestHeader );
            if( value instanceof String && ( (String) value ).trim().length() > 0 )
            {
                return (String) value;
            }
        }
        return m_filePattern;
    }

    /**
     * Returns the recurse by first looking for an entry in the manifest of the bundle specified by recurse manifest
     * header. It will return the default recurse if:
     * - file pattern manifest header is null
     * - header is not set
     * - header is not a string
     * - header is not true or false (case insensitive)
     * - header is empty
     *
     * @param bundle bundle containing the manifest
     *
     * @return found file pattern
     */
    protected boolean getRecurse( final Bundle bundle )
    {
        if( m_recurseManifestHeader != null )
        {
            final Object value = bundle.getHeaders().get( m_recurseManifestHeader );
            if( value instanceof String
                && ( (String) value ).trim().length() > 0
                && ( ( ( (String) value ).trim().equalsIgnoreCase( "true" ) )
                     || ( (String) value ).trim().equalsIgnoreCase( "false" ) ) )
            {

                return Boolean.valueOf( (String) value );
            }
        }
        return m_recurse;
    }

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
