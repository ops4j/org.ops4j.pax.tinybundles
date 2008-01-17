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
 * A bundle scanner can scan bundles for divers types of bundle entries.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, October 14, 2007
 */
public interface BundleScanner<T>
{

    /**
     * Scan a bundle for resources.
     *
     * @param bundle bundle to be scanned
     *
     * @return an array of found bundle resources. If no resource was found it should return an empty array.
     */
    List<T> scan( Bundle bundle );

}
