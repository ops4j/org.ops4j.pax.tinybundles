package org.ops4j.pax.swissbox.tinybundles.dp.store;

import java.io.IOException;

/**
 *
 */
public interface BinaryStore<T>
{

    BinaryHandle store( T inp )
        throws IOException;

    T load( BinaryHandle handle )
        throws IOException;


}
