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
import java.util.regex.Pattern;

import org.junit.Test;
import org.ops4j.pax.tinybundles.demo.DemoAnonymousInnerClass;
import org.ops4j.pax.tinybundles.finder.ClassDescriptor;
import org.ops4j.pax.tinybundles.finder.ClassFinder;

import aQute.bnd.build.Container;

/**
 * @author Harald Wellmann
 *
 */
public class ClassFinderTest
{
    
    @Test
    public void findAllEmdeddedClasses() throws IOException {
        Class<?> klass = Container.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses( klass );
        for (ClassDescriptor descriptor : descriptors) {
            System.out.println(descriptor.getResourcePath());
            System.out.println(descriptor.getUrl());
        }
    }

    @Test
    public void findAllEmdeddedClassesFromJreClass() throws IOException {
        Class<?> klass = Pattern.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses( klass );
        for (ClassDescriptor descriptor : descriptors) {
            System.out.println(descriptor.getUrl());
        }
    }

    @Test
    public void findAllEmdeddedClassesFromLocalClass() throws IOException {
        Class<?> klass = DemoAnonymousInnerClass.class;
        ClassFinder finder = new ClassFinder();
        Collection<ClassDescriptor> descriptors = finder.findAllEmbeddedClasses( klass );
        for (ClassDescriptor descriptor : descriptors) {
            System.out.println(descriptor.getResourcePath());
            System.out.println(descriptor.getUrl());
        }
    }

        
}
