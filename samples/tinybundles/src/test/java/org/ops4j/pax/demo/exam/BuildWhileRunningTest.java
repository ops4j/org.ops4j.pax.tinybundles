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
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Info;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;

/**
 * This is example will use TinyBundles at runtime (inside osgi itself).
 * You need TinyBundles at runtime in this case.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
@RunWith( JUnit4TestRunner.class )
public class BuildWhileRunningTest
{

    @Configuration
    public static Option[] configure()
    {
        return options(
            provision(
                mavenBundle()
                    .groupId( "org.ops4j.pax.swissbox" )
                    .artifactId( "pax-swissbox-tinybundles" )
                    .version( "0.2.1-SNAPSHOT" )
            )

        );
    }

    @Inject
    BundleContext context;

    @Test
    public void runMyService()
        throws BundleException, IOException
    {
        // first prepare and install a tinybundle:
        Bundle b = context.installBundle( "file:/dev/null/foo", newBundle().prepare( with().set(Constants.BUNDLE_SYMBOLICNAME,"MyFirstTinyBundle" )).build( asStream() ) );

        b.start();
        boolean found = false;

        // check if bundle was found:
        for( Bundle bundle : context.getBundles() )
        {
            if( bundle.getSymbolicName().equals( "MyFirstTinyBundle" ) )
            {
                found = true;
            }
        }
        assertTrue( found );

    }
}
