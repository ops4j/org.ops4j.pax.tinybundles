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

package org.ops4j.pax.swissbox.converter.internal;

import static java.lang.String.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class AbstractCompositeConverter
    implements Converter
{

    protected Collection<Converter> converters;

    public AbstractCompositeConverter( final Converter... converters )
    {
        this( converters == null ? null : Arrays.asList( converters ) );
    }

    public AbstractCompositeConverter( final Collection<Converter> converters )
    {
        this.converters = new ArrayList<Converter>();
        if( converters != null && !converters.isEmpty() )
        {
            for( Converter converter : converters )
            {
                assert converter != null : "Converters cannot contain null converters";
            }
            this.converters.addAll( converters );
        }
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        for( Converter converter : converters )
        {
            if( converter.canConvert( sourceObject, targetType ) )
            {
                return true;
            }
        }

        return false;
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        for( Converter converter : converters )
        {
            try
            {
                if( converter.canConvert( sourceObject, targetType ) )
                {
                    return converter.convert( sourceObject, targetType );
                }
            }
            catch( Exception e )
            {
                // try with next converter
            }
        }

        throw new Exception( format( "Unable to convert from %s to %s", sourceObject, targetType ) );
    }

}