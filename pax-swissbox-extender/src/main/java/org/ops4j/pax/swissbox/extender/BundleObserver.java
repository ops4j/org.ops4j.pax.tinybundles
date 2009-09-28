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

import java.util.List;
import org.osgi.framework.Bundle;

/**
 * Observer for watched bundles entries.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, December 26, 2007
 */
public interface BundleObserver<T>
{

    /**
     * Called when a bundle gets started and contains the desired entries.
     *
     * @param bundle  started bundle
     * @param entries list of watched entries
     */
    void addingEntries( Bundle bundle, List<T> entries );

    /**
     * Called when a bundle gets stopped.
     *
     * @param bundle  stopped bundle
     * @param entries list of watched entries for that bundle
     */
    void removingEntries( Bundle bundle, List<T> entries );

}
