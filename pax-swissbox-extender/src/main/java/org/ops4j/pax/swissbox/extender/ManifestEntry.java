/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ops4j.pax.swissbox.extender;

import org.ops4j.lang.NullArgumentException;

/**
 * A pair of String key and value.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, February 09, 2008
 */
public class ManifestEntry
{

    /**
     * Entry key.
     */
    private final String m_key;
    /**
     * Entry value.
     */
    private final String m_value;

    public ManifestEntry( final String key, final String value )
    {
        NullArgumentException.validateNotNull( key, "Manifest entry key" );

        m_key = key;
        m_value = value;
    }

    /**
     * Getter.
     *
     * @return manifest entry key.
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Getter.
     *
     * @return manifest entry value.
     */
    public String getValue()
    {
        return m_value;
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( !( o instanceof ManifestEntry ) )
        {
            return false;
        }

        ManifestEntry that = (ManifestEntry) o;

        if( m_key != null ? !m_key.equals( that.m_key ) : that.m_key != null )
        {
            return false;
        }
        if( m_value != null ? !m_value.equals( that.m_value ) : that.m_value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = ( m_key != null ? m_key.hashCode() : 0 );
        result = 31 * result + ( m_value != null ? m_value.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString()
    {
        return new StringBuilder( ManifestEntry.class.getSimpleName() )
            .append( "{ " )
            .append( "key=" ).append( m_key )
            .append( ", value=" ).append( m_value )
            .append( " }" )
            .toString();
    }

}
