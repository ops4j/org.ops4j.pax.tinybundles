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
package org.ops4j.pax.tinybundles.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.ds.DsService;
import org.ops4j.pax.tinybundles.demo.internal.HelloWorldActivator;
import org.ops4j.pax.tinybundles.demo.internal.HelloWorldImpl;
import org.ops4j.pax.tinybundles.internal.Info;
import org.osgi.framework.Constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.ops4j.pax.tinybundles.TinyBundles.bndBuilder;
import static org.ops4j.pax.tinybundles.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.test.JarHelper.getManifest;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class BndTest {

    private static final String BND = "Bnd-6.4.1.202306080939";

    /**
     * Make sure bnd independent headers are not affected by bnd's activity.
     */
    @Test
    public void bndCustomHeaders() throws IOException {
        final String createdBy = String.format("%s (%s)", System.getProperty("java.version"), System.getProperty("java.vendor"));
        final InputStream bundle = bundle()
            .build(bndBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        assertThat(attributes.getValue("Created-By"), is(createdBy));
        assertThat("Manifest Header: Built-By", attributes.getValue("Built-By"), is(nullValue()));
        assertThat("Manifest Header: Tool", attributes.getValue("Tool"), is(BND));
        assertThat("Manifest Header: Pax-TinyBundles", attributes.getValue("Pax-TinyBundles"), is(Info.getPaxTinybundlesVersion()));
    }

    @Test
    public void bndDeclarativeServices() throws IOException {
        final InputStream bundle = bundle()
            .addClass(DsService.class)
            .build(bndBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        assertThat(attributes.getValue("Service-Component"), is("OSGI-INF/org.ops4j.pax.tinybundles.demo.ds.DsService.xml"));
    }

    @Test
    public void createTestAllDefault() throws IOException {
        final InputStream bundle = bundle()
            .addClass(HelloWorldActivator.class)
            .addClass(HelloWorld.class)
            .addClass(HelloWorldImpl.class)
            .setHeader(Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle")
            .setHeader(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(bndBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        // calculated import
        assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is("org.osgi.framework,org.slf4j"));
        // export nothing
        assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is(nullValue()));
    }

    @Test
    public void createTestExport() throws IOException {
        final InputStream bundle = bundle()
            .addClass(HelloWorldActivator.class)
            .addClass(HelloWorld.class)
            .addClass(HelloWorldImpl.class)
            .setHeader(Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle")
            .setHeader(Constants.EXPORT_PACKAGE, HelloWorld.class.getPackage().getName())
            .setHeader(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(bndBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        // calculated import and re-import
        assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is(String.format("%s,org.osgi.framework,org.slf4j", HelloWorld.class.getPackage().getName())));
        // export
        assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is(String.format("%s;version=\"0.0.0\"", HelloWorld.class.getPackage().getName())));
    }

    @Test
    public void embedDependency() throws IOException {
        final InputStream bundle = bundle()
            .addClass(HelloWorldActivator.class)
            .addClass(HelloWorld.class)
            .addClass(HelloWorldImpl.class)
            .setHeader(Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle")
            .setHeader(Constants.EXPORT_PACKAGE, HelloWorld.class.getPackage().getName())
            .setHeader(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .setHeader("Bundle-Classpath", ".,ant-1.8.1.jar")
            .setHeader("Include-Resource", "@/Users/tonit/devel/gradle/lib/ant-1.8.1.jar")
            .build(bndBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        // calculated import and re-import
        assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is(String.format("%s,org.osgi.framework,org.slf4j", HelloWorld.class.getPackage().getName())));
        // export
        assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is(String.format("%s;version=\"0.0.0\"", HelloWorld.class.getPackage().getName())));
    }

    @Test
    public void modifyTest() throws IOException {
        final InputStream bundle1 = bundle()
            .addClass(HelloWorldActivator.class)
            .addClass(HelloWorld.class)
            .addClass(HelloWorldImpl.class)
            .setHeader(Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle")
            .setHeader(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(bndBuilder());
        // Add an export:
        final InputStream bundle2 = bundle()
            .readIn(new JarInputStream(bundle1))
            .setHeader(Constants.EXPORT_PACKAGE, HelloWorld.class.getPackage().getName())
            .setHeader(Constants.IMPORT_PACKAGE, "*")
            .setHeader("another", "property")
            .build(bndBuilder());
        // test output
        final Attributes attributes = getManifest(bundle2).getMainAttributes();
        assertThat(attributes.getValue("another"), is("property"));
        // import should be re-calculated with new export
        assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is(String.format("%s,org.osgi.framework,org.slf4j", HelloWorld.class.getPackage().getName())));
        // export
        assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is(String.format("%s;version=\"0.0.0\"", HelloWorld.class.getPackage().getName())));
    }

}
