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
package org.ops4j.pax.tinybundles.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarHelper {

    private JarHelper() { //
    }

    public static void createEmptyJar(final File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            final JarOutputStream jar = new JarOutputStream(out);
            jar.close();
        }
    }

    public static Manifest getManifest(final InputStream bundle) throws IOException {
        try (JarInputStream jar = new JarInputStream(bundle)) {
            return jar.getManifest();
        }
    }

}
