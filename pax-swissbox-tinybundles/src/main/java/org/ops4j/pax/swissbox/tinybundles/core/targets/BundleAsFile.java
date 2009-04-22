package org.ops4j.pax.swissbox.tinybundles.core.targets;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import org.ops4j.pax.swissbox.tinybundles.core.BundleAs;
import org.ops4j.io.StreamUtils;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 20, 2009
 */
public class BundleAsFile implements BundleAs<File>
{

    private File m_file;

    public BundleAsFile( File f )
    {
        m_file = f;
    }

    public File make( InputStream inp )

    {
        return toDisk( inp, m_file );
    }

    private File toDisk( InputStream inputStream, File file )

    {
        FileOutputStream fout = null;
        try
        {
            fout = new FileOutputStream( file );
        }
        catch( FileNotFoundException e )
        {
            throw new RuntimeException( e );
        }

        try
        {
            StreamUtils.copyStream( inputStream, fout, false );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // inputStream.close();
                fout.close();

            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }
        return file;

    }
}
