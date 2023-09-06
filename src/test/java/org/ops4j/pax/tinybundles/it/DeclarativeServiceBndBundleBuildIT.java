/*
 * Copyright 2023 Oliver Lietz.
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
package org.ops4j.pax.tinybundles.it;

import java.io.InputStream;
import java.io.Serializable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.tinybundles.demo.ds.DsService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.tinybundles.TinyBundles.bndBuilder;
import static org.ops4j.pax.tinybundles.TinyBundles.bundle;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class DeclarativeServiceBndBundleBuildIT extends TinybundlesTestSupport {

    private static final String BUNDLE_SYMBOLICNAME = "org.ops4j.pax.tinybundles.demo.ds";

    @Inject
    @Filter("(name=declarative)")
    private Serializable service;

    // create a bundle with TinyBundles
    private InputStream dsBundle() {
        return bundle()
            .symbolicName(BUNDLE_SYMBOLICNAME)
            .add(DsService.class)
            .build(bndBuilder());
    }

    @Configuration
    public Option[] configuration() {
        return options(
            baseConfiguration(),
            scr(),
            streamBundle(dsBundle()).start() // provision bundle created by TinyBundles
        );
    }

    @Test
    public void testDeclarativeService() {
        assertThat(service, notNullValue());
        assertThat(service.getClass().getName(), is(DsService.class.getName()));
    }

}
