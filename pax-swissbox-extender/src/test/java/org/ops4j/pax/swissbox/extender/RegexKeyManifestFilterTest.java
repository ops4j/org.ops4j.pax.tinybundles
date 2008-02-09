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
package org.ops4j.pax.swissbox.extender;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.ops4j.lang.NullArgumentException;

/**
 * RegexKeyManifestFilter Unit Tests.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, February 09, 2008
 */
public class RegexKeyManifestFilterTest
{

    /**
     * Tests that a null regex is not allowed.
     * Expected to throw NullArgumentException.
     */
    @Test( expected = NullArgumentException.class )
    public void nullRegex()
    {
        new RegexKeyManifestFilter( null );
    }

    /**
     * Tests that an empty regex is not allowed.
     * Expected to throw NullArgumentException.
     */
    @Test( expected = NullArgumentException.class )
    public void emptyRegex()
    {
        new RegexKeyManifestFilter( "" );
    }

    /**
     * Tests that a regex containing only spaces is not allowed.
     * Expected to throw NullArgumentException.
     */
    @Test( expected = NullArgumentException.class )
    public void onlySpacesRegex()
    {
        new RegexKeyManifestFilter( "  " );
    }

    /**
     * Tests that a malformed regex is not allowed.
     * Expected to throw PatternSyntaxException.
     */
    @Test( expected = PatternSyntaxException.class )
    public void invalidSyntaxPattern()
    {
        new RegexKeyManifestFilter( "[invalid" );
    }

    /**
     * Tests that matching against a null map will return an empty map.
     */
    @Test
    public void matchAgainstNullMap()
    {
        final Map<String, String> filtered = new RegexKeyManifestFilter( "k1" ).match( null );

        assertThat( "Manifest entries", filtered, is( notNullValue() ) );
        assertThat( "Filtered manifest entries", filtered, is( equalTo( Collections.<String, String>emptyMap() ) ) );
    }

    /**
     * Tests that matching against an empty map will return an empty map.
     */
    @Test
    public void matchAgainstEmptyMap()
    {
        final Map<String, String> filtered =
            new RegexKeyManifestFilter( "k1" ).match( Collections.<String, String>emptyMap() );

        assertThat( "Manifest entries", filtered, is( notNullValue() ) );
        assertThat( "Filtered manifest entries", filtered, is( equalTo( Collections.<String, String>emptyMap() ) ) );
    }

    @Test
    public void match01()
    {
        final Map<String, String> entries = new HashMap<String, String>();
        entries.put( "k1", "v1" );
        entries.put( "k2", "v2" );

        final Map<String, String> expected = new HashMap<String, String>();
        expected.put( "k1", "v1" );

        final Map<String, String> filtered = new RegexKeyManifestFilter( "k1" ).match( entries );

        assertThat( "Manifest entries", filtered, is( notNullValue() ) );
        assertThat( "Filtered manifest entries", filtered, is( equalTo( expected ) ) );
    }

    @Test
    public void match02()
    {
        final Map<String, String> entries = new HashMap<String, String>();
        entries.put( "k1", "v1" );
        entries.put( "k2", "v2" );
        entries.put( "k3", "v3" );

        final Map<String, String> expected = new HashMap<String, String>();
        expected.put( "k1", "v1" );
        expected.put( "k3", "v3" );

        final Map<String, String> filtered = new RegexKeyManifestFilter( "k[13]" ).match( entries );

        assertThat( "Manifest entries", filtered, is( notNullValue() ) );
        assertThat( "Filtered manifest entries", filtered, is( equalTo( expected ) ) );
    }

}