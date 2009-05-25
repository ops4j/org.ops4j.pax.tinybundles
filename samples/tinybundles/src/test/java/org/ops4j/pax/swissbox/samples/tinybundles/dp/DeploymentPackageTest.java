package org.ops4j.pax.swissbox.samples.tinybundles.dp;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
import org.osgi.framework.Constants;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.swissbox.tinybundles.core.intern.Info;
import org.ops4j.pax.swissbox.tinybundles.dp.DP;
import static org.ops4j.pax.swissbox.tinybundles.dp.DP.*;
import org.ops4j.io.StreamUtils;

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

}
