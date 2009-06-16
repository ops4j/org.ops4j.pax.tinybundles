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
package org.ops4j.pax.swissbox.tinybundles.core;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.ops4j.pax.swissbox.tinybundles.core.intern.CoreBuildImpl;
import org.ops4j.pax.swissbox.tinybundles.core.intern.TinyBundleImpl;
import org.ops4j.pax.swissbox.tinybundles.core.metadata.BndBuilder;
import org.ops4j.pax.swissbox.tinybundles.core.metadata.RawBuilder;
import org.ops4j.pax.swissbox.tinybundles.core.targets.BundleAsFile;
import org.ops4j.pax.swissbox.tinybundles.core.targets.BundleAsURLImpl;

/**
 * This is the humane api factory class that is meant to be imported statically with TinyBundles.*
 * Its also the default interaction place with the user. So take care of (non compatible) changes across releases.
 *
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class TinyBundles
{

    /**
     * Start with a fresh bundle with this factory method.
     * You can then chain methodcalls thanks to the humane nature of TinyBundle interface.
     *
     * @return a new instance of a tinybundle. This is almost always the startingpoint of any interaction with tinybundles.
     */
    public static TinyBundle newBundle()
    {
        return new TinyBundleImpl();
    }

    /**
     * Stream
     */
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
