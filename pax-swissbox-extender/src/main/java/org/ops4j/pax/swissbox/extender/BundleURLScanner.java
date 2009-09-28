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
import org.osgi.framework.Bundle;

/**
 * A bundle scanner that creates URL's out of entries.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, December 26, 2007
 */
public class BundleURLScanner
    extends BundleURLEntryScanner<URL>
{

    /**
     * Delegate to BundleEntryScanner.
     *
     * @see BundleEntryScanner#BundleEntryScanner(String,String,boolean)
     */
    public BundleURLScanner( final String path,
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
    public BundleURLScanner( final String pathManifestHeader,
                             final String filePatternManifestHeader,
                             final String recurseManifestHeader,
                             final String path,
                             final String filePattern,
                             boolean recurse )
    {
        super( pathManifestHeader, filePatternManifestHeader, recurseManifestHeader, path, filePattern, recurse );
    }

    /**
     * Creates an URL for the entry (same as the parameter)
     *
     * @param bundle bundle that contains the entries
     * @param entry  found entry
     *
     * @return URL for the watched entry
     */
    @Override
    protected URL createResource( final Bundle bundle, final URL entry )
    {
        return entry;
    }

}
