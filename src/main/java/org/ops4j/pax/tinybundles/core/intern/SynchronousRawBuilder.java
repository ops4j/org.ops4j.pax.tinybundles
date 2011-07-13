/*
 * Copyright 2011 Toni Menzel.
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
package org.ops4j.pax.tinybundles.core.intern;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alternative, early flushing version.
 * 
 * @deprecated
 */
public class SynchronousRawBuilder extends RawBuilder {

    private static Logger LOG = LoggerFactory.getLogger( SynchronousRawBuilder.class );
 
    public InputStream build( final Map<String, URL> resources,
                             final Map<String, String> headers )
    {
        LOG.debug( "make()" );
        try {
            File f = File.createTempFile( "temp", "bin" );
            JarOutputStream jarOut = new JarOutputStream( new FileOutputStream( f ) );
            try {
                build( resources, headers, jarOut );
            } finally {
                jarOut.close();
            }
            return new FileInputStream( f );

        } catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
