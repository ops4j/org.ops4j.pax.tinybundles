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

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.osgi.framework.Constants;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;
import static org.ops4j.pax.swissbox.tinybundles.dp.DP.*;
import org.ops4j.pax.swissbox.tinybundles.dp.store.BinaryHandle;
import org.ops4j.pax.swissbox.tinybundles.dp.store.BinaryStore;
import org.ops4j.pax.swissbox.tinybundles.dp.store.TemporaryBinaryStore;
import org.ops4j.pax.swissbox.samples.tinybundles.DPTestingHelper;
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
    public void createDPAndCreateFixPack()
        throws IOException
    {
        System.setProperty( "java.protocol.handler.pkgs", "org.ops4j.pax.url" );
        BinaryStore<InputStream> cache = new TemporaryBinaryStore();

        BinaryHandle original = cache.store( newDeploymentPackage()
            .set( "DeploymentPackage-SymbolicName", "MyFirstDeploymentPackage" )
            .set( "DeploymentPackage-DeploymentPackage-Version", "1.0.0" )
            .addResource( "log4j.properties", getClass().getResourceAsStream( "/log4j.properties" ), "log4j-properties-processor" )
            .setBundle( "t1.jar", "mvn:org.ops4j.pax.url/pax-url-mvn/1.1.0-SNAPSHOT" )
            .setBundle( "t2.jar", "mvn:org.ops4j.pax.url/pax-url-wrap/1.1.0-SNAPSHOT" )
            .build()
        );

        DPTestingHelper.verifyDP( cache.load( original ), "log4j.properties", "t1.jar", "t2.jar" );
        DPTestingHelper.verifyBundleContents( cache.load( original ), "t1.jar", "t2.jar" );

        BinaryHandle fix = cache.store( newFixPackage( cache.load( original ) ).remove( "t1.jar" ).build() );

        DPTestingHelper.verifyDP( cache.load( fix ), "log4j.properties", "t2.jar" );
        DPTestingHelper.verifyBundleContents( cache.load( fix ), "t2.jar" );
    }


}
