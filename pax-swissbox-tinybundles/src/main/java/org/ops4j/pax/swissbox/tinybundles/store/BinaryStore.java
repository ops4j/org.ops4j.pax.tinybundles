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
package org.ops4j.pax.swissbox.tinybundles.store;

import java.io.IOException;
import java.net.URI;

/**
 * Entity Store Style Interface to store and retrieve data of type T.
 */
public interface BinaryStore<T>
{

    /**
     * Read incoming object of type T (for example an InputSteam)
     *
     * @param inp object to be stored. Implementations are usually bound to a certain T
     *
     * @return handle that can be shared. Validity of this handle may be different from implementation to implementation.
     *
     * @throws IOException in case incoming object cannot be read (if it involves IO)
     */
    BinaryHandle store( T inp )
        throws IOException;

    /**
     * Load a T after (successfully) stored it before.
     *
     * @param handle identifier that has been returned from a previous srore call.
     *
     * @return instance of type T that has been stored before.
     *
     * @throws IOException if loading resource involves IO, things can always go wrong.
     */
    T load( BinaryHandle handle )
        throws IOException;

    /**
     * Fixed location for a resource that has been stored before.
     * Beware, not all BinaryStore implementations may have such thing.
     *
     * Tinybundles.Store is made for programs, not as an end-user library.
     * If possible, use the BinaryHandle you get from store(..) operations.
     * This also gives implementations of this interface greatest freedom and keeps you apps backend independent.
     *
     * @param handle must refer to a previously stored object.
     *
     * @return location that is equivalent to the given handle. If possible.
     *
     * @throws IOException                   if IO is involved, things may go mad.
     * @throws UnsupportedOperationException implementation may not support this.
     */
    URI getLocation( BinaryHandle handle )
        throws IOException;

}
