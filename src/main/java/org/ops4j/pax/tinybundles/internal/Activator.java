/*
 * Copyright 2023 Oliver Lietz
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
package org.ops4j.pax.tinybundles.internal;

import java.util.Objects;

import org.ops4j.pax.tinybundles.TinyBundlesFactory;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

@Header(
    name = Constants.BUNDLE_ACTIVATOR,
    value = "${@class}"
)
public class Activator implements BundleActivator {

    private final TinyBundlesFactory factory = new DefaultTinyBundlesFactory();

    private ServiceRegistration<TinyBundlesFactory> serviceRegistration;

    @Override
    public void start(final BundleContext context) {
        serviceRegistration = context.registerService(TinyBundlesFactory.class, factory, null);
    }

    @Override
    public void stop(final BundleContext context) {
        if (Objects.nonNull(serviceRegistration)) {
            serviceRegistration.unregister();
        }
    }

}
