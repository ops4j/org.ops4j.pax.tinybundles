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