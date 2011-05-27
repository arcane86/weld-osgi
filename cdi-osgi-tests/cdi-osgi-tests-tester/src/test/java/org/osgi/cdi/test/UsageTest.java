package org.osgi.cdi.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.test.util.Environment;
import org.osgi.framework.*;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Collection;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class UsageTest {

    @Configuration
    public static Option[] configure() {
        return options(
                Environment.CDIOSGiEnvironment(mavenBundle("com.sample.osgi", "cdi-osgi-tests-bundle1").version("1.0-SNAPSHOT"))
        );
    }

    @Test
    public void launchTest(BundleContext context) throws InterruptedException, BundleException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null;

        for(Bundle b : context.getBundles()) {
            Assert.assertEquals("Bundle" + b.getSymbolicName() + "is not ACTIVE", Bundle.ACTIVE, b.getState());
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle1")) {
                bundle1=b;
            }
        }

        Assert.assertNotNull("The bundle1 was not retrieved",bundle1);

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);
        Collection<CDIContainer> containers = factory.containers();

        Assert.assertEquals("The container collection had the wrong number of containers",1,containers.size());

        CDIContainer container = factory.container(bundle1);
        Assert.assertNotNull("The container for bundle1 was null",container);
        Assert.assertTrue("The container for bundle1 was not started",container.isStarted());

        Collection<ServiceRegistration> registrations = container.getRegistrations();
        Assert.assertEquals("The registration collection had the wrong number of registrations",3,registrations.size());

        Collection<String> beanClasses = container.getBeanClasses();
        Assert.assertNotNull("The bean class collection was null",beanClasses);

        BeanManager beanManager = container.getBeanManager();
        Assert.assertNotNull("The bean manager was null",beanManager);

//        Event event = container.getEvent();
//        Assert.assertNotNull("The event was null",event);

        Instance instance = container.getInstance();
        Assert.assertNotNull("The instance was null",instance);

        Assert.assertTrue("The container was not been shutdown",container.shutdown());
        Assert.assertFalse("The container was still started",container.isStarted());
        Assert.assertEquals("The container collection had the wrong number of containers",1,containers.size());

    }
}
