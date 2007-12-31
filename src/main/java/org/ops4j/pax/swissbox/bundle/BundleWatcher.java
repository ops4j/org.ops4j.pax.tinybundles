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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.SynchronousBundleListener;
import org.ops4j.lang.PreConditionException;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * Watches bundles lifecycle events. Once a bundle becomes active a scanning process will be performed and each bundle
 * resource found during scanning will be registered. Once a bundle stops the registered resources for that bundle will
 * be unregistered.
 * If the bundle watcher is stopped all bundle resources will be unregistred.
 *
 * @author Alin Dreghiciu
 * @since October 14, 2007
 */
public class BundleWatcher<T>
    extends AbstractLifecycle
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( BundleWatcher.class );

    /**
     * Bundle context in use. Constructor paramater. Cannot be null.
     */
    private final BundleContext m_context;
    /**
     * Bundle scanner used to scan bundles. Constructor paramater. Cannot be null.
     */
    private final BundleScanner<T> m_scanner;
    /**
     * Bundle observers for scanned entries. Cannot be null but can be empty.
     */
    private final List<BundleObserver<T>> m_observers;
    /**
     * Mapping between bundle and scanned resources. Cannot be null.
     */
    private Map<Bundle, List<T>> m_mappings;
    /**
     * Syncronous listner for bundle events.
     */
    private BundleListener m_bundleListener;

    /**
     * Create a new bundle watcher.
     *
     * @param context a bundle context. Cannot be null.
     * @param scanner a bundle scanner. Cannot be null.
     */
    public BundleWatcher( final BundleContext context, final BundleScanner<T> scanner )
    {
        this( context, scanner, (BundleObserver<T>[]) null );
    }

    /**
     * Create a new bundle watcher.
     *
     * @param context   a bundle context. Cannot be null.
     * @param scanner   a bundle scanner. Cannot be null.
     * @param observers vararg list of observers
     */
    public BundleWatcher( final BundleContext context,
                          final BundleScanner<T> scanner,
                          final BundleObserver<T>... observers )
    {
        LOG.info( "Creating bundle watcher with scanner [" + scanner + "]..." );

        PreConditionException.validateNotNull( context, "Context" );
        PreConditionException.validateNotNull( scanner, "Bundle scanner" );

        m_context = context;
        m_scanner = scanner;
        m_observers = new ArrayList<BundleObserver<T>>();
        if( observers != null )
        {
            m_observers.addAll( Arrays.asList( observers ) );
        }
    }

    /**
     * Registers a listener for bundle events and scans already active bundles.
     */
    @Override
    protected void onStart()
    {
        m_mappings = new HashMap<Bundle, List<T>>();
        // listen to bundles events
        m_context.addBundleListener( m_bundleListener = new SynchronousBundleListener()
        {

            public void bundleChanged( final BundleEvent bundleEvent )
            {
                switch( bundleEvent.getType() )
                {
                    case BundleEvent.STARTED:
                        register( bundleEvent.getBundle() );
                        break;
                    case BundleEvent.STOPPED:
                        unregister( bundleEvent.getBundle() );
                        break;
                }
            }

        }
        );
        // scan already started bundles
        Bundle[] bundles = m_context.getBundles();
        if( bundles != null )
        {
            for( Bundle bundle : bundles )
            {
                if( bundle.getState() == Bundle.ACTIVE )
                {
                    register( bundle );
                }
            }
        }
    }

    /**
     * Unregister the bundle listener, releases resources
     */
    @Override
    protected void onStop()
    {
        m_context.removeBundleListener( m_bundleListener );
        final Bundle[] toBeRemoved = m_mappings.keySet().toArray( new Bundle[m_mappings.keySet().size()] );
        for( Bundle bundle : toBeRemoved )
        {
            unregister( bundle );
        }

        m_bundleListener = null;
        m_mappings = null;
    }

    /**
     * Scans entries using the bundle scanner and registers the result of scanning process.
     * Then notify the observers. If an exception appears during notification, it is ignored.
     *
     * @param bundle registered bundle
     */
    private void register( final Bundle bundle )
    {
        LOG.debug( "Scanning bundle [" + bundle.getSymbolicName() + "]" );
        final List<T> resources = m_scanner.scan( bundle );
        m_mappings.put( bundle, resources );
        if( resources != null && resources.size() > 0 )
        {
            LOG.debug( "Found resources " + resources );
            for( BundleObserver<T> observer : m_observers )
            {
                try
                {
                    observer.addingEntries( bundle, Collections.unmodifiableList( resources ) );
                }
                catch( Throwable ignore )
                {
                    LOG.error( "Ignored exception during register", ignore );
                }
            }
        }
    }

    /**
     * Unregisters each entry from the unregistered bundle by first notifing the observers. If an exception appears
     * during notification, it is ignored.
     *
     * @param bundle the unregistred bundle
     */
    private void unregister( final Bundle bundle )
    {
        LOG.debug( "Releasing bundle [" + bundle.getSymbolicName() + "]" );
        final List<T> resources = m_mappings.get( bundle );
        if( resources != null && resources.size() > 0 )
        {
            LOG.debug( "Unregistering " + resources );
            for( BundleObserver<T> observer : m_observers )
            {
                try
                {
                    observer.removingEntries( bundle, Collections.unmodifiableList( resources ) );
                }
                catch( Throwable ignore )
                {
                    LOG.error( "Ignored exception during unregister", ignore );
                }
            }
        }
        m_mappings.remove( bundle );
    }

}
