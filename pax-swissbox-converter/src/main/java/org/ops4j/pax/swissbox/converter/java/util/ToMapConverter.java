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
package org.ops4j.pax.swissbox.converter.java.util;

import static java.lang.String.*;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ops4j.pax.swissbox.converter.internal.Reflection;
import static org.ops4j.pax.swissbox.converter.java.lang.AssignableConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromNullConverter.*;

/**
 * JAVADOC
 *
 * NOTICE: This class contains code originally developed by "Apache Geronimo Project", OSGi Blueprint Implementation.
 *
 * @author <a href="mailto:dev@geronimo.apache.org">Apache Geronimo Project</a>
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class ToMapConverter
    implements Converter
{

    private final Converter escape;

    public ToMapConverter( final Converter escape )
    {

        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || Map.class.isAssignableFrom( targetType.getRawClass() )
                  && ( sourceObject instanceof Map
                       || sourceObject instanceof Dictionary );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                format(
                    "%s cannot convert an %s", ToMapConverter.class.getSimpleName(), sourceObject.getClass()
                )
            );
        }

        if( fromNullConverter().canConvert( sourceObject, targetType ) )
        {
            return fromNullConverter().convert( sourceObject, targetType );
        }

        if( assignableConverter().canConvert( sourceObject, targetType ) )
        {
            return assignableConverter().convert( sourceObject, targetType );
        }

        return convertToMap( sourceObject, targetType );
    }

    private Object convertToMap( final Object sourceObject,
                                 final ReifiedType targetType )
        throws Exception
    {
        final ReifiedType keyType = targetType.getActualTypeArgument( 0 );
        final ReifiedType valueType = targetType.getActualTypeArgument( 1 );

        if( sourceObject instanceof Dictionary )
        {
            final Map<Object, Object> converted = createMap( getMapType( targetType.getRawClass() ) );

            final Dictionary toConvert = (Dictionary) sourceObject;

            for( Enumeration keys = toConvert.keys(); keys.hasMoreElements(); )
            {
                final Object key = keys.nextElement();

                try
                {
                    converted.put(
                        escape.convert( key, keyType ),
                        escape.convert( toConvert.get( key ), valueType )
                    );
                }
                catch( Exception e )
                {
                    throw new Exception(
                        format(
                            "Unable to convert from %s to %s (error converting map entry)", sourceObject, targetType
                        ),
                        e
                    );
                }
            }

            return converted;
        }

        if( sourceObject instanceof Map )
        {
            final Map<Object, Object> converted = createMap( getMapType( targetType.getRawClass() ) );

            for( Map.Entry<?, ?> entry : ( (Map<?, ?>) sourceObject ).entrySet() )
            {
                try
                {
                    converted.put(
                        escape.convert( entry.getKey(), keyType ),
                        escape.convert( entry.getValue(), valueType )
                    );
                }
                catch( Exception e )
                {
                    throw new Exception(
                        format(
                            "Unable to convert from %s to %s (error converting map entry)", sourceObject, targetType
                        ),
                        e
                    );
                }
            }

            return converted;
        }

        throw new Exception( format( "Unable to convert from %s to %s", sourceObject, targetType ) );
    }

    private static Map<Object, Object> createMap( final Class<? extends Map> type )
        throws Exception
    {
        return Reflection.newInstance( type );
    }

    private static Class<? extends Map> getMapType( final Class type )
    {
        if( Reflection.hasDefaultConstructor( type ) )
        {
            return type;
        }
        else if( SortedMap.class.isAssignableFrom( type ) )
        {
            return TreeMap.class;
        }
        else if( ConcurrentMap.class.isAssignableFrom( type ) )
        {
            return ConcurrentHashMap.class;
        }
        else
        {
            return LinkedHashMap.class;
        }
    }

    public static ToMapConverter toMapConverter( final Converter escape )
    {
        return new ToMapConverter( escape );
    }

}