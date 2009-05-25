package org.ops4j.pax.swissbox.samples.tinybundles.dp;

import java.io.IOException;
import java.io.InputStream;
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

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class DeploymentPackageTest
{

    @Test
    @Ignore
    public void myFirstDeploymentPackage()
        throws IOException
    {

        InputStream inp = newDeploymentPackage()
            .addBundle( "t1.jar", // this is the name of the resouce in DP
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
                        )
            )
            .addBundle( "t2.jar", "mvn:groupId/userId/version" )
            .addCustomizer( null )
            .build();

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

}
