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
package org.ops4j.pax.tinybundles.demo.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;

import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class HelloWorldActivator implements BundleActivator {

    private ServiceRegistration<HelloWorld> serviceRegistration;

    private final Logger logger = LoggerFactory.getLogger(HelloWorldActivator.class);

    public HelloWorldActivator() { //
    }

    @Override
    public void start(final BundleContext bundleContext) {
        logger.info("starting");
        final Dictionary<String, String> properties = new Hashtable<>();
        final HelloWorld service = new HelloWorldImpl();
        serviceRegistration = bundleContext.registerService(HelloWorld.class, service, properties);
        logger.info("serviceRegistration: {}", serviceRegistration);
    }

    @Override
    public void stop(final BundleContext bundleContext) {
        logger.info("stopping");
        if (!Objects.isNull(serviceRegistration)) {
            serviceRegistration.unregister();
        }
    }

}
