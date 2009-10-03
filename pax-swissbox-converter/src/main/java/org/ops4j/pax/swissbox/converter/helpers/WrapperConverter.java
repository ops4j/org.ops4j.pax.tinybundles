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

package org.ops4j.pax.swissbox.converter.helpers;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import static org.ops4j.pax.swissbox.converter.helpers.EmptyConverter.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class WrapperConverter
    implements Converter
{

    private Converter converter;

    public WrapperConverter()
    {
        converter = noConversionConverter();
    }

    public WrapperConverter( final Converter converter )
    {
        assert converter != null : "Converter must be specified (cannot be null)";

        this.converter = converter;
    }

    protected WrapperConverter delegate( final Converter converter )
    {
        assert converter != null : "Converter must be specified (cannot be null)";

        this.converter = converter;

        return this;
    }

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return converter.canConvert( sourceObject, targetType );
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        return converter.convert( sourceObject, targetType );
    }

    public static WrapperConverter wrapperConverter( final Converter converter )
    {
        return new WrapperConverter( converter );
    }

}