/*
 * Copyright 2013 Harald Wellmann
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import aQute.bnd.build.Container;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.DemoAnonymousInnerClass;
import org.ops4j.pax.tinybundles.internal.ClassDescriptor;
import org.ops4j.pax.tinybundles.internal.ClassFinder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Harald Wellmann
 */
public class ClassFinderTest {

    private ClassFinder finder;

    private void verify(final Collection<ClassDescriptor> descriptors, final boolean complete, final String... resources) {
        final Map<String, ClassDescriptor> descriptorMap = new HashMap<>();
        for (final ClassDescriptor descriptor : descriptors) {
            descriptorMap.put(descriptor.getResourcePath(), descriptor);
        }
        for (final String resource : resources) {
            final ClassDescriptor descriptor = descriptorMap.remove(resource);
            assertThat(String.format("%s not found", resource), descriptor, is(notNullValue()));
        }
        if (complete) {
            assertThat(descriptorMap.isEmpty(), is(true));
        }
    }

    @Before
    public void setUp() {
        finder = new ClassFinder();
    }

    @Test
    public void findAllEmbeddedClasses() throws IOException {
        final Class<?> clazz = Container.class;
        final Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses(clazz);
        verify(descriptors, true,
            "aQute/bnd/build/Container$1.class",
            "aQute/bnd/build/Container$TYPE.class"
        );
    }

    @Test
    public void findAnonymousClasses() throws IOException {
        final Class<?> clazz = Container.class;
        final Collection<ClassDescriptor> descriptors = finder.findAnonymousClasses(clazz);
        verify(descriptors, true,
            "aQute/bnd/build/Container$1.class"
        );
    }

    @Test
    public void findAllEmbeddedClassesFromJreClass() throws IOException {
        final Class<?> clazz = Pattern.class;
        final Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses(clazz);
        verify(descriptors, false,
            "java/util/regex/Pattern$1.class",
            "java/util/regex/Pattern$CharProperty.class",
            "java/util/regex/Pattern$Dollar.class"
        );
    }

    @Test
    public void findAnonymousClassesFromJreClass() throws IOException {
        final Class<?> clazz = Pattern.class;
        final Collection<ClassDescriptor> descriptors = finder.findAnonymousClasses(clazz);
        verify(descriptors, false,
            "java/util/regex/Pattern$1.class"
        );
    }

    @Test
    public void findAllEmbeddedClassesFromLocalClass() throws IOException {
        final Class<?> clazz = DemoAnonymousInnerClass.class;
        final Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses(clazz);
        verify(descriptors, true,
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1Local.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$NestedInnerClass.class"
        );
    }

    @Test
    public void findAnonymousClassesFromLocalClass() throws IOException {
        final Class<?> clazz = DemoAnonymousInnerClass.class;
        final Collection<ClassDescriptor> descriptors = finder.findAnonymousClasses(clazz);
        verify(descriptors, true,
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1Local.class"
        );
    }

}
