/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * Copyright 2005 Niclas Hedhman
 * Copyright 2008 Alin Dreghiciu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* NOTE!!!!  This is NOT the original Jakarta Commons Logging, but an adaption
 of its interface so that this Log4J OSGi bundle can export the JCL interface
 but redirect to the Log4J implementation. There is nothing here that is useful
 outside an OSGi environment.
 */

package org.apache.commons.logging;

/**
 * This is an adaptation of the Jakarta Commons Logging API that returns a logger that does nothing.
 *
 * @author Alin Dreghiciu
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author Richard A. Sitze
 */

public class LogFactory
{

    /**
     * Log factory in use.
     */
    private static LogFactory m_logFactory;

    /**
     * Logger in use.
     */
    private Log m_log;

    /**
     * @return the LogFactory instance to use.
     *
     * @throws LogConfigurationException if the implementation class is not available or cannot be
     *                                   instantiated.
     */
    public static LogFactory getFactory()
        throws LogConfigurationException
    {
        synchronized( LogFactory.class )
        {
            if( m_logFactory == null )
            {
                m_logFactory = new LogFactory();
            }
        }
        return m_logFactory;
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param clazz Class from which a log name will be derived
     *
     * @return the Log instance to use for the class
     *
     * @throws LogConfigurationException if a suitable <code>Log</code> instance cannot be returned
     */
    public static Log getLog( Class clazz )
        throws LogConfigurationException
    {
        return getFactory().getInstance( clazz.getName() );
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param name Logical name of the <code>Log</code> instance to be returned
     *             (the meaning of this name is only known to the underlying
     *             logging implementation that is being wrapped)
     *
     * @return the Log instance to use for the class of the given name
     *
     * @throws LogConfigurationException if a suitable <code>Log</code> instance cannot be returned
     */
    public static Log getLog( String name )
        throws LogConfigurationException
    {
        return getFactory().getInstance( name );
    }

    /**
     * Release any internal references to previously created {@link LogFactory}
     * instances that have been associated with the specified class loader (if
     * any), after calling the instance method <code>release()</code> on each
     * of them.
     *
     * @param classLoader ClassLoader for which to release the LogFactory
     */
    public static void release( ClassLoader classLoader )
    {
    }

    /**
     * Release any internal references to previously created {@link LogFactory}
     * instances, after calling the instance method <code>release()</code> on
     * each of them. This is useful in environments like servlet containers,
     * which implement application reloading by throwing away a ClassLoader.
     * Dangling references to objects in that class loader would prevent garbage
     * collection.
     */
    public static void releaseAll()
    {
        release();
    }

    private LogFactory()
    {
    }

    /**
     * Return the configuration attribute with the specified name (if any), or
     * <code>null</code> if there is no such attribute.
     *
     * @param name Name of the attribute to return
     *
     * @return always return null. This method is not supported in Pax Logging.
     */
    public Object getAttribute( String name )
    {
        return null;
    }

    /**
     * Return an array containing the names of all currently defined
     * configuration attributes. If there are no such attributes, a zero length
     * array is returned.
     *
     * @return always returns an emtpy String array. This method is not supported in Pax Logging.
     */
    public String[] getAttributeNames()
    {
        return new String[0];
    }

    /**
     * Convenience method to derive a name from the specified class and call
     * <code>getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     *
     * @return the Log instance to use for the given class.
     *
     * @throws LogConfigurationException if a suitable <code>Log</code> instance cannot be returned
     */
    public Log getInstance( Class clazz )
        throws LogConfigurationException
    {
        return getInstance( clazz.getName() );
    }

    /**
     * <p>
     * Construct (if necessary) and return a <code>Log</code> instance, using
     * the factory's current set of configuration attributes.
     * </p>
     *
     * <p>
     * <strong>NOTE</strong> - Depending upon the implementation of the
     * <code>LogFactory</code> you are using, the <code>Log</code> instance
     * you are returned may or may not be local to the current application, and
     * may or may not be returned again on a subsequent call with the same name
     * argument.
     * </p>
     *
     * @param name Logical name of the <code>Log</code> instance to be returned
     *             (the meaning of this name is only known to the underlying
     *             logging implementation that is being wrapped)
     *
     * @return the Log instance of the class with the given name.
     *
     * @throws LogConfigurationException if a suitable <code>Log</code> instance cannot be returned
     */
    public Log getInstance( String name )
        throws LogConfigurationException
    {
        if( m_log == null )
        {
            m_log = new NullLog();
        }
        return m_log;
    }

    /**
     * Release any internal references to previously created {@link Log}
     * instances returned by this factory. This is useful in environments like
     * servlet containers, which implement application reloading by throwing
     * away a ClassLoader. Dangling references to objects in that class loader
     * would prevent garbage collection.
     */
    static public void release()
    {
        m_logFactory = null;
    }

    /**
     * Remove any configuration attribute associated with the specified name. If
     * there is no such attribute, no action is taken.
     *
     * @param name Name of the attribute to remove
     */
    public void removeAttribute( String name )
    {
    }

    /**
     * Set the configuration attribute with the specified name. Calling this
     * with a <code>null</code> value is equivalent to calling
     * <code>removeAttribute(name)</code>.
     *
     * @param name  Name of the attribute to set
     * @param value Value of the attribute to set, or <code>null</code> to
     *              remove any setting for this attribute
     */
    public void setAttribute( String name, Object value )
    {
    }

    /**
     * JCL Log implementation that does nothing.
     */
    private static class NullLog
        implements Log
    {

        public boolean isDebugEnabled()
        {
            return false;
        }

        public boolean isErrorEnabled()
        {
            return false;
        }

        public boolean isFatalEnabled()
        {
            return false;
        }

        public boolean isInfoEnabled()
        {
            return false;
        }

        public boolean isTraceEnabled()
        {
            return false;
        }

        public boolean isWarnEnabled()
        {
            return false;
        }

        public void trace( Object message )
        {
            // does nothing
        }

        public void trace( Object message, Throwable t )
        {
            // does nothing
        }

        public void debug( Object message )
        {
            // does nothing
        }

        public void debug( Object message, Throwable t )
        {
            // does nothing
        }

        public void info( Object message )
        {
            // does nothing
        }

        public void info( Object message, Throwable t )
        {
            // does nothing
        }

        public void warn( Object message )
        {
            // does nothing
        }

        public void warn( Object message, Throwable t )
        {
            // does nothing
        }

        public void error( Object message )
        {
            // does nothing
        }

        public void error( Object message, Throwable t )
        {
            // does nothing
        }

        public void fatal( Object message )
        {
            // does nothing
        }

        public void fatal( Object message, Throwable t )
        {
            // does nothing
        }

        public int getLogLevel()
        {
            return Integer.MAX_VALUE;
        }

    }
}
