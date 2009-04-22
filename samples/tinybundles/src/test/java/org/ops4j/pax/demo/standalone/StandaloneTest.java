/*
 * Copyright 2009 Toni Menzel.
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
package org.ops4j.pax.demo.standalone;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.osgi.framework.Constants;
import static org.ops4j.pax.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;

/**
 * This is a standalone test.
 * Practically you define your bundle in java api and finally will get the bundle written to disk.
 *
 * This is mainly for demonstrative purposes.
 * Real value comes when using tinybundles together with a pax exam test.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class StandaloneTest
{

    @Test
    public void myFirstTinyBundle()
        throws IOException
    {

        InputStream inp = newBundle()
            .addClass( MyFirstActivator.class )
            .addClass( HelloWorld.class )
            .addClass( HelloWorldImpl.class )
            .prepare(
                with()
                    .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
                    .set( Constants.EXPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                    .set( Constants.IMPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                    .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            ).build( asStream() );

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        assertEquals( "org.ops4j.pax.tinybundles.demo",
                      man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE )
        );
        assertEquals( "org.ops4j.pax.tinybundles.demo", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );

        jout.close();

    }
}
