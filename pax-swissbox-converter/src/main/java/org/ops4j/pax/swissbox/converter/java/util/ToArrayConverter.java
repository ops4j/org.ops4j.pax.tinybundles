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
import java.lang.reflect.Array;
import java.util.Collection;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ops4j.pax.swissbox.converter.GenericType;
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
public class ToArrayConverter
    implements Converter
{

    private final Converter escape;

    public ToArrayConverter( final Converter escape )
    {

        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( targetType.getRawClass().isArray()
                    && ( sourceObject instanceof Collection
                         || sourceObject.getClass().isArray() ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                format(
                    "%s cannot convert an %s", ToArrayConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertToArray( sourceObject, targetType );
    }

    private Object convertToArray( final Object sourceObject,
                                   final ReifiedType targetType )
        throws Exception
    {
        Object toConvert = sourceObject;

        if( toConvert instanceof Collection )
        {
            toConvert = ( (Collection) toConvert ).toArray();
        }

        if( !toConvert.getClass().isArray() )
        {
            throw new Exception( format( "Unable to convert from [%s] to [%s]", sourceObject, targetType ) );
        }

        ReifiedType componentType;
        if( targetType.size() > 0 )
        {
            componentType = targetType.getActualTypeArgument( 0 );
        }
        else
        {
            componentType = new GenericType( targetType.getRawClass().getComponentType() );
        }

        final Object array = Array.newInstance( componentType.getRawClass(), Array.getLength( toConvert ) );

        for( int i = 0; i < Array.getLength( toConvert ); i++ )
        {
            try
            {
                Array.set( array, i, escape.convert( Array.get( toConvert, i ), componentType ) );
            }
            catch( Exception t )
            {
                throw new Exception(
                    "Unable to convert from " + toConvert + " to " + targetType + "(error converting array element)", t
                );
            }
        }
        return array;
    }

    public static ToArrayConverter toArrayConverter( final Converter escape )
    {
        return new ToArrayConverter( escape );
    }

}