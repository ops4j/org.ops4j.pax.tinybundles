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

import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ops4j.lang.PreConditionException;
import org.ops4j.pax.swissbox.lifecycle.AbstractLifecycle;

public class ReplaceableService<T>
    extends AbstractLifecycle
{

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog( ReplaceableService.class );

    /**
     * Bundle context. Constructor parameter. Cannot be null.
     */
    private final BundleContext m_context;
    /**
     * Service class. Constructor parameter. Cannot be null.
     */
    private final Class<T> m_serviceClass;
    /**
     * Listener for backing service related events. Constructor paramater. Can be null.
     */
    private final ReplaceableServiceListener<T> m_serviceListener;

    /**
     * Collection of tracked services.
     */
    private ServiceCollection<T> m_serviceCollection;
    /**
     * Current service. Null if there is no service available or replaceable service is not started.
     */
    private T m_service;

    /**
     * Creates a new replaceable service without a listener.
     *
     * @param context      bundle context
     * @param serviceClass class of the replaceable service
     */
    public ReplaceableService( final BundleContext context, final Class<T> serviceClass )
    {
        this( context, serviceClass, null );
    }

    /**
     * Creates a new replaceable service.
     *
     * @param context      bundle context
     * @param serviceClass class of the replaceable service
     * @param listener     a listener
     */
    public ReplaceableService( final BundleContext context, final Class<T> serviceClass,
                               final ReplaceableServiceListener<T> listener )
    {
        LOGGER.info( "Creating replaceable service for [" + serviceClass + "]" );

        PreConditionException.validateNotNull( context, "Context" );
        PreConditionException.validateNotNull( serviceClass, "Service class" );

        m_context = context;
        m_serviceClass = serviceClass;
        m_serviceListener = listener;
    }

    /**
     * Returne the current service.
     *
     * @return the current service.
     */
    public synchronized T getService()
    {
        return m_service;
    }

    /**
     * Sets the new service and notifies the listener that the service was changed.
     *
     * @param newService the new service
     */
    private synchronized void setService( final T newService )
    {
        if( m_service != newService )
        {
            LOGGER.info( "Service changed [" + m_service + "]  -> [" + newService + "]" );
            final T oldService = m_service;
            m_service = newService;
            if( m_serviceListener != null )
            {
                m_serviceListener.serviceChanged( oldService, m_service );
            }
        }
    }

    /**
     * Resolves a new service by serching the services collection for first available service.
     */
    private synchronized void resolveService()
    {
        T newService = null;
        final Iterator<T> it = m_serviceCollection.iterator();
        while( newService == null && it.hasNext() )
        {
            final T candidateService = it.next();
            if( !candidateService.equals( getService() ) )
            {
                newService = candidateService;
            }
        }
        setService( newService );
    }

    /**
     * Creates a service collection and starts it.
     *
     * @see AbstractLifecycle#onStart
     */
    @Override
    protected void onStart()
    {
        m_serviceCollection = new ServiceCollection<T>( m_context, m_serviceClass, new CollectionListener() );
        m_serviceCollection.start();
    }

    /**
     * Stops the service collection and releases resources.
     *
     * @see AbstractLifecycle#onStop
     */
    @Override
    protected void onStop()
    {
        if( m_serviceCollection != null )
        {
            m_serviceCollection.stop();
            m_serviceCollection = null;
        }
        setService( null );
    }

    /**
     * Service collection listener that will allow services to get registered and select always the first available.
     * If a service gets unregister will get next available from the collection.
     */
    private class CollectionListener
        extends DefaultServiceCollectionListener<T>
    {

        /**
         * Uses the new service if there is no current service
         *
         * @see ServiceCollectionListener#serviceAdded(org.osgi.framework.ServiceReference,Object)
         */
        @Override
        public boolean serviceAdded( final ServiceReference serviceReference, final T service )
        {
            if( getService() == null )
            {
                setService( service );
            }
            return true;
        }

        /**
         * If the service that is removed is the current service searches for a new one. Otherwise does nothing.
         *
         * @see ServiceCollectionListener#serviceRemoved(org.osgi.framework.ServiceReference,Object)
         */
        @Override
        public void serviceRemoved( final ServiceReference serviceReference, final T service )
        {
            if( service != null && service.equals( getService() ) )
            {
                resolveService();
            }
        }

    }

}
