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
package org.ops4j.pax.swissbox.lang;

import java.util.concurrent.Callable;

/**
 * Utilities for class loading.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, December 29, 2007
 */
public class ContextClassLoaderUtils
{

    private ContextClassLoaderUtils()
    {
        // utility class
    }

    /**
     * Executes a piece of code (callable.call) using a specific class loader set as context class loader.
     * If the curent thread context clas loader is already set, it will be restored after execution.
     *
     * @param classLoader clas loader to be used as context clas loader during call.
     * @param callable    piece of code to be executed using the clas loader
     *
     * @return return from callable
     *
     * @throws Exception re-thrown from callable
     */
    public static <V> V doWithClassLoader( final ClassLoader classLoader, final Callable<V> callable )
        throws Exception
    {
        Thread currentThread = null;
        ClassLoader backupClassLoader = null;
        try
        {
            if( classLoader != null )
            {
                currentThread = Thread.currentThread();
                backupClassLoader = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader( classLoader );
            }
            return callable.call();
        }
        finally
        {
            if( backupClassLoader != null )
            {
                currentThread.setContextClassLoader( backupClassLoader );
            }
        }
    }

}
