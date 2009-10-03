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
import java.util.Hashtable;
import java.util.Map;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
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
public class ToDictionaryConverter
    implements Converter
{

    private final Converter escape;

    public ToDictionaryConverter( final Converter escape )
    {

        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || Dictionary.class.isAssignableFrom( targetType.getRawClass() )
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
                    "%s cannot convert an %s", ToDictionaryConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertToDictionary( sourceObject, targetType );
    }

    private Object convertToDictionary( final Object sourceObject,
                                        final ReifiedType targetType )
        throws Exception
    {
        final ReifiedType keyType = targetType.getActualTypeArgument( 0 );
        final ReifiedType valueType = targetType.getActualTypeArgument( 1 );

        if( sourceObject instanceof Dictionary )
        {
            final Dictionary<Object, Object> converted = new Hashtable<Object, Object>();

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
            final Dictionary<Object, Object> converted = new Hashtable<Object, Object>();

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

    public static ToDictionaryConverter toDictionaryConverter( final Converter escape )
    {
        return new ToDictionaryConverter( escape );
    }

}