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
package org.ops4j.pax.tinybundles.bnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.Test;
import org.ops4j.pax.tinybundles.core.BuildStrategy;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.ops4j.pax.tinybundles.core.intern.Info;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.ds.DsService;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;
import org.osgi.framework.Constants;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class BndTest {

    BuildStrategy getStrategy() {
        return TinyBundles.withBnd();
    }

    /**
     * Make sure BND independent headers are not affected by bnds activity.
     */
    @Test
    public void bndIndependendProps()
        throws IOException
    {
        InputStream inp = bundle( ).build( getStrategy() );

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        assertEquals( "Header Originally-Created-By", "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( "Originally-Created-By" ) );
        assertEquals( "Header Tool", "Bnd-4.2.0.201903051501", man.getMainAttributes().getValue( "Tool" ) );
        assertEquals( "Header TinybundlesVersion", "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( "TinybundlesVersion" ) );
        assertEquals( System.getProperty( "user.name" ), man.getMainAttributes().getValue( "Built-By" ) );
        jout.close();
    }

    @Test
    public void bndDeclarativeServices()
        throws IOException
    {
        InputStream inp = bundle( ).add(DsService.class).build( getStrategy() );

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        assertEquals( "OSGI-INF/org.ops4j.pax.tinybundles.demo.ds.DsService.xml", man.getMainAttributes().getValue( "Service-Component" ) );

        jout.close();
    }
    
    @Test
    public void createTestAllDefault()
        throws IOException
    {
        InputStream inp = bundle(  )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build( getStrategy() );

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        //calculated import
        assertEquals( "org.osgi.framework", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        // export nothing
        assertNull( man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        jout.close();
    }

    @Test
    public void createTestExport()
        throws IOException
    {
        InputStream inp = bundle( )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.EXPORT_PACKAGE, HelloWorld.class.getPackage().getName() )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build( getStrategy() );

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        //calculated import + re-import
        assertEquals( HelloWorld.class.getPackage().getName() + ",org.osgi.framework", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        // export:
        assertEquals( HelloWorld.class.getPackage().getName() + ";version=\"0.0.0\"", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        jout.close();
    }

    @Test
    public void embedDependency()
        throws IOException
    {
        InputStream inp = bundle( )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.EXPORT_PACKAGE, HelloWorld.class.getPackage().getName() )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .set( "Bundle-Classpath", ".,ant-1.8.1.jar" )
            .set( "Include-Resource","@/Users/tonit/devel/gradle/lib/ant-1.8.1.jar")
            .build(getStrategy());

        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        //calculated import + re-import
        assertEquals( HelloWorld.class.getPackage().getName() + ",org.osgi.framework", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        // export:
        assertEquals( HelloWorld.class.getPackage().getName() + ";version=\"0.0.0\"", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        System.out.println("Private: " + man.getMainAttributes().getValue("Private-Package"));
        System.out.println("Private: " + man.getMainAttributes().getValue("Bundle-Classpath"));
        ZipEntry entry ;
        while ((entry = jout.getNextEntry()) != null) {
            System.out.println("+" + entry.getName());
        }
        jout.close();
    }

    @Test
    public void modifyTest()
        throws IOException
    {
        InputStream inp1 = bundle(  )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build(getStrategy());

        // Add an export:
        InputStream inp2 = bundle(  ).read( inp1 )
            .set( Constants.EXPORT_PACKAGE, HelloWorld.class.getPackage().getName() )
            .set( Constants.IMPORT_PACKAGE, "*" )
            .set( "another", "property" )
            .build(getStrategy());

        // test output
        JarInputStream jout = new JarInputStream( inp2 );
        Manifest man = jout.getManifest();
        assertEquals( "property", man.getMainAttributes().getValue( "another" ) );

        // export
        assertEquals( HelloWorld.class.getPackage().getName() + ";version=\"0.0.0\"", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        // import should be re-calculated with new export
        assertEquals( HelloWorld.class.getPackage().getName() + ",org.osgi.framework", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );

        jout.close();
    }


}
