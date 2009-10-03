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

import java.util.HashMap;
import java.util.Map;

/**
 * JAVADOC
 *
 * NOTICE: This class contains code originally developed by "Apache Geronimo Project", OSGi Blueprint Implementation.
 *
 * @author <a href="mailto:dev@geronimo.apache.org">Apache Geronimo Project</a>
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class Primitives
{

    private static final Map<String, Class> PRIMITIVES = new HashMap<String, Class>();

    static
    {
        PRIMITIVES.put( "int", int.class );
        PRIMITIVES.put( "integer", int.class );
        PRIMITIVES.put( "short", short.class );
        PRIMITIVES.put( "long", long.class );
        PRIMITIVES.put( "byte", byte.class );
        PRIMITIVES.put( "char", char.class );
        PRIMITIVES.put( "character", char.class );
        PRIMITIVES.put( "float", float.class );
        PRIMITIVES.put( "double", double.class );
        PRIMITIVES.put( "boolean", boolean.class );
    }

    private static final Map<Class, Class> PRIMITIVES_WRAPPERS = new HashMap<Class, Class>();

    static
    {
        PRIMITIVES_WRAPPERS.put( byte.class, Byte.class );
        PRIMITIVES_WRAPPERS.put( short.class, Short.class );
        PRIMITIVES_WRAPPERS.put( char.class, Character.class );
        PRIMITIVES_WRAPPERS.put( int.class, Integer.class );
        PRIMITIVES_WRAPPERS.put( long.class, Long.class );
        PRIMITIVES_WRAPPERS.put( float.class, Float.class );
        PRIMITIVES_WRAPPERS.put( double.class, Double.class );
        PRIMITIVES_WRAPPERS.put( boolean.class, Boolean.class );
    }

    private Primitives()
    {
        // utility class
    }

    public static Class unwrap( final Class clazz )
    {
        final Class unwrapped = PRIMITIVES_WRAPPERS.get( clazz );
        return unwrapped != null ? unwrapped : clazz;
    }

    public static boolean isPrimitive( final String clazz )
    {
        return PRIMITIVES.containsKey( clazz == null ? null : clazz.trim() );
    }

    public static Class primitive( final String clazz )
    {
        return PRIMITIVES.get( clazz == null ? null : clazz.trim() );
    }

}