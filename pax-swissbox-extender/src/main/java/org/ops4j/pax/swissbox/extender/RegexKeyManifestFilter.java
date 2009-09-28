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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.ops4j.lang.NullArgumentException;

/**
 * A manifest filter that filters manifest keys based on a regular expression.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, February 09, 2008
 */
public class RegexKeyManifestFilter
    implements ManifestFilter
{

    /**
     * Regular expression pattern.
     */
    private final Pattern m_pattern;

    /**
     * Constructor.
     *
     * @param regexp a regular expression. Cannot be null or empty.
     *
     * @throws NullArgumentException  - If regexp is null or empty
     * @throws PatternSyntaxException - If the expression's syntax is invalid
     */
    public RegexKeyManifestFilter( final String regexp )
    {
        NullArgumentException.validateNotEmpty( regexp, true, "Regular expression" );

        m_pattern = Pattern.compile( regexp );
    }

    public Map<String, String> match( final Map<String, String> entries )
    {
        final Map<String, String> matching = new HashMap<String, String>();
        if( entries != null && !entries.isEmpty() )
        {
            for( Map.Entry<String, String> entry : entries.entrySet() )
            {
                if( m_pattern.matcher( entry.getKey() ).matches() )
                {
                    matching.put( entry.getKey(), entry.getValue() );
                }
            }
        }
        return matching;
    }
}
