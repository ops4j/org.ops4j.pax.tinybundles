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
package org.ops4j.pax.swissbox.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

/**
 * A collection of tracked services.
 *
 * @author Alin Dreghiciu
 * @since October 14, 2007
 */
public class ServiceCollection<T>
    extends AbstractLifecycle
    implements Iterable<T>
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( ServiceCollection.class );

    /**
     * Bundle context. Constructor parameter. Cannot be null.
     */
    private final BundleContext m_context;
    /**
     * Service class. Constructor parameter. Cannot be null.
     */
    private final Class<T> m_serviceClass;
    /**
     * Listener for tracked services. Constructor parameter or if null a default one. Cannot be null.
     */
    private final ServiceCollectionListener<T> m_collectionListener;
    /**
     * Service tracker used to track services.
     */
    private Tracker m_serviceTracker;

    /**
     * Creates a new service tracker collection, using the specified service context and the default service collection
     * listener.
     *
     * @param context      bundle context
     * @param serviceClass class of the services to be tracked
     */
    public ServiceCollection( final BundleContext context, final Class<T> serviceClass )
    {
        this( context, serviceClass, null );
    }

    /**
     * Creates a new service tracker collection, using the specified service context and listener.
     * Listener can be null, case when the default service collection listener is used.
     *
     * @param context            bundle context
     * @param serviceClass       class of the services to be tracked
     * @param collectionListener service events listener
     */
    public ServiceCollection( final BundleContext context, final Class<T> serviceClass,
                              final ServiceCollectionListener<T> collectionListener )
    {
        LOG.debug( "Creating service collection for [" + serviceClass + "]" );

        NullArgumentException.validateNotNull( context, "Context" );
        NullArgumentException.validateNotNull( serviceClass, "Service class" );

        m_context = context;
        m_serviceClass = serviceClass;
        if( collectionListener == null )
        {
            m_collectionListener = new DefaultServiceCollectionListener<T>();
        }
        else
        {
            m_collectionListener = collectionListener;
        }
    }

    /**
     * Returns an iterator over the tracked services at the call point in time.
     * Note that a susequent call can produce a different list of services as the services are dynamic.
     * If there are no services available returns an empty iterator.
     *
     * @see Iterable#iterator()
     */
    @SuppressWarnings( "unchecked" )
    public Iterator<T> iterator()
    {
        final List<T> services = new ArrayList<T>();
        if( m_serviceTracker != null )
        {
            final Object[] trackedServices = m_serviceTracker.getServices();
            if( trackedServices != null )
            {
                for( Object trackedService : trackedServices )
                {
                    services.add( (T) trackedService );
                }
            }
        }
        return Collections.unmodifiableCollection( services ).iterator();
    }

    /**
     * Creates a service tracker and opens it.
     *
     * @see AbstractLifecycle#onStart
     */
    @Override
    protected void onStart()
    {
        m_serviceTracker = new Tracker( m_context, m_serviceClass );
        m_serviceTracker.open();
    }

    /**
     * Closes the service tracker and releases resources.
     *
     * @see AbstractLifecycle#onStop
     */
    @Override
    protected void onStop()
    {
        if( m_serviceTracker != null )
        {
            m_serviceTracker.close();
            m_serviceTracker = null;
        }
    }

    /**
     * Trackes services and handlers adding/removing.
     */
    private class Tracker
        extends ServiceTracker
    {

        public Tracker( final BundleContext context, final Class<T> serviceClass )
        {
            super( context, serviceClass.getName(), null );
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public Object addingService( final ServiceReference serviceReference )
        {
            LOG.debug( "Added service with reference [" + serviceReference + "]" );
            T service = null;
            try
            {
                service = (T) super.addingService( serviceReference );
                LOG.debug( "Related service [" + service + "]" );
                if( service != null )
                {
                    if( !m_collectionListener.serviceAdded( serviceReference, service ) )
                    {
                        super.removedService( serviceReference, service );
                        LOG.trace(
                            "Service [" + service + "] dropped as requested by listener [" + m_collectionListener + "]"
                        );
                        service = null;
                    }
                }
            }
            catch( RuntimeException e )
            {
                if( service != null )
                {
                    super.removedService( serviceReference, service );
                    LOG.debug(
                        "Service [" + service + "] dropped due to exception [" + e.getClass() + ":" + e.getMessage()
                        + "]"
                    );
                    throw e;
                }
            }
            return service;
        }

        @Override
        @SuppressWarnings( "unchecked" )
        public void removedService( final ServiceReference serviceReference, final Object service )
        {
            LOG.debug( "Removed service [" + service + "]" );
            // if one of the listenres is throwing an exception we will still remove it
            try
            {
                m_collectionListener.serviceRemoved( serviceReference, (T) service );
            }
            catch( Throwable ignore )
            {
                LOG.warn( "Ignored exception from collection listener", ignore );
            }
            super.removedService( serviceReference, service );
        }
    }

}
