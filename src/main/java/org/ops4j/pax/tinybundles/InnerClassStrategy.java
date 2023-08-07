/*
 * Copyright 2011 Rafael Liu.
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
package org.ops4j.pax.tinybundles;

/**
 * The strategy used when a class added has inner classes.
 * NONE means no other class will be added. ALL means all
 * inner classes are added recursively. ANONYMOUS mean
 * only anonymous classes will be added.
 *
 * @author <a href="mailto:rafaelliu@gmail.com">Rafael Liu</a>
 */
public enum InnerClassStrategy {

    NONE, ALL, ANONYMOUS

}
