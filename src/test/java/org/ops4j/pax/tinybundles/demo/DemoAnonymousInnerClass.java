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
package org.ops4j.pax.tinybundles.demo;

/**
 * @author <a href="mailto:rafaelliu@gmail.com">Rafael Liu</a>
 */
public class DemoAnonymousInnerClass {

    Object object = new Object() {
    };

    SomeInnerClass innerClass = new SomeInnerClass();

    public DemoAnonymousInnerClass() { //
    }

    public static class SomeInnerClass {

        Object object = new Object() {
        };

        public static class NestedInnerClass {
            int a;
        }

    }

    public Object foo() {

        class Local {
        }

        return new Local();
    }

}
