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

import javax.enterprise.event.Event;
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
                Environment.CDIOSGiEnvironment(
                        mavenBundle("com.sample.osgi","cdi-osgi-tests-bundle1").version("1.0-SNAPSHOT"),
                        mavenBundle("com.sample.osgi","cdi-osgi-tests-bundle2").version("1.0-SNAPSHOT")
                )
        );
    }

    @Test
    public void launchTest(BundleContext context) throws InterruptedException, BundleException, InvalidSyntaxException {
        Environment.waitForEnvironment(context);

        Bundle bundle1 = null, bundle2 = null;

        for(Bundle b : context.getBundles()) {
            Assert.assertEquals("Bundle" + b.getSymbolicName() + "is not ACTIVE", Bundle.ACTIVE, b.getState());
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle1")) {
                bundle1=b;
            }
            if(b.getSymbolicName().equals("com.sample.osgi.cdi-osgi-tests-bundle2")) {
                bundle2=b;
            }
        }

        Assert.assertNotNull("The bundle1 was not retrieved",bundle1);
        Assert.assertNotNull("The bundle2 was not retrieved",bundle2);

        ServiceReference factoryReference = context.getServiceReference(CDIContainerFactory.class.getName());
        CDIContainerFactory factory = (CDIContainerFactory) context.getService(factoryReference);
        Collection<CDIContainer> containers = factory.containers();

        Assert.assertEquals("The container collection had the wrong number of containers",2,containers.size());

        CDIContainer container1 = factory.container(bundle1);
        CDIContainer container2 = factory.container(bundle2);
        Assert.assertNotNull("The container for bundle1 was null",container1);
        Assert.assertNotNull("The container for bundle2 was null",container2);
        Assert.assertTrue("The container for bundle1 was not started",container1.isStarted());
        Assert.assertTrue("The container for bundle2 was not started",container2.isStarted());

        Collection<ServiceRegistration> registrations1 = container1.getRegistrations();
        Collection<ServiceRegistration> registrations2 = container2.getRegistrations();
        Assert.assertEquals("The registration collection 1 had the wrong number of registrations",3,registrations1.size());
        Assert.assertEquals("The registration collection 2 had the wrong number of registrations",3,registrations2.size());

        Collection<String> beanClasses1 = container1.getBeanClasses();
        Collection<String> beanClasses2 = container2.getBeanClasses();
        Assert.assertNotNull("The bean class collection 1 was null",beanClasses1);
        Assert.assertNotNull("The bean class collection 2 was null",beanClasses2);

        BeanManager beanManager1 = container1.getBeanManager();
        BeanManager beanManager2 = container2.getBeanManager();
        ServiceReference[] beanManagerServices = context.getServiceReferences(BeanManager.class.getName(),null);
        BeanManager beanManagerService1 = null;
        BeanManager beanManagerService2 = null;
        for(ServiceReference ref : beanManagerServices) {
            if(ref.getBundle() == bundle1) {
                beanManagerService1 = (BeanManager)context.getService(ref);
            }
            if(ref.getBundle() == bundle2) {
                beanManagerService2 = (BeanManager)context.getService(ref);
            }
        }
        Assert.assertEquals("The number of bean manager services was wrong",2,beanManagerServices.length);
        Assert.assertNotNull("The bean manager 1 was null",beanManager1);
        Assert.assertNotNull("The bean manager 2 was null",beanManager2);
        Assert.assertNotNull("The bean manager service 1 was null",beanManagerService1);
        Assert.assertNotNull("The bean manager service 2 was null",beanManagerService2);
        Assert.assertEquals("The bean manager service 1 did not match the bean manager 1",beanManager1,beanManagerService1);
        Assert.assertEquals("The bean manager service 2 did not match the bean manager 2",beanManager2,beanManagerService2);

        Event event1 = container1.getEvent();
        Event event2 = container2.getEvent();
        ServiceReference[] eventServices = context.getServiceReferences(Event.class.getName(),null);
        Event eventService1 = null;
        Event eventService2 = null;
        for(ServiceReference ref : eventServices) {
            if(ref.getBundle() == bundle1) {
                eventService1 = (Event)context.getService(ref);
            }
            if(ref.getBundle() == bundle2) {
                eventService2 = (Event)context.getService(ref);
            }
        }
        Assert.assertEquals("The number of event services was wrong",2,eventServices.length);
        Assert.assertNotNull("The event 1 was null",event1);
        Assert.assertNotNull("The event 2 was null",event2);
        Assert.assertNotNull("The event service 1 was null",eventService1);
        Assert.assertNotNull("The event service 2 was null",eventService2);
//        Assert.assertEquals("The event service 1 did not match the event 1",event1,eventService1);
//        Assert.assertEquals("The event service 2 did not match the event 2",event2,eventService2);

        Instance instance1 = container1.getInstance();
        Instance instance2 = container2.getInstance();
        ServiceReference[] instanceServices = context.getServiceReferences(Instance.class.getName(),null);
        Instance instanceService1 = null;
        Instance instanceService2 = null;
        for(ServiceReference ref : instanceServices) {
            if(ref.getBundle() == bundle1) {
                instanceService1 = (Instance)context.getService(ref);
            }
            if(ref.getBundle() == bundle2) {
                instanceService2 = (Instance)context.getService(ref);
            }
        }
        Assert.assertEquals("The number of instance services was wrong",2,instanceServices.length);
        Assert.assertNotNull("The instance 1 was null",instance1);
        Assert.assertNotNull("The instance 2 was null",instance2);
        Assert.assertNotNull("The instance 1 service was null",instanceService1);
        Assert.assertNotNull("The instance 2 service was null",instanceService2);
        Assert.assertEquals("The instance service 1 did not match the instance 1",instance1,instanceService1);
        Assert.assertEquals("The instance service 2 did not match the instance 2",instance2,instanceService2);

        Assert.assertTrue("The container was not been shutdown",container1.shutdown());
        Assert.assertFalse("The container was still started",container1.isStarted());
        Assert.assertEquals("The container collection had the wrong number of containers",2,containers.size());

    }
}
