/*
 * Copyright 2013 Harald Wellmann
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

package org.ops4j.pax.tinybundles.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

/**
 * @author Harald Wellmann
 * 
 */
public class UrlTest
{
    @Test
    public void convertUrlWithEncodedWhitespace() throws MalformedURLException, URISyntaxException
    {
        String urlString = "file:/home/hwellmann/My%20Documents/doc.txt";
        URL url = new URL( urlString );
        assertThat( new File( url.toURI() ).toString(), is( "/home/hwellmann/My Documents/doc.txt" ) );
    }
}
