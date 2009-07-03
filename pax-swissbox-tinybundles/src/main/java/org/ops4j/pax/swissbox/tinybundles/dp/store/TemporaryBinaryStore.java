/*
 * Copyright 2009 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.swissbox.tinybundles.dp.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.ops4j.io.StreamUtils;

/**
 * Entity store like implementation.
 * Stores incoming data (store) to disk at a temporaty location.
 * The handle is valid for use (load) only for this instance's lifetime. (tmp storage location)
 */
public class TemporaryBinaryStore implements BinaryStore<InputStream>
{

    private File m_dir;

    public TemporaryBinaryStore()
    {
        m_dir = new File( System.getProperty( "java.io.tmpdir" ) + "/tb" );
        if( m_dir.exists() )
        {
            //FileUtils.delete( m_dir );
        }
        m_dir.mkdirs();
    }

    public BinaryHandle store( InputStream inp )
        throws IOException
    {
        final File intermediate = File.createTempFile( "tinybundles_", ".tmp" );

        FileOutputStream fis = null;
        final String h;

        fis = new FileOutputStream( intermediate );
        h = hash( inp, fis );
        fis.close();
        StreamUtils.copyStream( new FileInputStream( intermediate ), new FileOutputStream( getLocation( h ) ), true );
        intermediate.delete();

        BinaryHandle handle = new BinaryHandle()
        {

            public String getIdentification()
            {
                return h;
            }
        };
        return handle;
    }

    private File getLocation( String id )
    {
        return new File( m_dir, "tinyundles_" + id + ".bin" );
    }

    public InputStream load( BinaryHandle handle )
        throws IOException
    {
        return new FileInputStream( getLocation( handle.getIdentification() ) );
    }

    public String hash( final InputStream in, OutputStream storeHere )
        throws IOException
    {
        String result;
        MessageDigest digest;

        try
        {
            digest = MessageDigest.getInstance( "SHA1" );
        } catch( NoSuchAlgorithmException failed )
        {
            failed.printStackTrace( System.err );
            RuntimeException failure = new IllegalStateException( "Could not get SHA-1 Message" );

            failure.initCause( failed );
            throw failure;
        }

        try
        {
            byte[] buffer = new byte[1024];
            int le = 0;
            while( ( le = in.read( buffer ) ) >= 0 )
            {
                digest.update( buffer.toString().getBytes( "UTF-8" ) );
                storeHere.write( buffer, 0, le );
            }

            result = Base64.encode( digest.digest().toString() );
        } catch( UnsupportedEncodingException impossible )
        {
            RuntimeException failure = new IllegalStateException( "Could not encode expression as UTF8" );

            failure.initCause( impossible );
            throw failure;
        }
        return result;
    }

}
