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
package org.ops4j.pax.tinybundles.demo.intern;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class HelloWorldActivator implements BundleActivator {

    private ServiceRegistration ref;

    public void start(BundleContext bundleContext) throws Exception {
        Dictionary<String, String> dict = new Hashtable<String, String>();

        ref = bundleContext.registerService(HelloWorld.class.getName(), new HelloWorldImpl(), dict);
        System.out.println("waiting for 10seks..");
        Thread.sleep(10000);
        System.out.println("DONE");

    }

    public void stop(BundleContext bundleContext) throws Exception {
        ref.unregister();
    }

}
