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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.Test;
import org.ops4j.pax.tinybundles.core.intern.Info;
import org.ops4j.pax.tinybundles.demo.DemoAnonymousInnerClass;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;
import org.osgi.framework.Constants;

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
public class CoreTest {

    private static final String HEADER_CREATED_BY = "Created-By";
    private static final String HEADER_TOOL = "Tool";
    private static final String HEADER_TINYBUNDLES_VERSION = "TinybundlesVersion";
    private static final String HEADER_BUILT_BY = "Built-By";

    @Test
    public void testCreatingMultipleBundles()
        throws IOException, InterruptedException
    {
            for( int i = 0; i < 100; i++ ) {
            String symbolicName = "bundle_" + i;
            assertManifestAttributes(
                    createTestBundle( symbolicName ), 
                    symbolicName );

        }
    }

    private InputStream createTestBundle( String caption )
        throws IOException
    {
        return bundle()
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, caption )
            .set( Constants.EXPORT_PACKAGE, "demo" )
            .set( Constants.IMPORT_PACKAGE, "demo" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build();
    }

    private static void assertManifestAttributes( InputStream inp, String caption )
        throws IOException
    {
        Attributes attrs = getBundleManifest(inp).getMainAttributes();
        assertEquals( "demo", attrs.getValue( Constants.IMPORT_PACKAGE ) );
        assertEquals( "demo", attrs.getValue( Constants.EXPORT_PACKAGE ) );
        assertEquals( caption, attrs.getValue( Constants.BUNDLE_SYMBOLICNAME ) );
    }

    @Test
    public void testReadBundleWithoutManifestDoesNotThrowException() throws Exception {
    	File file = File.createTempFile("test", ".jar");
		createEmptyJar(file);
		TinyBundles.bundle().read(new FileInputStream(file));
    }

	private void createEmptyJar(File file) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(file);
    	JarOutputStream jarOut = new JarOutputStream(out);
    	jarOut.close();
	}
    
    @Test
    public void testActivatorAndSymbolicName()
        throws IOException
    {
        InputStream bundle = bundle()
                .symbolicName("my")
                .activator(MyFirstActivator.class)
                .build();
        Manifest man = getBundleManifest( bundle );
        assertEquals( "my", man.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME ) );
        assertEquals( MyFirstActivator.class.getName(), man.getMainAttributes().getValue( Constants.BUNDLE_ACTIVATOR ) );
    }
    
    @Test
    public void testDefaultPropertiesAreSetCorrectly()
        throws IOException
    {
        Manifest man = getBundleManifest( bundle().build() );
        assertEquals( "Header " + HEADER_CREATED_BY, "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( HEADER_CREATED_BY ) );
        assertEquals( "Header " + HEADER_TOOL, "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( HEADER_TOOL ) );
        assertEquals( "Header " + HEADER_TINYBUNDLES_VERSION, "pax-tinybundles-" + Info.getPaxTinybundlesVersion(), man.getMainAttributes().getValue( HEADER_TINYBUNDLES_VERSION ) );
        assertEquals( System.getProperty( "user.name" ), man.getMainAttributes().getValue( HEADER_BUILT_BY ) );
    }

    @Test
    public void modifyTest()
        throws IOException
    {
    	// create a bundle
        InputStream originalBundle = bundle()
            .add( MyFirstActivator.class )
            .add( HelloWorld.class )
            .add( HelloWorldImpl.class )
            .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
            .set( Constants.EXPORT_PACKAGE, "demo" )
            .set( Constants.IMPORT_PACKAGE, "demo" )
            .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
            .build();

        // create a new bundle from the original and modify it
        InputStream modifiedBundle = bundle().read( originalBundle )
            .set( Constants.EXPORT_PACKAGE, "bacon" )
            .build();

        Manifest modifiedMan = getBundleManifest(modifiedBundle);
        assertEquals( "demo", modifiedMan.getMainAttributes().getValue( Constants.IMPORT_PACKAGE ) );
        assertEquals( "bacon", modifiedMan.getMainAttributes().getValue( Constants.EXPORT_PACKAGE ) );
        
    }

    @Test
    public void allInnerClassesTest()
        throws IOException
    {
        TinyBundle b = bundle();
        b.add( DemoAnonymousInnerClass.class, InnerClassStrategy.ALL );
        b.build(
            new BuildStrategy() {
                public InputStream build( Map<String, URL> resources, Map<String, String> headers )
                {
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass.class" ) );
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class" ) );
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class" ) );
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class" ) );
                    return null;
                }
            }
        );
    }

    @Test
    public void anonymousInnerClassesTest()
        throws IOException
    {
        bundle().add( DemoAnonymousInnerClass.class, InnerClassStrategy.ANONYMOUS ).build(
            new BuildStrategy() {
                public InputStream build( Map<String, URL> resources, Map<String, String> headers )
                {
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass.class" ) );
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class" ) );
                    assertFalse( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class" ) );
                    assertFalse( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class" ) );
                    return null;
                }
            }
        );
    }

    @Test
    public void noInnerClassesTest()
        throws IOException
    {
        bundle().add( DemoAnonymousInnerClass.class, InnerClassStrategy.NONE ).build(
            new BuildStrategy() {
                public InputStream build( Map<String, URL> resources, Map<String, String> headers )
                {
                    assertTrue( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass.class" ) );
                    assertFalse( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class" ) );
                    assertFalse( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class" ) );
                    assertFalse( resources.containsKey( "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class" ) );
                    return null;
                }
            }

        );
    }

    private static Manifest getBundleManifest(InputStream bundle) throws IOException {
        JarInputStream jarInputStream = new JarInputStream( bundle );
        Manifest manifest = jarInputStream.getManifest();
        jarInputStream.close();
        return manifest;
    }

}