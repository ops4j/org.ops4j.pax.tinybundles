package org.ops4j.pax.swissbox.tinybundles.core.metadata;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tonit
 * Date: Sep 7, 2009
 * Time: 10:33:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIDProvider
{

    public static String getUID()
    {
        File f = null;
        try
        {
            f = File.createTempFile( "tinybundles", "UID" );
        } catch( IOException e )
        {
            throw new RuntimeException( "No UID" );
        }
        f.delete();
        return f.getName();
    }
}
