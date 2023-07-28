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

package org.ops4j.pax.tinybundles.core;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import aQute.bnd.build.Container;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.DemoAnonymousInnerClass;
import org.ops4j.pax.tinybundles.finder.ClassDescriptor;
import org.ops4j.pax.tinybundles.finder.ClassFinder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Harald Wellmann
 */
public class ClassFinderTest {

    private ClassFinder finder;

    @Before
    public void setUp() {
        finder = new ClassFinder();
    }

    @Test
    public void findAllEmbeddedClasses() throws IOException {
        Class<?> klass = Container.class;
        Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses(klass);
        verify(descriptors, true, "aQute/bnd/build/Container$1.class",
            "aQute/bnd/build/Container$TYPE.class");
    }

    @Test
    public void findAnonymousClasses() throws IOException {
        Class<?> klass = Container.class;
        Collection<ClassDescriptor> descriptors = finder.findAnonymousClasses(klass);
        verify(descriptors, true, "aQute/bnd/build/Container$1.class");
    }

    /**
     * @param descriptors
     * @param string
     * @param string2
     */
    private void verify(Collection<ClassDescriptor> descriptors, boolean complete, String... resources) {
        Map<String, ClassDescriptor> descriptorMap = new HashMap<String, ClassDescriptor>();
        for (ClassDescriptor descriptor : descriptors) {
            descriptorMap.put(descriptor.getResourcePath(), descriptor);
        }

        for (String resource : resources) {
            ClassDescriptor descriptor = descriptorMap.remove(resource);
            assertThat(resource + " not found", descriptor, is(notNullValue()));
        }
        if (complete) {
            assertThat(descriptorMap.isEmpty(), is(true));
        }
    }

    @Test
    public void findAllEmbeddedClassesFromJreClass() throws IOException {
        Class<?> klass = Pattern.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses(klass);
        verify(descriptors, false, "java/util/regex/Pattern$6.class",
            "java/util/regex/Pattern$CharProperty.class",
            "java/util/regex/Pattern$CharProperty$1.class");
    }

    @Test
    public void findAnonymousClassesFromJreClass() throws IOException {
        Class<?> klass = Pattern.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAnonymousClasses(klass);
        verify(descriptors, false, "java/util/regex/Pattern$6.class");
    }

    @Test
    public void findAllEmbeddedClassesFromLocalClass() throws IOException {
        Class<?> klass = DemoAnonymousInnerClass.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses(klass);
        verify(descriptors, true,
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1Local.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$NestedInnerClass.class");
    }

    @Test
    public void findAnonymousClassesFromLocalClass() throws IOException {
        Class<?> klass = DemoAnonymousInnerClass.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAnonymousClasses(klass);
        verify(descriptors, true,
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class",
            "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1Local.class");
    }

}
