package org.ops4j.pax.swissbox.tinybundles.dp;

import java.io.InputStream;
import java.io.IOException;

/**
 * @author Toni Menzel (tonit)
 * @since Jun 24, 2009
 */
public interface ResourceTinyDP
{

    TinyDP addResource( String name, InputStream inp )
        throws IOException;

    TinyDP addResource( String name, InputStream inputStream, String resourceProcessorPID )
        throws IOException;

    TinyDP addResource( String name, String url )
        throws IOException;

    /**
     * @param name                 identifier of name section
     * @param url                  to be used to get the content
     * @param resourceProcessorPID a resource pid.
     *
     * @return this
     *
     * @throws java.io.IOException dew
     */
    TinyDP addResource( String name, String url, String resourceProcessorPID )
        throws IOException;

}
