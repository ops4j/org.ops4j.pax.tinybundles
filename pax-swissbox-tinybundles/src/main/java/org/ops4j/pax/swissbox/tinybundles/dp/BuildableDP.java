package org.ops4j.pax.swissbox.tinybundles.dp;

import java.io.InputStream;
import java.io.IOException;

/**
 * @author Toni Menzel (tonit)
 * @since Jun 24, 2009
 */
public interface BuildableDP
{
/**
     * Will kick off the build of a deployment package.
     *
     * @return the Deployment Package (JarInputStream)
     *
     * @throws java.io.IOException If something goes wrong while building.
     */
    InputStream build()
        throws IOException;
}
