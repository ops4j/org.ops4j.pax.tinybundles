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
package org.ops4j.pax.swissbox.samples.em.extender.internal;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.swissbox.extender.BundleManifestScanner;
import org.ops4j.pax.swissbox.extender.BundleObserver;
import org.ops4j.pax.swissbox.extender.BundleWatcher;
import org.ops4j.pax.swissbox.extender.ManifestEntry;
import org.ops4j.pax.swissbox.extender.RegexKeyManifestFilter;

/**
 * Activator for a manifest header based extender.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, February 09, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );

    /**
     * Bundle Watcher.
     */
    private BundleWatcher<ManifestEntry> m_bundleWatcher;

    public void start( final BundleContext bundleContext )
        throws Exception
    {
        LOG.info( "Starting Pax Swissbox Manifest Extender Example" );
        // create the bundle watcher
        // the bundle watcher will list all OSGi headers from started bundles 
        m_bundleWatcher = new BundleWatcher<ManifestEntry>(
            bundleContext,
            // scans manifest entries for headers with names starting with "Servlet"
            new BundleManifestScanner(
                new RegexKeyManifestFilter(
                    "Bundle-.*"
                )
            ),
            // customized for scanned entries
            new BundleObserver<ManifestEntry>()
            {
                // log found entries when bundle containing expected headers starts
                public void addingEntries( final Bundle bundle, final List<ManifestEntry> entries )
                {
                    LOG.info( "Starting bundle " + bundle.getSymbolicName() );
                    for( ManifestEntry entry : entries )
                    {
                        LOG.info( "-> " + entry );
                    }
                }

                //log found entries when bundle containing expected headers stops
                public void removingEntries( final Bundle bundle, final List<ManifestEntry> entries )
                {
                    LOG.info( "Stopping bundle " + bundle.getSymbolicName() );
                    for( ManifestEntry entry : entries )
                    {
                        LOG.info( "-> " + entry );
                    }
                }
            }
        );
        m_bundleWatcher.start();
    }

    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        LOG.info( "Stopping Pax Swissbox Manifest Extender Example" );
        m_bundleWatcher.stop();
    }
}
