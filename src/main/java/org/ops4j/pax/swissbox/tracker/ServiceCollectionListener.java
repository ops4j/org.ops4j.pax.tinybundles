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

import org.osgi.framework.ServiceReference;

public interface ServiceCollectionListener<T>
{

    /**
     * Notify that a service is added.
     *
     * @param serviceReference service reference of the added service
     * @param service          the service that was added
     *
     * @return true if the service should be retained in the collection, false otherwise
     */
    boolean serviceAdded( final ServiceReference serviceReference, final T service );

    /**
     * Notify that a service was removed.
     *
     * @param serviceReference service reference of the removed service
     * @param service          the service that was removed
     */
    void serviceRemoved( final ServiceReference serviceReference, final T service );

}
