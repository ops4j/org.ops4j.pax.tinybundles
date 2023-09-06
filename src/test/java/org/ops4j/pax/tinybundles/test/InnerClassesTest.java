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

import java.util.Set;

import org.junit.Test;
import org.ops4j.pax.tinybundles.InnerClassStrategy;
import org.ops4j.pax.tinybundles.demo.DemoAnonymousInnerClass;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.ops4j.pax.tinybundles.TinyBundles.bundle;

public class InnerClassesTest {

    @Test
    public void allInnerClassesTest() {
        bundle().add(DemoAnonymousInnerClass.class, InnerClassStrategy.ALL).build(
            (resources, headers) -> {
                assertThat(resources.keySet(), hasItems(
                        "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass.class",
                        "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class",
                        "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class",
                        "org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class"
                    )
                );
                return null;
            }
        );
    }

    @Test
    public void anonymousInnerClassesTest() {
        bundle().add(DemoAnonymousInnerClass.class, InnerClassStrategy.ANONYMOUS).build(
            (resources, headers) -> {
                final Set<String> keys = resources.keySet();
                assertThat(keys, hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass.class"));
                assertThat(keys, hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class"));
                assertThat(keys, not(hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class")));
                assertThat(keys, not(hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class")));
                return null;
            }
        );
    }

    @Test
    public void noInnerClassesTest() {
        bundle().add(DemoAnonymousInnerClass.class, InnerClassStrategy.NONE).build(
            (resources, headers) -> {
                final Set<String> keys = resources.keySet();
                assertThat(keys, hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass.class"));
                assertThat(keys, not(hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$1.class")));
                assertThat(keys, not(hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass.class")));
                assertThat(keys, not(hasItem("org/ops4j/pax/tinybundles/demo/DemoAnonymousInnerClass$SomeInnerClass$1.class")));
                return null;
            }
        );
    }

}
