/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.swissbox.util.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Utilities related to bundles.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, January 11, 2008
 */
public class BundleUtils
{

    /**
     * Discovers the bundle context for a bundle. If the bundle is an 4.1.0 or greather bundle it should have a method
     * that just returns the bundle context. Otherwise uses reflection to look for an internal bundle context.
     *
     * @param bundle the bundle from which the bundle context is needed
     *
     * @return corresponding bundle context or null if bundle context cannot be discovered
     */
    public static BundleContext getBundleContext( final Bundle bundle )
    {
        try
        {
            // first try to find the getBundleContext method (OSGi spec >= 4.10)
            final Method method = bundle.getClass().getDeclaredMethod( "getBundleContext" );
            if( !method.isAccessible() )
            {
                method.setAccessible( true );
            }
            return (BundleContext) method.invoke( bundle );
        }
        catch( Exception e )
        {
            // then try to find a field in the bundle that looks like a bundle context
            try
            {
                final Field[] fields = bundle.getClass().getDeclaredFields();
                for( Field field : fields )
                {
                    if( BundleContext.class.isAssignableFrom( field.getType() ) )
                    {
                        if( !field.isAccessible() )
                        {
                            field.setAccessible( true );
                        }
                        return (BundleContext) field.get( bundle );
                    }
                }
            }
            catch( IllegalAccessException ignore )
            {
                // ignore
            }
        }
        // well, discovery failed
        return null;
    }

}
