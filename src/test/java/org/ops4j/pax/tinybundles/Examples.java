package org.ops4j.pax.tinybundles;

import java.io.InputStream;

import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.HelloWorld;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldActivator;
import org.ops4j.pax.tinybundles.demo.intern.HelloWorldImpl;
import org.osgi.framework.Constants;

import static org.ops4j.pax.tinybundles.TinyBundles.bndBuilder;
import static org.ops4j.pax.tinybundles.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.TinyBundles.rawBuilder;

/**
 * Simple examples that showcase the regular usage of Tinybundles.
 */
@SuppressWarnings("unused")
public class Examples {

    @Test
    public void testExampleSimple() {
        InputStream inp = bundle()
            .add(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .set(Constants.BUNDLE_SYMBOLICNAME, "Hello World Bundle")
            .set(Constants.EXPORT_PACKAGE, "demo")
            .set(Constants.IMPORT_PACKAGE, "demo")
            .set(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build();
    }

    @Test
    public void testExampleActivator() {
        InputStream inp = bundle()
            .activator(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .symbolicName("Hello World Bundle")
            .set(Constants.EXPORT_PACKAGE, "demo")
            .set(Constants.IMPORT_PACKAGE, "demo")
            .build();
    }

    @Test
    public void testExampleWithExplicitBuilder() {
        InputStream inp = bundle()
            .add(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .set(Constants.BUNDLE_SYMBOLICNAME, "Hello World Bundle")
            .set(Constants.EXPORT_PACKAGE, "demo")
            .set(Constants.IMPORT_PACKAGE, "demo")
            .set(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(rawBuilder());
    }

    @Test
    public void testExampleWithBNDBuilder() {
        InputStream inp = bundle()
            .add(HelloWorldActivator.class)
            .add(HelloWorld.class)
            .add(HelloWorldImpl.class)
            .set(Constants.BUNDLE_SYMBOLICNAME, "Hello World Bundle")
            .set(Constants.EXPORT_PACKAGE, "")
            .set(Constants.IMPORT_PACKAGE, "*")
            .set(Constants.BUNDLE_ACTIVATOR, HelloWorldActivator.class.getName())
            .build(bndBuilder());
    }

}
