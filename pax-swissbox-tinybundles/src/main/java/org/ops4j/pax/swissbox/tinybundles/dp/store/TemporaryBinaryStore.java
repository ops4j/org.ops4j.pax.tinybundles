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
        // TODO: do SHA1 mappign so we do not store identical artifacts twice.
        final File target = File.createTempFile( "tinybundles", "binary" );
        BinaryHandle handle = new BinaryHandle()
        {

            public String getIdentification()
            {
                return target.getAbsolutePath();
            }
        };

        FileOutputStream fis = null;
        try
        {
            fis = new FileOutputStream( target );
            StreamUtils.copyStream( inp, fis, false );
        } finally
        {
            if( fis != null )
            {
                fis.close();
            }
        }
        return handle;
    }

    public InputStream load( BinaryHandle handle )
        throws IOException
    {
        return new FileInputStream( handle.getIdentification() );
    }
}
