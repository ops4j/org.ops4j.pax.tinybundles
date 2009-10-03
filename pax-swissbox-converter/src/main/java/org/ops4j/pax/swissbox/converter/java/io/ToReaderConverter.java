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
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
public class ToReaderConverter
    implements Converter
{

    public static final ToReaderConverter INSTANCE = new ToReaderConverter();

    private final Converter escape;

    public ToReaderConverter()
    {
        this.escape = null;
    }

    public ToReaderConverter( final Converter escape )
    {
        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || ( Reader.class.isAssignableFrom( unwrap( targetType.getRawClass() ) )
                    && ( sourceObject instanceof URL
                         || sourceObject instanceof URI
                         || sourceObject instanceof File
                         || sourceObject instanceof InputStream
                         || sourceObject instanceof CharSequence
                         || ( escape != null
                              && ( escape.canConvert( sourceObject, new ReifiedType( URL.class ) )
                                   || escape.canConvert( sourceObject, new ReifiedType( URI.class ) )
                                   || escape.canConvert( sourceObject, new ReifiedType( File.class ) )
                                   || escape.canConvert( sourceObject, new ReifiedType( InputStream.class ) ) ) ) ) );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                String.format(
                    "%s cannot convert an %s", ToReaderConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertToReader( sourceObject, targetType );
    }

    public Reader convertToReader( final Object sourceObject,
                                   final ReifiedType targetType )
        throws Exception
    {
        if( sourceObject instanceof URL )
        {
            return new InputStreamReader( ( (URL) sourceObject ).openStream() );
        }

        if( sourceObject instanceof URI )
        {
            return new InputStreamReader( ( (URI) sourceObject ).toURL().openStream() );
        }

        if( sourceObject instanceof File )
        {
            return new FileReader( (File) sourceObject );
        }

        if( sourceObject instanceof InputStream )
        {
            return new InputStreamReader( (InputStream) sourceObject );
        }

        if( sourceObject instanceof CharSequence )
        {
            try
            {
                return new InputStreamReader( new URL( sourceObject.toString() ).openStream() );
            }
            catch( MalformedURLException e )
            {
                return new FileReader( sourceObject.toString() );
            }
        }

        if( escape != null )
        {
            if( escape.canConvert( sourceObject, new ReifiedType( URL.class ) ) )
            {
                try
                {
                    return new InputStreamReader(
                        ( (URL) escape.convert( sourceObject, new ReifiedType( URL.class ) ) ).openStream()
                    );
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }

            if( escape.canConvert( sourceObject, new ReifiedType( URI.class ) ) )
            {
                try
                {
                    return new InputStreamReader(
                        ( (URI) escape.convert( sourceObject, new ReifiedType( URI.class ) ) ).toURL().openStream()
                    );
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }

            if( escape.canConvert( sourceObject, new ReifiedType( File.class ) ) )
            {
                try
                {
                    return new FileReader( (File) escape.convert( sourceObject, new ReifiedType( File.class ) ) );
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }

            if( escape.canConvert( sourceObject, new ReifiedType( InputStream.class ) ) )
            {
                try
                {
                    return new InputStreamReader(
                        (InputStream) escape.convert( sourceObject, new ReifiedType( InputStream.class ) )
                    );
                }
                catch( Exception ignore )
                {
                    // ignore
                }
            }
        }

        throw new Exception( String.format( "Unable to convert number %s to %s", sourceObject, targetType ) );
    }

    public static ToReaderConverter toReaderConverter()
    {
        return INSTANCE;
    }

    public static ToReaderConverter toReaderConverter( final Converter escape )
    {
        return new ToReaderConverter( escape );
    }

}