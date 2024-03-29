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
package org.ops4j.pax.tinybundles.internal;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 26, 2009
 */
public class Info {

    private static String version = "";

    static {
        try {
            final InputStream inputStream = Info.class.getClassLoader().getResourceAsStream("META-INF/pax-tinybundlesversion.properties");
            if (!Objects.isNull(inputStream)) {
                final Properties properties = new Properties();
                properties.load(inputStream);
                version = properties.getProperty("pax.tinybundles.version", "").trim();
            }
        } catch (Exception ignore) {
            // use default versions
        }
    }

    private Info() { //
    }

    public static String getPaxTinybundlesVersion() {
        return version;
    }

}
