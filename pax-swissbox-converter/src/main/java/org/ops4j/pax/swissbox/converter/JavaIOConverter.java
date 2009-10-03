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
import static org.ops4j.pax.swissbox.converter.java.io.ToInputStreamConverter.*;
import static org.ops4j.pax.swissbox.converter.java.io.ToOutputStreamConverter.*;
import static org.ops4j.pax.swissbox.converter.java.io.ToReaderConverter.*;
import static org.ops4j.pax.swissbox.converter.java.io.ToURIConverter.*;
import static org.ops4j.pax.swissbox.converter.java.io.ToURLConverter.*;
import static org.ops4j.pax.swissbox.converter.java.io.ToWriterConverter.*;

/**
 * JAVADOC
 *
 * @author Alin Dreghiciu
 */
public class JavaIOConverter
    extends WrapperConverter
    implements Converter
{

    public static final JavaIOConverter INSTANCE = new JavaIOConverter();

    public JavaIOConverter()
    {
        delegate(
            immutableCompositeConverter(
                toURLConverter(),
                toURIConverter(),
                toInputStreamConverter(),
                toOutputStreamConverter(),
                toReaderConverter(),
                toWriterConverter()
            )
        );
    }

    public JavaIOConverter( final Converter escape )
    {
        delegate(
            immutableCompositeConverter(
                toURLConverter( escape ),
                toURIConverter( escape ),
                toInputStreamConverter( escape ),
                toOutputStreamConverter( escape ),
                toReaderConverter( escape ),
                toWriterConverter( escape )
            )
        );
    }

    public static JavaIOConverter javaIOConverter()
    {
        return INSTANCE;
    }

    public static JavaIOConverter javaIOConverter( final Converter escape )
    {
        return new JavaIOConverter( escape );
    }

}