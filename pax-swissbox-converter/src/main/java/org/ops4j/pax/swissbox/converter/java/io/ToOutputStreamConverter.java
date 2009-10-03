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

package org.ops4j.pax.swissbox.converter.java.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import static org.ops4j.pax.swissbox.converter.internal.Primitives.*;
import static org.ops4j.pax.swissbox.converter.java.lang.AssignableConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromNullConverter.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class ToOutputStreamConverter
    implements Converter
{

    public static final ToOutputStreamConverter INSTANCE = new ToOutputStreamConverter();

    private final Converter escape;

    public ToOutputStreamConverter()
    {
        this.escape = null;
    }

    public ToOutputStreamConverter( final Converter escape )
    {
        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( OutputStream.class.isAssignableFrom( unwrap( targetType.getRawClass() ) )
                    && ( sourceObject instanceof File
                         || sourceObject instanceof CharSequence
                         || ( escape != null
                              && escape.canConvert( sourceObject, new ReifiedType( File.class ) ) ) ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                String.format(
                    "%s cannot convert an %s", ToOutputStreamConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertToOutputStream( sourceObject, targetType );
    }

    public OutputStream convertToOutputStream( final Object sourceObject,
                                               final ReifiedType targetType )
        throws Exception
    {
        if( sourceObject instanceof File )
        {
            return new FileOutputStream( (File) sourceObject );
        }

        if( sourceObject instanceof CharSequence )
        {
            return new FileOutputStream( sourceObject.toString() );
        }

        if( escape != null )
        {
            if( escape.canConvert( sourceObject, new ReifiedType( File.class ) ) )
            {
                try
                {
                    return new FileOutputStream( (File) escape.convert( sourceObject, new ReifiedType( File.class ) ) );
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }
        }

        throw new Exception( String.format( "Unable to convert number %s to %s", sourceObject, targetType ) );
    }

    public static ToOutputStreamConverter toOutputStreamConverter()
    {
        return INSTANCE;
    }

    public static ToOutputStreamConverter toOutputStreamConverter( final Converter escape )
    {
        return new ToOutputStreamConverter( escape );
    }

}