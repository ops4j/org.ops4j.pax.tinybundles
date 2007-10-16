/*
 * Copyright 2007 Alin Dreghiciu.
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
package org.ops4j.pax.swissbox.bundle;

import java.net.URL;
import org.ops4j.lang.PreConditionException;

/**
 * Represents a file in a bundle.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0
 */
public class BundleResource
{

    /**
     * Bundle resource name.
     */
    private String m_name;
    /**
     * Bundle resource url.
     */
    private URL m_url;

    /**
     * Creates a new bundle resource.
     *
     * @param name name of the resource. Cannot be null
     * @param url  url of the resource. Cannot be null
     */
    public BundleResource( final String name, final URL url )
    {
        PreConditionException.validateNotNull( name, "Resource name" );
        PreConditionException.validateNotNull( url, "Resource URL" );

        m_name = name;
        m_url = url;
    }

    /**
     * @return resource name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * @return resource url
     */
    public URL getUrl()
    {
        return m_url;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        BundleResource that = (BundleResource) o;

        if( m_url != null ? !m_url.equals( that.m_url ) : that.m_url != null )
        {
            return false;
        }

        return true;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return ( m_url != null ? m_url.hashCode() : 0 );
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder()
            .append( this.getClass().getSimpleName() )
            .append( "{" )
            .append( "name=" ).append( m_name )
            .append( "}" )
            .toString();
    }

}
