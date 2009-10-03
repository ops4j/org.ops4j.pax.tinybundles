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
package org.ops4j.pax.swissbox.converter.java.lang;

import static java.lang.String.*;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ops4j.pax.swissbox.converter.GenericType;
import static org.ops4j.pax.swissbox.converter.java.lang.AssignableConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromNullConverter.*;
import org.ops4j.pax.swissbox.converter.loader.Loader;

/**
 * JAVADOC
 *
 * NOTICE: This class contains code originally developed by "Apache Geronimo Project", OSGi Blueprint Implementation.
 *
 * @author <a href="mailto:dev@geronimo.apache.org">Apache Geronimo Project</a>
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class FromStringToClassConverter
    implements Converter
{

    private final Loader loader;

    public FromStringToClassConverter( final Loader loader )
    {
        this.loader = loader;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( sourceObject instanceof String
                    && targetType != null
                    && ( targetType.getRawClass() == Class.class
                         || targetType.getRawClass() == ReifiedType.class ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                format(
                    "%s cannot convert an %s", FromStringConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertFromString( (String) sourceObject, targetType.getRawClass() );
    }

    public Object convertFromString( final String sourceObject,
                                     final Class targetType )
        throws Exception
    {

        if( ReifiedType.class == targetType )
        {
            try
            {
                return GenericType.parse( sourceObject, loader );
            }
            catch( ClassNotFoundException e )
            {
                throw new Exception( "Unable to convert", e );
            }
        }

        if( Class.class == targetType )
        {
            try
            {
                return GenericType.parse( sourceObject, loader ).getRawClass();
            }
            catch( ClassNotFoundException e )
            {
                throw new Exception( "Unable to convert", e );
            }
        }

        throw new Exception( format( "Unable to convert string [%s] to %s", sourceObject, targetType ) );
    }

    public static FromStringToClassConverter fromStringToClassConverter( final Loader loader )
    {
        return new FromStringToClassConverter( loader );
    }

}