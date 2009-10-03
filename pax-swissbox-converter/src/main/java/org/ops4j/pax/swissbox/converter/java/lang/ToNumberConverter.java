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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import static org.ops4j.pax.swissbox.converter.internal.Primitives.*;
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
public class ToNumberConverter
    implements Converter
{

    public static final ToNumberConverter INSTANCE = new ToNumberConverter();

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( sourceObject instanceof Number
                    && Number.class.isAssignableFrom( unwrap( targetType.getRawClass() ) ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                String.format(
                    "%s cannot convert an %s", ToNumberConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertToNumber( (Number) sourceObject, targetType.getRawClass() );
    }

    public Object convertToNumber( final Number sourceObject,
                                   final Class targetType )
        throws Exception
    {
        final Class type = unwrap( targetType );

        if( AtomicInteger.class == type )
        {
            return new AtomicInteger( (Integer) convertToNumber( sourceObject, Integer.class ) );
        }

        if( AtomicLong.class == type )
        {
            return new AtomicLong( (Long) convertToNumber( sourceObject, Long.class ) );
        }

        if( Integer.class == type )
        {
            return sourceObject.intValue();
        }

        if( Short.class == type )
        {
            return sourceObject.shortValue();
        }

        if( Long.class == type )
        {
            return sourceObject.longValue();
        }

        if( Float.class == type )
        {
            return sourceObject.floatValue();
        }

        if( Double.class == type )
        {
            return sourceObject.doubleValue();
        }

        if( Byte.class == type )
        {
            return sourceObject.byteValue();
        }

        if( BigInteger.class == type )
        {
            return new BigInteger( sourceObject.toString() );
        }

        if( BigDecimal.class == type )
        {
            return new BigDecimal( sourceObject.toString() );
        }

        throw new Exception( String.format( "Unable to convert number %s to %s", sourceObject, type ) );
    }

    public static ToNumberConverter toNumberConverter()
    {
        return INSTANCE;
    }

}