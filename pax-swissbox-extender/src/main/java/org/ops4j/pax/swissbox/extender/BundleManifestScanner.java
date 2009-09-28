/*
 * Copyright 2008 Alin Dreghiciu.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.ops4j.lang.NullArgumentException;

/**
 * Scans manifest files for specific manifest entries.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, February 09, 2008
 */
public class BundleManifestScanner
    implements BundleScanner<ManifestEntry>
{

    /**
     * Dictionary filter used to filter manifest headers.
     */
    private ManifestFilter m_manifestFilter;

    /**
     * Creates a BundleManifestScanner.
     *
     * @param manifestFilter dictionary filter used to filter manifest entries. Cannot be null.
     *
     * @throws NullArgumentException if dictionary filter is null
     */
    public BundleManifestScanner( final ManifestFilter manifestFilter )
    {
        NullArgumentException.validateNotNull( manifestFilter, "Dictionary filter" );

        m_manifestFilter = manifestFilter;
    }

    /**
     * Scans bundle manifest for matches against configured manifest headers.
     *
     * @param bundle bundle to be scanned
     *
     * @return list of matching manifest entries
     *
     * @see BundleScanner#scan(Bundle)
     */
    public List<ManifestEntry> scan( final Bundle bundle )
    {
        NullArgumentException.validateNotNull( bundle, "Bundle" );

        final Dictionary bundleHeaders = bundle.getHeaders();
        if( bundleHeaders != null && !bundleHeaders.isEmpty() )
        {
            return asManifestEntryList( m_manifestFilter.match( dictionaryToMap( bundleHeaders ) ) );
        }
        else
        {
            return Collections.emptyList();
        }
    }

    /**
     * Converts a map of String/String to a list of manifest entries.
     *
     * @param entries to be converted
     *
     * @return converted list
     */
    private static List<ManifestEntry> asManifestEntryList( final Map<String, String> entries )
    {
        final List<ManifestEntry> manifestEntries = new ArrayList<ManifestEntry>();
        if( entries != null && !entries.isEmpty() )
        {
            for( Map.Entry<String, String> entry : entries.entrySet() )
            {
                manifestEntries.add( new ManifestEntry( entry.getKey(), entry.getValue() ) );
            }
        }
        return manifestEntries;
    }

    /**
     * Converts a dictionary of String/String to a map.
     *
     * @param dictionary to be converted
     *
     * @return dictionary content as map
     *
     * @throws IllegalArgumentException if dictionary entries keys or values are not string
     */
    private static Map<String, String> dictionaryToMap( final Dictionary dictionary )
    {
        final Map<String, String> map = new HashMap<String, String>();
        try
        {
            // first try a shortcut
            if( dictionary instanceof Hashtable )
            {
                map.putAll( (Hashtable<String, String>) dictionary );
            }
            else
            {
                final Enumeration keys = dictionary.keys();
                while( keys.hasMoreElements() )
                {
                    final String key = (String) keys.nextElement();
                    map.put( key, (String) dictionary.get( key ) );
                }
            }
            return map;
        }
        catch( ClassCastException e )
        {
            throw new IllegalArgumentException( "Dictionary entries must have String keys and values" );
        }
    }


}
