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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
public class ToCollectionConverter
    implements Converter
{

    private final Converter escape;

    public ToCollectionConverter( final Converter escape )
    {

        this.escape = escape;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return fromNullConverter().canConvert( sourceObject, targetType )
               || assignableConverter().canConvert( sourceObject, targetType )
               || Collection.class.isAssignableFrom( targetType.getRawClass() )
                  && ( sourceObject instanceof Collection
                       || sourceObject.getClass().isArray() );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        if( !canConvert( sourceObject, targetType ) )
        {
            throw new Exception(
                format(
                    "%s cannot convert an %s", ToCollectionConverter.class.getSimpleName(), sourceObject.getClass()
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

        return convertToCollection( sourceObject, targetType );
    }

    private Object convertToCollection( final Object sourceObject,
                                        final ReifiedType targetType )
        throws Exception
    {
        final ReifiedType valueType = targetType.getActualTypeArgument( 0 );

        if( sourceObject.getClass().isArray() )
        {
            final Collection<Object> converted = createCollection( getCollectionType( targetType.getRawClass() ) );

            for( int i = 0; i < Array.getLength( sourceObject ); i++ )
            {
                try
                {
                    converted.add( escape.convert( Array.get( sourceObject, i ), valueType ) );
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

        if( sourceObject instanceof Collection )
        {
            final Collection<Object> converted = createCollection( getCollectionType( targetType.getRawClass() ) );

            for( Object item : (Collection) sourceObject )
            {
                try
                {
                    converted.add( convert( item, valueType ) );
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

    private static Collection<Object> createCollection( final Class<? extends Collection> type )
        throws Exception
    {
        return Reflection.newInstance( type );
    }

    private static Class<? extends Collection> getCollectionType( final Class type )
    {
        if( Reflection.hasDefaultConstructor( type ) )
        {
            return type;
        }
        else if( SortedSet.class.isAssignableFrom( type ) )
        {
            return TreeSet.class;
        }
        else if( Set.class.isAssignableFrom( type ) )
        {
            return LinkedHashSet.class;
        }
        else if( List.class.isAssignableFrom( type ) )
        {
            return ArrayList.class;
        }
        else if( Queue.class.isAssignableFrom( type ) )
        {
            return LinkedList.class;
        }
        else
        {
            return ArrayList.class;
        }
    }

    public static ToCollectionConverter toCollectionConverter( final Converter escape )
    {
        return new ToCollectionConverter( escape );
    }

}