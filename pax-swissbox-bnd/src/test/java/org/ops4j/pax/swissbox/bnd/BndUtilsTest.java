/*
 * Copyright 2009 Toni Menzel.
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
package org.ops4j.pax.swissbox.bnd;

import java.net.MalformedURLException;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Toni Menzel (tonit)
 * @since Jan 13, 2009
 */
public class BndUtilsTest
{

    @Test
    public void emptyInstructionTest()
        throws MalformedURLException

    {
        Properties p = BndUtils.parseInstructions( "" );
        assertEquals( 0, p.size() );

    }

    @Test
    public void oneInstructionTest()
        throws MalformedURLException

    {
        Properties p = BndUtils.parseInstructions( "foo=bar" );
        assertEquals( "bar", p.getProperty( "foo" ) );
    }

    @Test
    public void multipleSimpleInstructionTest()
        throws MalformedURLException

    {
        Properties p = BndUtils.parseInstructions( "foo=bar&sing=sang&cheese=bacon" );
        assertEquals( "bar", p.getProperty( "foo" ) );
        assertEquals( "sang", p.getProperty( "sing" ) );
        assertEquals( "bacon", p.getProperty( "cheese" ) );

    }

    @Test
    public void complexOneInstructionTest()
        throws MalformedURLException

    {
        Properties p = BndUtils.parseInstructions( "Export-Package=*;version=\"2.4.0\"" );
        assertEquals( "*;version=\"2.4.0\"", p.getProperty( "Export-Package" ) );
    }

    @Test
    public void complexManyInstructionTest()
        throws MalformedURLException

    {
        Properties p = BndUtils.parseInstructions( "Export-Package=*;version=\"2.4.0\"&sec=two" );
        assertEquals( "*;version=\"2.4.0\"", p.getProperty( "Export-Package" ) );
        assertEquals( "two", p.getProperty( "sec" ) );
    }

    @Test
    public void versionRangeImportInstructionTest()
    	throws MalformedURLException
	{
		Properties p = BndUtils.parseInstructions( "Import-Package=javax.servlet.*;version=\"[2.3.0,3)\"" );
		assertEquals( "javax.servlet.*;version=\"[2.3.0,3)\"", p.getProperty("Import-Package"));
	}
    
    @Test
    public void whiteSpaceInstructionTest()
    	throws MalformedURLException
    {
    	Properties p = BndUtils.parseInstructions( "Export-Package=*; version=\"2.4.0\"");
    	assertEquals( "*; version=\"2.4.0\"", p.getProperty( "Export-Package" ) );
    }
    
}
