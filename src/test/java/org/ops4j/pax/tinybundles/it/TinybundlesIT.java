/*
 * Copyright 2023 Oliver Lietz.
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
package org.ops4j.pax.tinybundles.it;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.pax.tinybundles.TinyBundles;
import org.ops4j.pax.tinybundles.demo.DemoAnonymousInnerClass;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.ds.DsService;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldActivator;
import org.osgi.framework.Constants;

import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.tinybundles.TinyBundles.bndBuilder;
import static org.ops4j.pax.tinybundles.TinyBundles.rawBuilder;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TinybundlesIT extends TinybundlesTestSupport {

    private static final String BUNDLE_SYMBOLICNAME = "org.ops4j.pax.tinybundles.demo";

    private static void fill(final TinyBundle bundle) {
        bundle
            .add(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .add(DsService.class)
            .add(DemoAnonymousInnerClass.class)
            .add(Pattern.class)
            .set(Constants.BUNDLE_SYMBOLICNAME, BUNDLE_SYMBOLICNAME)
            .set(Constants.EXPORT_PACKAGE, "")
            .set(Constants.IMPORT_PACKAGE, "*")
            .set(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName());
    }

    @Test
    public void testRawBuilder() throws IOException {
        final TinyBundle bundle = TinyBundles.bundle();
        fill(bundle);
        final InputStream inputStream = bundle.build(rawBuilder());
        JarInputStream jar = new JarInputStream(inputStream);
        final Manifest manifest = jar.getManifest();
        assertEquals(BUNDLE_SYMBOLICNAME, manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME));
    }

    @Test
    public void testBndBuilder() throws IOException {
        final TinyBundle bundle = TinyBundles.bundle();
        fill(bundle);
        final InputStream inputStream = bundle.build(bndBuilder());
        JarInputStream jar = new JarInputStream(inputStream);
        final Manifest manifest = jar.getManifest();
        assertEquals(BUNDLE_SYMBOLICNAME, manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME));
        assertEquals("OSGI-INF/org.ops4j.pax.tinybundles.demo.ds.DsService.xml", manifest.getMainAttributes().getValue("Service-Component"));
    }

}
