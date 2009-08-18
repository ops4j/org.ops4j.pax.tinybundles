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
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.io.StreamUtils;
import org.ops4j.io.FileUtils;

/**
 * Entity store like implementation.
 * Stores incoming data (store) to disk at a temporary location.
 * The handle is valid for use (load) only for this instance's lifetime. (tmp storage location)
 *
 * Uses an SHA-1 hash for indexing.
 */
public class TemporaryBinaryStore implements BinaryStore<InputStream>
{

    private static Log LOG = LogFactory.getLog( TemporaryBinaryStore.class );
    private File m_dir;

    public TemporaryBinaryStore()
    {
        this( false );
    }

    public TemporaryBinaryStore( boolean flushStoreage )
    {
        m_dir = new File( System.getProperty( "java.io.tmpdir" ) + "/tb" );
        if( m_dir.exists() && flushStoreage )
        {
            FileUtils.delete( m_dir );
        }
        m_dir.mkdirs();
        LOG.debug( "Storage Area is " + m_dir.getAbsolutePath() );
    }

    public BinaryHandle store( InputStream inp )
        throws IOException
    {
        LOG.debug( "Enter store()" );
        final File intermediate = File.createTempFile( "tinybundles_", ".tmp" );

        FileOutputStream fis = null;
        final String h;

        fis = new FileOutputStream( intermediate );
        h = hash( inp, fis );

        fis.close();
        if( !getLocation( h ).exists() )
        {
            StreamUtils.copyStream( new FileInputStream( intermediate ), new FileOutputStream( getLocation( h ) ), true );
        }
        else
        {
            LOG.info( "Object for " + h + " already exists in store." );
        }
        intermediate.delete();

        BinaryHandle handle = new BinaryHandle()
        {

            public String getIdentification()
            {
                return h;
            }
        };
        LOG.debug( "Exit store(): " + h );
        return handle;
    }

    private File getLocation( String id )
    {
        return new File( m_dir, "tinybundles_" + id + ".bin" );
    }

    public InputStream load( BinaryHandle handle )
        throws IOException
    {
        return new FileInputStream( getLocation( handle.getIdentification() ) );
    }

    public String hash( final InputStream is, OutputStream storeHere )
        throws IOException
    {

        byte[] sha1hash;

        try
        {
            MessageDigest md;
            md = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = new byte[1024];
            int numRead = 0;
            while( ( numRead = is.read( bytes ) ) >= 0 )

            {
                md.update( bytes, 0, numRead );
                storeHere.write( bytes, 0, numRead );
            }
            sha1hash = md.digest();
        } catch( NoSuchAlgorithmException e )
        {
            throw new RuntimeException( e );
        } catch( FileNotFoundException e )
        {
            throw new RuntimeException( e );
        } catch( IOException e )
        {
            throw new RuntimeException( e );
        }
        return convertToHex( sha1hash );
    }

    private static String convertToHex( byte[] data )
    {
        StringBuffer buf = new StringBuffer();
        for( int i = 0; i < data.length; i++ )
        {
            int halfbyte = ( data[ i ] >>> 4 ) & 0x0F;
            int two_halfs = 0;
            do
            {
                if( ( 0 <= halfbyte ) && ( halfbyte <= 9 ) )
                {
                    buf.append( (char) ( '0' + halfbyte ) );
                }
                else
                {
                    buf.append( (char) ( 'a' + ( halfbyte - 10 ) ) );
                }
                halfbyte = data[ i ] & 0x0F;
            } while( two_halfs++ < 1 );
        }
        return buf.toString();
    }

}
