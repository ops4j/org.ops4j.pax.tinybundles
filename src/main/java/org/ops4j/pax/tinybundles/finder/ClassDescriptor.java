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

package org.ops4j.pax.tinybundles.finder;

import java.net.URL;

/**
 * @author Harald Wellmann
 * 
 */
public class ClassDescriptor
{
    private String resourcePath;
    private URL url;

    public ClassDescriptor()
    {
    }

    public ClassDescriptor( String resourcePath, URL url )
    {
        this.resourcePath = resourcePath;
        this.url = url;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath()
    {
        return resourcePath;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath( String resourcePath )
    {
        this.resourcePath = resourcePath;
    }

    /**
     * @return the url
     */
    public URL getUrl()
    {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl( URL url )
    {
        this.url = url;
    }

}
