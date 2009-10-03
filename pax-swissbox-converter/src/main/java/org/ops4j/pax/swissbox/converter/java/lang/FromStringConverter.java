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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;
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
public class FromStringConverter
    implements Converter
{

    public static final FromStringConverter INSTANCE = new FromStringConverter();

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( sourceObject instanceof String
                    && targetType != null
                    && !( targetType.getRawClass() == Class.class )
                    && !( targetType.getRawClass() == ReifiedType.class ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                String.format(
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
        final Class type = unwrap( targetType );

        if( Locale.class == type )
        {
            String[] tokens = sourceObject.split( "_" );
            if( tokens.length == 1 )
            {
                return new Locale( tokens[ 0 ] );
            }
            else if( tokens.length == 2 )
            {
                return new Locale( tokens[ 0 ], tokens[ 1 ] );
            }
            else if( tokens.length == 3 )
            {
                return new Locale( tokens[ 0 ], tokens[ 1 ], tokens[ 2 ] );
            }
            else
            {
                throw new Exception( "Invalid locale string:" + sourceObject );
            }
        }

        if( Pattern.class == type )
        {
            return Pattern.compile( sourceObject );
        }

        if( Properties.class == type )
        {
            Properties props = new Properties();
            ByteArrayInputStream in = new ByteArrayInputStream( sourceObject.getBytes( "UTF8" ) );
            props.load( in );
            return props;
        }

        if( Boolean.class == type )
        {
            if( "yes".equalsIgnoreCase( sourceObject )
                || "true".equalsIgnoreCase( sourceObject )
                || "on".equalsIgnoreCase( sourceObject ) )
            {
                return Boolean.TRUE;
            }
            else if( "no".equalsIgnoreCase( sourceObject )
                     || "false".equalsIgnoreCase( sourceObject )
                     || "off".equalsIgnoreCase( sourceObject ) )
            {
                return Boolean.FALSE;
            }
            else
            {
                throw new RuntimeException( "Invalid boolean value: " + sourceObject );
            }
        }

        if( Integer.class == type )
        {
            return Integer.valueOf( sourceObject );
        }

        if( Short.class == type )
        {
            return Short.valueOf( sourceObject );
        }

        if( Long.class == type )
        {
            return Long.valueOf( sourceObject );
        }

        if( Float.class == type )
        {
            return Float.valueOf( sourceObject );
        }

        if( Double.class == type )
        {
            return Double.valueOf( sourceObject );
        }

        if( Character.class == type )
        {
            if( sourceObject.length() == 6 && sourceObject.startsWith( "\\u" ) )
            {
                int code = Integer.parseInt( sourceObject.substring( 2 ), 16 );
                return (char) code;
            }
            else if( sourceObject.length() == 1 )
            {
                return sourceObject.charAt( 0 );
            }
            else
            {
                throw new Exception( "Invalid value for character type: " + sourceObject );
            }
        }

        if( Byte.class == type )
        {
            return Byte.valueOf( sourceObject );
        }

        if( Enum.class.isAssignableFrom( type ) )
        {
            return Enum.valueOf( (Class<Enum>) type, sourceObject );
        }

        return createObject( sourceObject, type );
    }

    private Object createObject( final String sourceObject,
                                 final Class targetType )
        throws Exception
    {
        if( targetType.isInterface()
            || Modifier.isAbstract( targetType.getModifiers() ) )
        {
            throw new Exception(
                "Unable to convert value " + sourceObject + " to type " + targetType + ". Type " + targetType
                + " is an interface or an abstract class"
            );
        }

        try
        {
            final Constructor constructor = targetType.getConstructor( String.class );
            return constructor.newInstance( sourceObject );
        }
        catch( NoSuchMethodException e )
        {
            throw new RuntimeException( "Unable to convert to " + targetType );
        }
    }

    public static FromStringConverter fromStringConverter()
    {
        return INSTANCE;
    }

}