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
package org.ops4j.pax.swissbox.samples.tinybundles.dp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import org.osgi.framework.Constants;
import org.ops4j.io.StreamUtils;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import static org.ops4j.pax.swissbox.tinybundles.dp.DP.*;
import org.ops4j.pax.swissbox.tinybundles.dp.BuildableDP;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class DeploymentPackageTest
{

    @Test
    // @Ignore
    public void myFirstDeploymentPackage()
        throws IOException
    {

        InputStream inp = newDeploymentPackage()
            .set( "DeploymentPackage-SymbolicName", "MyFirstDeploymentPackage" )
            .set( "DeploymentPackage-DeploymentPackage-Version", "1.0.0" )
            .addResource( "log4j.properties", getClass().getResourceAsStream( "/log4j.properties" ),
                          "log4j-properties-processor"
            )

            .addBundle( "t1.jar",
                        newBundle()
                            .addClass( MyFirstActivator.class )
                            .addClass( HelloWorld.class )
                            .addClass( HelloWorldImpl.class )
                            .prepare(
                            with()
                                .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
                                .set( Constants.EXPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                                .set( Constants.IMPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                                .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
                                .set( Constants.BUNDLE_VERSION, "1.0.0" )
                        )
            )
                //.addBundle( "t2.jar", "mvn:groupId/userId/version" )
            .build();

        File f = File.createTempFile( "dest", ".dp" );
        StreamUtils.copyStream( inp, new FileOutputStream( f ), true );

        JarInputStream jout = new JarInputStream( new FileInputStream( f ) );
        Manifest man = jout.getManifest();
        assertEquals( "application/vnd.osgi.dp", man.getMainAttributes().getValue( "Content-Type" ) );

        jout.close();

    }

    @Test
    @Ignore
    public void patchDP()
        throws IOException
    {

        BuildableDP original = newDeploymentPackage()
            .set( "DeploymentPackage-SymbolicName", "MyFirstDeploymentPackage" )
            .set( "DeploymentPackage-DeploymentPackage-Version", "1.0.0" )
            .addResource( "log4j.properties", getClass().getResourceAsStream( "/log4j.properties" ),
                          "log4j-properties-processor"
            )

            .addBundle( "t1.jar",
                        newBundle()
                            .addClass( MyFirstActivator.class )
                            .addClass( HelloWorld.class )
                            .addClass( HelloWorldImpl.class )
                            .prepare(
                            with()
                                .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
                                .set( Constants.EXPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                                .set( Constants.IMPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                                .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
                                .set( Constants.BUNDLE_VERSION, "1.0.0" )
                        )
            );

        InputStream fix = newFixPackage( original )
            .removeBundle( "t1.jar" ).build();

        File f = File.createTempFile( "dest", ".dp" );
        StreamUtils.copyStream( fix, new FileOutputStream( f ), true );

        JarInputStream jout = new JarInputStream( new FileInputStream( f ) );
        Manifest man = jout.getManifest();
        assertEquals( "application/vnd.osgi.dp", man.getMainAttributes().getValue( "Content-Type" ) );

        jout.close();

    }


}
