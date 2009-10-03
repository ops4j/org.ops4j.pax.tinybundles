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

import static java.lang.String.*;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class EmptyConverter
    implements Converter
{

    public boolean canConvert( final Object sourceObject,
                               final ReifiedType targetType )
    {
        return false;
    }

    public Object convert( final Object sourceObject,
                           final ReifiedType targetType )
        throws Exception
    {
        throw new Exception( format( "Unable to convert from %s to %s", sourceObject, targetType ) );
    }

    public static EmptyConverter noConversionConverter()
    {
        return new EmptyConverter();
    }

}