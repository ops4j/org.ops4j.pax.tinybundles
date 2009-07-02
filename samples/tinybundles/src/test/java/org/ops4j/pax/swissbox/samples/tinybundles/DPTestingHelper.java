package org.ops4j.pax.swissbox.samples.tinybundles;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import static org.junit.Assert.*;
import org.osgi.framework.Constants;
import org.ops4j.io.StreamUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tonit
 * Date: Jul 2, 2009
 * Time: 7:52:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class DPTestingHelper
{
      public static InputStream flush( InputStream inp )
        throws IOException
    {
        File f = File.createTempFile( "dest", ".dp" );
        StreamUtils.copyStream( inp, new FileOutputStream( f ), true );
        System.out.println( "--> Flushed to " + f.getAbsolutePath() );
        return new FileInputStream( f );
    }

    public static void verifyDP( InputStream inputStream, String... expectedEntries )
        throws IOException
    {
        // verify manifest entries:
        JarInputStream jout = new JarInputStream( inputStream );
        Manifest man = jout.getManifest();
        assertEquals( "application/vnd.osgi.dp", man.getMainAttributes().getValue( "Content-Type" ) );
        Map<String, Attributes> attributesMap = man.getEntries();
        Set<String> contentHeaders = new HashSet<String>();
        Collections.addAll( contentHeaders, expectedEntries );

        for( String key : attributesMap.keySet() )
        {
            if( !contentHeaders.remove( key ) )
            {
                fail( "Unexpected section in manifest: " + key );
            }
        }

        if( !contentHeaders.isEmpty() )
        {
            for( String s : contentHeaders )
            {
                System.err.println( "Missing Header in manifest: " + s );
                fail( "Missing Header in manifest!" );
            }
        }

        // verify content

        // assume the following content:
        Set<String> content = new HashSet<String>();
        Collections.addAll( content, expectedEntries );
        JarEntry entry = null;

        while( ( entry = jout.getNextJarEntry() ) != null )
        {
            if( !content.remove( entry.getName() ) )
            {
                fail( "Unexpected content in final output: " + entry.getName() );
            }
        }
        jout.close();
        if( !content.isEmpty() )
        {
            for( String s : content )
            {
                System.err.println( "Missing: " + s );
                fail( "Missing content in final output!" );
            }
        }
    }

    public static void verifyBundleContents( InputStream inputStream, String... expectedEntries )
        throws IOException
    {
        // verify manifest entries:
        JarInputStream jout = new JarInputStream( inputStream );
        Manifest man = jout.getManifest();
        Map<String, Attributes> attributesMap = man.getEntries();
        Set<String> contentHeaders = new HashSet<String>();
        Collections.addAll( contentHeaders, expectedEntries );

        for( String key : attributesMap.keySet() )
        {
            if( contentHeaders.contains( key ) )
            {
                Attributes att = attributesMap.get( key );
                assertNotNull( "Bundle " + key + " does not have a Bundle-SymbolicName in DP Manifest",
                               att.getValue( Constants.BUNDLE_SYMBOLICNAME )
                );
            }
        }
    }
}
