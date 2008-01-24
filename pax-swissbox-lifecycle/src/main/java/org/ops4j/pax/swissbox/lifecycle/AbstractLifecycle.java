/*
 * Copyright 2007 Alin Dreghiciu.
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
package org.ops4j.pax.swissbox.lifecycle;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractLifecycle
    implements Lifecycle
{

    private static final State STARTED = new Started();
    private static final State STOPPED = new Stopped();

    /**
     * Concurrency lock.
     */
    private final Lock m_lock;
    private State m_state;

    protected AbstractLifecycle()
    {
        m_lock = new ReentrantLock();
        m_state = STOPPED;
    }

    public void start()
    {
        try
        {
            m_lock.lock();
            if( STARTED != m_state )
            {
                m_state.start( this );
                m_state = STARTED;
            }
        }
        finally
        {
            m_lock.unlock();
        }
    }

    public void stop()
    {
        try
        {
            m_lock.lock();
            if( STOPPED != m_state )
            {
                m_state.stop( this );
                m_state = STOPPED;
            }
        }
        finally
        {
            m_lock.unlock();
        }
    }

    public boolean isStarted()
    {
        return m_state == STARTED;
    }

    protected abstract void onStart();

    protected abstract void onStop();

    private static class State
    {

        public void start( final AbstractLifecycle context )
        {
            //does nothing
        }

        public void stop( final AbstractLifecycle context )
        {
            //does nothing
        }

    }

    private static class Started
        extends State
    {

        @Override
        public void stop( final AbstractLifecycle context )
        {
            context.onStop();
        }



    }

    private static class Stopped
        extends State
    {

        @Override
        public void start( final AbstractLifecycle context )
        {
            context.onStart();
        }

    }


}
