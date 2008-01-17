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

/**
 * Default implementation of ServiceCollectionListener.
 * Is a convenience class so subclasses  are implement just the necessary methods.
 *
 * @author Alin Dreghiciu
 * @since October 14, 2007
 */
public class DefaultServiceCollectionListener<T>
    implements ServiceCollectionListener<T>
{

    /**
     * Default implementation just returns true, so the service get's added to the collection.
     *
     * @see ServiceCollectionListener#serviceAdded(org.osgi.framework.ServiceReference,Object)
     */
    public boolean serviceAdded( ServiceReference serviceReference, T service )
    {
        return true;
    }

    /**
     * Default implementation does nothing.
     *
     * @see ServiceCollectionListener#serviceRemoved(org.osgi.framework.ServiceReference,Object)
     */
    public void serviceRemoved( ServiceReference serviceReference, T service )
    {
        //do nothing
    }

}
