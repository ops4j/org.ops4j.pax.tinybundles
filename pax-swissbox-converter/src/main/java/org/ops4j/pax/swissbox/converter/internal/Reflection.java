/*
 * Copyright 2009 Alin Dreghiciu.
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
package org.ops4j.pax.swissbox.converter.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * JAVADOC
 *
 * NOTICE: This class contains code originally developed by "Apache Geronimo Project", OSGi Blueprint Implementation.
 *
 * @author <a href="mailto:dev@geronimo.apache.org">Apache Geronimo Project</a>
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class Reflection
{

    public static boolean hasDefaultConstructor( final Class type )
    {
        if( !Modifier.isPublic( type.getModifiers() ) )
        {
            return false;
        }
        if( Modifier.isAbstract( type.getModifiers() ) )
        {
            return false;
        }
        Constructor[] constructors = type.getConstructors();
        for( Constructor constructor : constructors )
        {
            if( Modifier.isPublic( constructor.getModifiers() ) &&
                constructor.getParameterTypes().length == 0 )
            {
                return true;
            }
        }
        return false;
    }

    public static <T> T newInstance( final Class<T> clazz )
        throws Exception
    {
        return newInstance( null, clazz );
    }

    public static <T> T newInstance( final AccessControlContext acc,
                                     final Class<T> clazz )
        throws Exception
    {
        if( acc == null )
        {
            return clazz.newInstance();
        }
        else
        {
            try
            {
                return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<T>()
                    {
                        public T run()
                            throws Exception
                        {
                            return clazz.newInstance();
                        }
                    },
                    acc
                );
            }
            catch( PrivilegedActionException e )
            {
                throw e.getException();
            }
        }
    }

}
