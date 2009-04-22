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
package org.ops4j.pax.tinybundles.core;

import java.io.InputStream;
import java.io.File;
import java.net.URL;
import org.ops4j.pax.tinybundles.core.intern.TinyBundleImpl;
import org.ops4j.pax.tinybundles.core.intern.CoreBuildImpl;
import org.ops4j.pax.tinybundles.core.targets.BundleAsURLImpl;
import org.ops4j.pax.tinybundles.core.targets.BundleAsFile;
import org.ops4j.pax.tinybundles.core.metadata.BndBuilder;
import org.ops4j.pax.tinybundles.core.metadata.RawBuilder;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class TinyBundles
{

    public static TinyBundle newBundle()
    {
        return new TinyBundleImpl();
    }

    public static BundleAs<InputStream> asStream()
    {
        return new CoreBuildImpl();
    }

    public static BundleAs<URL> asURL()
    {
        return new BundleAsURLImpl();
    }

    public static BundleAs<File> asFile( File f )
    {
        return new BundleAsFile( f );
    }

    public static BuildableBundle withBnd()
    {
        return new BndBuilder();
    }

    public static BuildableBundle with()
    {
        return new RawBuilder();
    }

}
