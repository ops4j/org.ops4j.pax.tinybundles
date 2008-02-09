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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * BundleManifestScanner Unit Tests.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, February 09, 2008
 */
public class BundleManifestScannerTest
{

    /**
     * Tests that a null string matcher is not allowed.
     * Expected to throw NullArgumentException.
     */
    @Test( expected = org.ops4j.lang.NullArgumentException.class )
    public void nullStringMatcher()
    {
        new BundleManifestScanner( null );
    }

    /**
     * Tests that a null bundle is not accepted for scanning.
     * Expected to throw NullArgumentException.
     */
    @Test( expected = org.ops4j.lang.NullArgumentException.class )
    public void scanWithNullBundle()
    {
        new BundleManifestScanner( createMock( ManifestFilter.class ) ).scan( null );
    }

    /**
     * Tests that a bundle that returns a null dictionary headers will be accepted and return an empty list of headers.
     */
    @Test
    public void scanWithNullBundleHeaders()
    {
        final Bundle bundle = createMock( Bundle.class );
        expect( bundle.getHeaders() ).andReturn( null );

        replay( bundle );
        List<?> headers = new BundleManifestScanner( createMock( ManifestFilter.class ) ).scan( bundle );

        assertThat( "Headers list", headers, is( notNullValue() ) );
        assertThat( "Number of headers", 0, is( equalTo( headers.size() ) ) );
        verify( bundle );
    }

    /**
     * Tests that dictionary filter is called with bundle headers.
     */
    @Test
    public void scan()
    {
        final Dictionary<String, String> dictionary = new Hashtable<String, String>();
        dictionary.put( "k1", "v1" );
        dictionary.put( "k2", "v2" );

        final Map<String, String> entries = new HashMap<String, String>();
        entries.put( "k1", "v1" );
        entries.put( "k2", "v2" );

        final Bundle bundle = createMock( Bundle.class );
        expect( bundle.getHeaders() ).andReturn( dictionary );
        final ManifestFilter filter = createMock( ManifestFilter.class );
        expect( filter.match( (Map<String, String>) notNull() ) ).andReturn( entries );

        replay( bundle, filter );
        List<?> headers = new BundleManifestScanner( filter ).scan( bundle );
        verify( bundle, filter );

        assertThat( "Headers list", headers, is( notNullValue() ) );
        assertThat( "Number of headers", 2, is( equalTo( headers.size() ) ) );
    }

}
