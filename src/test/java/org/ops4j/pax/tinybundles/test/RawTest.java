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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.internal.HelloWorldActivator;
import org.ops4j.pax.tinybundles.demo.internal.HelloWorldImpl;
import org.ops4j.pax.tinybundles.internal.Info;
import org.osgi.framework.Constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.ops4j.pax.tinybundles.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.TinyBundles.rawBuilder;
import static org.ops4j.pax.tinybundles.test.JarHelper.createEmptyJar;
import static org.ops4j.pax.tinybundles.test.JarHelper.getManifest;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class RawTest {

    private static InputStream createTestBundle(final String symbolicName) {
        return bundle()
            .addClass(HelloWorldActivator.class)
            .addClass(HelloWorld.class)
            .addClass(HelloWorldImpl.class)
            .setHeader(Constants.BUNDLE_SYMBOLICNAME, symbolicName)
            .setHeader(Constants.EXPORT_PACKAGE, "demo")
            .setHeader(Constants.IMPORT_PACKAGE, "demo")
            .setHeader(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(rawBuilder());
    }

    @Test
    public void testCreatingMultipleBundles() throws IOException {
        for (int i = 0; i < 100; i++) {
            final String symbolicName = "bundle_" + i;
            final InputStream bundle = createTestBundle(symbolicName);
            final Attributes attributes = getManifest(bundle).getMainAttributes();
            assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is("demo"));
            assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is("demo"));
            assertThat(attributes.getValue(Constants.BUNDLE_SYMBOLICNAME), is(symbolicName));
        }
    }

    @Test
    public void testReadBundleWithoutManifestDoesNotThrowException() throws Exception {
        final File file = File.createTempFile("test", ".jar");
        file.deleteOnExit();
        createEmptyJar(file);
        final JarInputStream jarIn = new JarInputStream(Files.newInputStream(file.toPath()));
        bundle().readIn(jarIn);
    }

    @Test
    public void testActivatorAndSymbolicName() throws IOException {
        final InputStream bundle = bundle()
            .symbolicName("my")
            .activator(HelloWorldActivator.class)
            .build(rawBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        assertThat(attributes.getValue(Constants.BUNDLE_SYMBOLICNAME), is("my"));
        assertThat(attributes.getValue(Constants.BUNDLE_ACTIVATOR), is(HelloWorldActivator.class.getName()));
    }

    @Test
    public void testDefaultPropertiesAreSetCorrectly() throws IOException {
        final InputStream bundle = bundle()
            .build(rawBuilder());
        final Attributes attributes = getManifest(bundle).getMainAttributes();
        assertThat("Manifest Header: Created-By", attributes.getValue("Created-By"), is(nullValue()));
        assertThat("Manifest Header: Built-By", attributes.getValue("Built-By"), is(nullValue()));
        assertThat("Manifest Header: Pax-TinyBundles", attributes.getValue("Pax-TinyBundles"), is(Info.getPaxTinybundlesVersion()));
    }

    @Test
    public void modifyTest() throws IOException {
        // create a bundle
        final InputStream originalBundle = bundle()
            .addClass(HelloWorldActivator.class)
            .addClass(HelloWorld.class)
            .addClass(HelloWorldImpl.class)
            .setHeader(Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle")
            .setHeader(Constants.EXPORT_PACKAGE, "demo")
            .setHeader(Constants.IMPORT_PACKAGE, "demo")
            .setHeader(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(rawBuilder());
        // create a new bundle from the original and modify it
        final InputStream modifiedBundle = bundle()
            .readIn(new JarInputStream(originalBundle))
            .setHeader(Constants.EXPORT_PACKAGE, "bacon")
            .build(rawBuilder());
        final Attributes attributes = getManifest(modifiedBundle).getMainAttributes();
        assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is("demo"));
        assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is("bacon"));
    }

}
