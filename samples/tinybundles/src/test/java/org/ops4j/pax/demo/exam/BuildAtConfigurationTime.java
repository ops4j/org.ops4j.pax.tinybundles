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
package org.ops4j.pax.demo.exam;

import java.io.IOException;
import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import static org.ops4j.pax.tinybundles.core.TinyBundles.*;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.MyFirstActivator;

/**
 * This is example will use TinyBundles in the configuration part only.
 * This means, that everything will be set up before executing the osgi process.
 * You don't need TinyBundles at runtime in this case.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
@RunWith( JUnit4TestRunner.class )
public class BuildAtConfigurationTime
{

    @Configuration
    public static Option[] configure()
    {
        return options(

            // install extra tiny bundles that are created on demand:
            provision(
                newBundle()
                    .addClass( MyFirstActivator.class )
                    .addClass( HelloWorld.class )
                    .addClass( HelloWorldImpl.class )
                    .prepare()
                    .set( Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle" )
                    .set( Constants.EXPORT_PACKAGE, "org.ops4j.pax.tinybundles.demo" )
                    .set( Constants.IMPORT_PACKAGE, "org.osgi.framework" )
                    .set( Constants.BUNDLE_ACTIVATOR, MyFirstActivator.class.getName() )
                    .build( asURL() ).toExternalForm()
            )
        );
    }

    @Inject
    BundleContext context;

    @Test
    public void runMyService()
        throws BundleException, IOException
    {
        // check and call service
        ServiceReference ref = context.getServiceReference( HelloWorld.class.getName() );
        assertNotNull( ref );
        HelloWorld service = (HelloWorld) context.getService( ref );
        assertNotNull( service );
        assertEquals( "Hello,Toni", service.sayHello( "Toni" ) );
        context.ungetService( ref );
    }
}
