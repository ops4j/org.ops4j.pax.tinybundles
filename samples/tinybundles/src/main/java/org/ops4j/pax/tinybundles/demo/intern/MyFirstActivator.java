package org.ops4j.pax.tinybundles.demo.intern;

import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.ops4j.pax.tinybundles.demo.HelloWorld;

/**
 * @author Toni Menzel (tonit)
 * @since Apr 9, 2009
 */
public class MyFirstActivator implements BundleActivator
{

    private ServiceRegistration ref;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        Dictionary dict = new Properties();

        ref = bundleContext.registerService( HelloWorld.class.getName(), new HelloWorldImpl(), dict );
        System.out.println( "waiting for 10seks.." );
        Thread.sleep( 10000 );
        System.out.println( "DONE" );

    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        ref.unregister();
    }
}
