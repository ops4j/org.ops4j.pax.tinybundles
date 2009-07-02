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
package org.ops4j.pax.swissbox.tinybundles.dp.intern;

import java.util.Map;
import java.util.HashMap;
import org.ops4j.pax.swissbox.tinybundles.dp.store.BinaryHandle;

/**
 * Lightweight container that basically just "records" user's calls (addBundle, removeBundle .. etc).
 * From this, a builder can build the final dp.
 * 
 */
public class Bucket
{
    private Map<String, TypedBinaryHandle> m_store = new HashMap<String, TypedBinaryHandle>();

    public String[] getEntries()
    {
        return m_store.keySet().toArray( new String[m_store.size()] );
    }

    public void remove( String entry )
    {
        m_store.remove( entry );
    }

    public void store( String entry, BinaryHandle binaryHandle, DPContentTypes type )
    {
        m_store.put( entry, new TypedBinaryHandle( binaryHandle, type ) );
    }

    public BinaryHandle getHandle( String entry )
    {
        return m_store.get( entry );
    }

    public boolean isType( String name, DPContentTypes bundle )
    {
        return m_store.get( name ).getType() == bundle;
    }

    private class TypedBinaryHandle implements BinaryHandle
    {

        private BinaryHandle m_handle;
        private DPContentTypes m_type;

        public TypedBinaryHandle( BinaryHandle handle, DPContentTypes type )
        {
            m_handle = handle;
            m_type = type;
        }

        public String getIdentification()
        {
            return m_handle.getIdentification();
        }

        public DPContentTypes getType()
        {
            return m_type;
        }
    }

}