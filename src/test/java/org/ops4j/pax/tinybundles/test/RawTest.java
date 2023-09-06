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

import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.internal.HelloWorldActivator;
import org.ops4j.pax.tinybundles.demo.internal.HelloWorldImpl;
import org.ops4j.pax.tinybundles.internal.Info;
import org.osgi.framework.Constants;

import static org.hamcrest.CoreMatchers.is;
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

    private static final String HEADER_CREATED_BY = "Created-By";

    private static final String HEADER_TOOL = "Tool";

    private static final String HEADER_TINYBUNDLES_VERSION = "TinybundlesVersion";

    private static final String HEADER_BUILT_BY = "Built-By";

    private static InputStream createTestBundle(final String caption) {
        return bundle()
            .add(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .set(Constants.BUNDLE_SYMBOLICNAME, caption)
            .set(Constants.EXPORT_PACKAGE, "demo")
            .set(Constants.IMPORT_PACKAGE, "demo")
            .set(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
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
        bundle().read(Files.newInputStream(file.toPath()));
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
        assertThat(attributes.getValue(HEADER_CREATED_BY), is("pax-tinybundles-" + Info.getPaxTinybundlesVersion()));
        assertThat(attributes.getValue(HEADER_TOOL), is("pax-tinybundles-" + Info.getPaxTinybundlesVersion()));
        assertThat(attributes.getValue(HEADER_TINYBUNDLES_VERSION), is("pax-tinybundles-" + Info.getPaxTinybundlesVersion()));
        assertThat(attributes.getValue(HEADER_BUILT_BY), is(System.getProperty("user.name")));
    }

    @Test
    public void modifyTest() throws IOException {
        // create a bundle
        final InputStream originalBundle = bundle()
            .add(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .set(Constants.BUNDLE_SYMBOLICNAME, "MyFirstTinyBundle")
            .set(Constants.EXPORT_PACKAGE, "demo")
            .set(Constants.IMPORT_PACKAGE, "demo")
            .set(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(rawBuilder());
        // create a new bundle from the original and modify it
        final InputStream modifiedBundle = bundle()
            .read(originalBundle)
            .set(Constants.EXPORT_PACKAGE, "bacon")
            .build(rawBuilder());
        final Attributes attributes = getManifest(modifiedBundle).getMainAttributes();
        assertThat(attributes.getValue(Constants.IMPORT_PACKAGE), is("demo"));
        assertThat(attributes.getValue(Constants.EXPORT_PACKAGE), is("bacon"));
    }

}
