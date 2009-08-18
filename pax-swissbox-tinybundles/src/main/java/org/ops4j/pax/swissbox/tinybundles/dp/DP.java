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
package org.ops4j.pax.swissbox.tinybundles.dp;

import java.io.InputStream;
import org.ops4j.pax.swissbox.tinybundles.dp.intern.Bucket;
import org.ops4j.pax.swissbox.tinybundles.dp.intern.TinyDPImpl;
import org.ops4j.pax.swissbox.tinybundles.dp.intern.DPBuilder;
import org.ops4j.pax.swissbox.tinybundles.store.TemporaryBinaryStore;

/**
 * Factory to use a TinyDP instance to construct Deployment Packages.
 * 
 * Capabilities and final format will comply to
 * OSGi Compendium R4 Version 4.2, Deployment Admin Specification Version 1.1
 *
 * @author Toni Menzel (tonit)
 * @since May 23, 2009
 */
public class DP
{

    /**
     * Create a new deploymentpackage from scratch.
     *
     * @return an api to create a deployment package.
     */
    public static TinyDP newDeploymentPackage()
    {
        return new TinyDPImpl( new DPBuilder(), null, new Bucket(), new TemporaryBinaryStore() );
    }

    /**
     * Change an existing DeploymentPackage. (Fix Package)
     *
     * @param target Deployment Package you want to change.
     *
     * @return an extended api to create and change a deployment package.
     */
    public static TinyDP newFixPackage( InputStream target )
    {
        return new TinyDPImpl( new DPBuilder(), target, new Bucket(), new TemporaryBinaryStore() );

    }

}
