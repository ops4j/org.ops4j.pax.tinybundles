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
package org.ops4j.pax.swissbox.samples.tinybundles.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import static org.junit.Assert.*;
import org.junit.Test;
import org.osgi.framework.Constants;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.swissbox.tinybundles.core.intern.Info;
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
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
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
        assertEquals( "pax-swissbox-tinybundles-" + Info.getPaxSwissboxTinybundlesVersion(),
                      man.getMainAttributes().getValue( "Created-By" )
        );
        assertEquals( "pax-swissbox-tinybundles-" + Info.getPaxSwissboxTinybundlesVersion(),
                      man.getMainAttributes().getValue( "Tool" )
        );

        assertEquals( System.getProperty( "user.name" ), man.getMainAttributes().getValue( "Built-By" ) );
        assertEquals( "pax-swissbox-tinybundles-" + Info.getPaxSwissboxTinybundlesVersion(),
                      man.getMainAttributes().getValue( "SwissboxTinybundlesVersion" )
        );
        jout.close();

    }

    @Test
    public void myFirstBndBundle()
        throws IOException
    {

        InputStream inp = newBundle()
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .prepare(
                withBnd()
                    .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
                    .set( Constants.EXPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                    .set( Constants.IMPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                    .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            ).build( asStream() );

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        assertEquals( "org.ops4j.pax.tinybundles.demo", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        assertEquals( "org.ops4j.pax.tinybundles.demo", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );

        // test standard headers output
        assertTrue( man.getMainAttributes().getValue( "Tool" ).startsWith( "Bnd" ) );
        assertEquals( System.getProperty( "user.name" ), man.getMainAttributes().getValue( "Built-By" ) );

        assertEquals( "pax-swissbox-tinybundles-" + Info.getPaxSwissboxTinybundlesVersion(),
                      man.getMainAttributes().getValue( "SwissboxTinybundlesVersion" )
        );

        jout.close();

    }
}
