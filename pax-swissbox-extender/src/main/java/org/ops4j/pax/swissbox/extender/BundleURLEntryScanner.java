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

/**
 * A bundle scanner that uses Bundle.findEntries to search for entries and allows customization of created resources.
 *
 * @author Alin Dreghiciu
 * @since 0.2.1, March 08, 2007
 */
public abstract class BundleURLEntryScanner<T>
    extends BundleEntryScanner<T>
{

    /**
     * Delegate to BundleEntryScanner.
     *
     * @see BundleEntryScanner#BundleEntryScanner(String,String,boolean)
     */
    public BundleURLEntryScanner( final String path,
                                  final String filePattern,
                                  final boolean recurse )
    {
        super( path, filePattern, recurse );
    }

    /**
     * Delegate to BundleEntryScanner.
     *
     * @see BundleEntryScanner#BundleEntryScanner(String,String,boolean)
     */
    public BundleURLEntryScanner( final String pathManifestHeader,
                                  final String filePatternManifestHeader,
                                  final String recurseManifestHeader,
                                  final String path,
                                  final String filePattern,
                                  boolean recurse )
    {
        super( pathManifestHeader, filePatternManifestHeader, recurseManifestHeader, path, filePattern, recurse );
    }

    /**
     * @see BundleScanner#scan(org.osgi.framework.Bundle)
     */
    public List<T> scan( final Bundle bundle )
    {
        final List<T> resources = new ArrayList<T>();
        final Enumeration e = bundle.findEntries( getPath( bundle ), getFilePattern( bundle ), getRecurse( bundle ) );
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

    /**
     * Resource factory method.
     *
     * @param bundle bundle containing the entry
     * @param entry  entry URL
     *
     * @return created resource
     */
    protected abstract T createResource( Bundle bundle, URL entry );

}