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

import org.osgi.service.blueprint.container.Converter;
import static org.ops4j.pax.swissbox.converter.helpers.ImmutableCompositeConverter.*;
import org.ops4j.pax.swissbox.converter.helpers.WrapperConverter;
import static org.ops4j.pax.swissbox.converter.java.lang.AssignableConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromNullConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromStringConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.FromStringToClassConverter.*;
import static org.ops4j.pax.swissbox.converter.java.lang.ToNumberConverter.*;
import org.ops4j.pax.swissbox.converter.loader.Loader;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class JavaLangConverter
    extends WrapperConverter
    implements Converter
{

    public static final JavaLangConverter INSTANCE = new JavaLangConverter();

    public JavaLangConverter()
    {
        delegate(
            immutableCompositeConverter(
                fromNullConverter(),
                assignableConverter(),
                toNumberConverter(),
                fromStringConverter()
            )
        );
    }

    public JavaLangConverter( final Loader loader )
    {
        delegate(
            immutableCompositeConverter(
                fromNullConverter(),
                assignableConverter(),
                toNumberConverter(),
                fromStringConverter(),
                fromStringToClassConverter( loader )
            )
        );
    }

    public static JavaLangConverter javaLangConverter()
    {
        return INSTANCE;
    }

    public static JavaLangConverter javaLangConverter( final Loader loader )
    {
        return new JavaLangConverter( loader );
    }

}