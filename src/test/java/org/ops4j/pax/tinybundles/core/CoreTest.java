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
package org.ops4j.pax.tinybundles.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import junit.framework.Assert;
import org.junit.Test;
import org.osgi.framework.Constants;
import org.ops4j.pax.tinybundles.core.intern.Info;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;

import static junit.framework.Assert.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.*;

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
public abstract class CoreTest {

    private static final String HEADER_CREATED_BY = "Created-By";
    private static final String HEADER_TOOL = "Tool";
    private static final String HEADER_TINYBUNDLES_VERSION = "TinybundlesVersion";
    private static final String HEADER_BUILT_BY = "Built-By";

    abstract BuildStrategy getStrategy();

    @Test
    public void doubleTest()
        throws IOException, InterruptedException
    {
        {
            for (int i = 0;i<100;i++) {
                makeBundle( "b" + i );
            }

        }
        Thread.currentThread().join( 2000 );

    }

    private void makeBundle( String caption )
        throws IOException
    {
        InputStream inp = bundle(  )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, caption )
            .set( Constants.EXPORT_PACKAGE, "demo" )
            .set( Constants.IMPORT_PACKAGE, "demo" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build(getStrategy());
        read( inp, caption );
    }



    private void read( InputStream inp, String caption )
        throws IOException
    {
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        Assert.assertEquals( "demo", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        Assert.assertEquals( "demo", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        Assert.assertEquals( caption, man.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME ) );

        jout.close();
    }

    // @Test
    public void createTest()
        throws IOException
    {
        InputStream inp = bundle(  )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.EXPORT_PACKAGE, "demo" )
            .set( Constants.IMPORT_PACKAGE, "demo" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build(getStrategy());
        inp.close();

        // test output
        /**
         JarInputStream jout = new JarInputStream( inp );
         Manifest man = jout.getManifest();
         Assert.assertEquals( "demo", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
         Assert.assertEquals( "demo", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
         jout.close();
         **/
    }

    // @Test
    public void defaultSetProps()
        throws IOException
    {
        InputStream inp = bundle(  ).build(getStrategy());
        // test output
        JarInputStream jout = new JarInputStream( inp );
        Manifest man = jout.getManifest();
        assertEquals( "Header " + HEADER_CREATED_BY, "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( HEADER_CREATED_BY ) );
        assertEquals( "Header " + HEADER_TOOL, "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( HEADER_TOOL ) );
        assertEquals( "Header " + HEADER_TINYBUNDLES_VERSION, "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( HEADER_TINYBUNDLES_VERSION ) );
        assertEquals( System.getProperty( "user.name" ), man.getMainAttributes().getValue( HEADER_BUILT_BY ) );
        jout.close();
    }

    // @Test
    public void modifyTest()
        throws IOException
    {
        InputStream inp1 = bundle(  )
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.EXPORT_PACKAGE, "demo" )
            .set( Constants.IMPORT_PACKAGE, "demo" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build(getStrategy());

        InputStream inp2 = bundle(  ).read( inp1 )
            .set( Constants.EXPORT_PACKAGE, "bacon" )
            .build(getStrategy());

        // test output
        JarInputStream jout = new JarInputStream( inp2 );
        Manifest man = jout.getManifest();
        Assert.assertEquals( "demo", man.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        Assert.assertEquals( "bacon", man.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        jout.close();
    }

}
