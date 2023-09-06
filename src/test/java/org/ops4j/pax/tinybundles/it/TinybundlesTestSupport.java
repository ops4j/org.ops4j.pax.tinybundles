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

import java.io.File;
import java.util.UUID;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.util.PathUtils;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.keepCaches;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.workingDirectory;

public abstract class TinybundlesTestSupport {

    protected final String workingDirectory = String.format("%s/target/paxexam/%s/%s", PathUtils.getBaseDir(), getClass().getSimpleName(), UUID.randomUUID());

    protected static UrlProvisionOption testBundle(final String systemProperty) {
        final String pathname = System.getProperty(systemProperty);
        final File file = new File(pathname);
        return bundle(file.toURI().toString());
    }

    protected static SystemPropertyOption failOnUnresolvedBundles() {
        return systemProperty("pax.exam.osgi.unresolved.fail").value("true");
    }

    protected static Option scr() {
        return composite(
            mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin").versionAsInProject(),
            mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.metatype").versionAsInProject(),
            mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.scr").versionAsInProject(),
            mavenBundle().groupId("org.osgi").artifactId("org.osgi.service.component").versionAsInProject(),
            mavenBundle().groupId("org.osgi").artifactId("org.osgi.util.function").versionAsInProject(),
            mavenBundle().groupId("org.osgi").artifactId("org.osgi.util.promise").versionAsInProject()
        );
    }

    protected Option baseConfiguration() {
        return composite(
            testBundle("bundle.filename"), // TinyBundles itself
            // dependencies
            // org.ops4j.store – pulled in by Pax Exam (org.ops4j.base bundle)
            // bnd
            mavenBundle().groupId("biz.aQute.bnd").artifactId("biz.aQute.bndlib").versionAsInProject(),
            mavenBundle().groupId("biz.aQute.bnd").artifactId("biz.aQute.bnd.util").versionAsInProject(),
            mavenBundle().groupId("org.osgi").artifactId("org.osgi.service.repository").versionAsInProject(),
            mavenBundle().groupId("org.osgi").artifactId("org.osgi.util.function").versionAsInProject(),
            mavenBundle().groupId("org.osgi").artifactId("org.osgi.util.promise").versionAsInProject(),
            // testing
            junitBundles(),
            //
            failOnUnresolvedBundles(),
            keepCaches(),
            workingDirectory(workingDirectory)
        );
    }

}
