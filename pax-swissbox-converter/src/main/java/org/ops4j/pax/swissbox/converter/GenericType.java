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
package org.ops4j.pax.swissbox.converter;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ops4j.pax.swissbox.converter.internal.Primitives;
import org.ops4j.pax.swissbox.converter.loader.Loader;

/**
 * JAVADOC
 *
 * NOTICE: This class contains code originally developed by "Apache Geronimo Project", OSGi Blueprint Implementation.
 *
 * @author <a href="mailto:dev@geronimo.apache.org">Apache Geronimo Project</a>
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 */
public class GenericType
    extends ReifiedType
{

    private static final GenericType[] EMPTY = new GenericType[0];

    private final GenericType[] parameters;

    public GenericType( final Type type )
    {
        this( getConcreteClass( type ), parametersOf( type ) );
    }

    public GenericType( final Class clazz,
                        final GenericType... parameters )
    {
        super( clazz );

        this.parameters = parameters;
    }

    @Override
    public ReifiedType getActualTypeArgument( final int index )
    {
        if( parameters.length == 0 )
        {
            return super.getActualTypeArgument( index );
        }
        return parameters[ index ];
    }

    @Override
    public int size()
    {
        return parameters.length;
    }

    @Override
    public String toString()
    {
        final Class clazz = getRawClass();

        if( clazz.isArray() )
        {
            if( parameters.length > 0 )
            {
                return parameters[ 0 ].toString() + "[]";
            }
            else
            {
                return clazz.getComponentType().getName() + "[]";
            }
        }

        if( parameters.length > 0 )
        {
            final StringBuilder sb = new StringBuilder();
            sb.append( clazz.getName() );
            sb.append( "<" );
            for( int i = 0; i < parameters.length; i++ )
            {
                if( i > 0 )
                {
                    sb.append( "," );
                }
                sb.append( parameters[ i ].toString() );
            }
            sb.append( ">" );
            return sb.toString();
        }

        return clazz.getName();
    }

    public static GenericType parse( final String type,
                                     final Loader loader )
        throws ClassNotFoundException, IllegalArgumentException
    {
        assert type != null : "Type must be specified (cannot be null)";
        assert loader != null : "Loader must be specified (cannot be null)";

        final String localType = type.trim();

        // Check if this is an array
        if( localType.endsWith( "[]" ) )
        {
            final GenericType parsedType = parse( localType.substring( 0, localType.length() - 2 ), loader );
            return new GenericType( Array.newInstance( parsedType.getRawClass(), 0 ).getClass(), parsedType );
        }

        // Check if this is a generic
        int genericIndex = localType.indexOf( '<' );
        if( genericIndex > 0 )
        {
            if( !localType.endsWith( ">" ) )
            {
                throw new IllegalArgumentException( "Can not load type: " + localType );
            }

            final GenericType baseType = parse( localType.substring( 0, genericIndex ), loader );

            final String[] params = localType.substring( genericIndex + 1, localType.length() - 1 ).split( "," );

            GenericType[] types = new GenericType[params.length];
            for( int i = 0; i < params.length; i++ )
            {
                types[ i ] = parse( params[ i ], loader );
            }
            return new GenericType( baseType.getRawClass(), types );
        }

        // Primitive
        if( Primitives.isPrimitive( localType ) )
        {
            return new GenericType( Primitives.primitive( localType ) );
        }

        return new GenericType( loader.loadClass( localType ) );
    }

    public boolean equals( final Object object )
    {
        if( !( object instanceof GenericType ) )
        {
            return false;
        }
        GenericType other = (GenericType) object;
        if( getRawClass() != other.getRawClass() )
        {
            return false;
        }
        if( parameters == null )
        {
            return ( other.parameters == null );
        }
        else
        {
            if( other.parameters == null )
            {
                return false;
            }
            if( parameters.length != other.parameters.length )
            {
                return false;
            }
            for( int i = 0; i < parameters.length; i++ )
            {
                if( !parameters[ i ].equals( other.parameters[ i ] ) )
                {
                    return false;
                }
            }
            return true;
        }
    }

    private static GenericType[] parametersOf( final Type type )
    {
        if( type instanceof Class )
        {
            Class clazz = (Class) type;
            if( clazz.isArray() )
            {
                GenericType t = new GenericType( clazz.getComponentType() );
                if( t.size() > 0 )
                {
                    return new GenericType[]{ t };
                }
                else
                {
                    return EMPTY;
                }
            }
            else
            {
                return EMPTY;
            }
        }

        if( type instanceof ParameterizedType )
        {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] parameters = pt.getActualTypeArguments();
            GenericType[] gts = new GenericType[parameters.length];
            for( int i = 0; i < gts.length; i++ )
            {
                gts[ i ] = new GenericType( parameters[ i ] );
            }
            return gts;
        }

        if( type instanceof GenericArrayType )
        {
            return new GenericType[]{ new GenericType( ( (GenericArrayType) type ).getGenericComponentType() ) };
        }

        if( type instanceof WildcardType )
        {
            return EMPTY;
        }

        if( type instanceof TypeVariable )
        {
            return EMPTY;
        }

        throw new RuntimeException( "Unknown type " + type );
    }

    private static Class<?> getConcreteClass( final Type type )
    {
        final Type collapsed = collapse( type );

        if( collapsed instanceof Class )
        {
            return (Class<?>) collapsed;
        }

        if( collapsed instanceof ParameterizedType )
        {
            return getConcreteClass( collapse( ( (ParameterizedType) collapsed ).getRawType() ) );
        }

        throw new RuntimeException( "Unknown type " + type );
    }

    private static Type collapse( final Type type )
    {
        if( type instanceof Class || type instanceof ParameterizedType )
        {
            return type;
        }
        else if( type instanceof TypeVariable )
        {
            return collapse( ( (TypeVariable<?>) type ).getBounds()[ 0 ] );
        }
        else if( type instanceof GenericArrayType )
        {
            Type arrayType = collapse( ( (GenericArrayType) type ).getGenericComponentType() );
            while( arrayType instanceof ParameterizedType )
            {
                arrayType = collapse( ( (ParameterizedType) arrayType ).getRawType() );
            }
            return Array.newInstance( (Class<?>) arrayType, 0 ).getClass();
        }
        else if( type instanceof WildcardType )
        {
            WildcardType wildcardType = (WildcardType) type;
            if( wildcardType.getLowerBounds().length == 0 )
            {
                return collapse( wildcardType.getUpperBounds()[ 0 ] );
            }
            else
            {
                return collapse( wildcardType.getLowerBounds()[ 0 ] );
            }
        }

        throw new RuntimeException( "Unknown type " + type );
    }

    public static GenericType genericType( final Type type )
    {
        return new GenericType( type );
    }

    public static GenericType genericType( final Class clazz,
                                           final GenericType... parameters )
    {
        return new GenericType( clazz, parameters );
    }

}
