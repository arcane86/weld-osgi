/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.cdi.impl.integration;

import org.osgi.cdi.api.extension.events.AbstractBundleEvent;
import org.osgi.cdi.api.extension.events.AbstractServiceEvent;
import org.osgi.cdi.api.extension.events.BundleEvents;
import org.osgi.cdi.api.extension.events.ServiceEvents;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.api.integration.EmbeddedCDIContainer;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.ExtensionActivator;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmbeddedContainer {

    private final CDIContainer container;

    private final BundleContext context;

    private final EmbeddedListener listener;

    public EmbeddedContainer(BundleContext context) {
        this.context = context;
        try {
            ServiceReference ref = context.getServiceReference(CDIContainerFactory.class.getName());
            CDIContainerFactory factory = (CDIContainerFactory) context.getService(ref);
            container = factory.createContainer(context.getBundle());
            listener = new EmbeddedListener(context, container);
        } catch (Exception e) {
            throw new IllegalStateException("You can't start an embedded container without having a container factory registered", e);
        }
    }

    public EmbeddedCDIContainer initialize() {
        container.initialize();
        context.addBundleListener(listener);
        context.addServiceListener(listener);
        return container;
    }

    public void shutdown() {
        container.shutdown();
        context.removeBundleListener(listener);
        context.removeServiceListener(listener);
    }

    public static class EmbeddedListener implements BundleListener, ServiceListener {

        private static Logger logger = LoggerFactory.getLogger(EmbeddedListener.class);

        private final BundleContext context;

        private final CDIContainer container;

        public EmbeddedListener(BundleContext context, CDIContainer container) {
            this.context = context;
            this.container = container;
        }

        @Override
        public void bundleChanged(BundleEvent event) {
            ServiceReference[] references = findReferences(context, Event.class);

            if (references != null) { //if there are some listening bean bundles
                Bundle bundle = event.getBundle();
                AbstractBundleEvent bundleEvent = null;
                switch (event.getType()) {
                    case BundleEvent.INSTALLED:
                        logger.debug("Receiving a new OSGi bundle event INSTALLED");
                        bundleEvent = new BundleEvents.BundleInstalled(bundle);
                        break;
                    case BundleEvent.LAZY_ACTIVATION:
                        logger.debug("Receiving a new OSGi bundle event LAZY_ACTIVATION");
                        bundleEvent = new BundleEvents.BundleLazyActivation(bundle);
                        break;
                    case BundleEvent.RESOLVED:
                        logger.debug("Receiving a new OSGi bundle event RESOLVED");
                        bundleEvent = new BundleEvents.BundleResolved(bundle);
                        break;
                    case BundleEvent.STARTED:
                        logger.debug("Receiving a new OSGi bundle event STARTED");
                        bundleEvent = new BundleEvents.BundleStarted(bundle);
                        break;
                    case BundleEvent.STARTING:
                        logger.debug("Receiving a new OSGi bundle event STARTING");
                        bundleEvent = new BundleEvents.BundleStarting(bundle);
                        break;
                    case BundleEvent.STOPPED:
                        logger.debug("Receiving a new OSGi bundle event STOPPED");
                        bundleEvent = new BundleEvents.BundleStopped(bundle);
                        break;
                    case BundleEvent.STOPPING:
                        logger.debug("Receiving a new OSGi bundle event STOPPING");
                        bundleEvent = new BundleEvents.BundleStopping(bundle);
                        break;
                    case BundleEvent.UNINSTALLED:
                        logger.debug("Receiving a new OSGi bundle event UNINSTALLED");
                        bundleEvent = new BundleEvents.BundleUninstalled(bundle);
                        break;
                    case BundleEvent.UNRESOLVED:
                        logger.debug("Receiving a new OSGi bundle event UNRESOLVED");
                        bundleEvent = new BundleEvents.BundleUnresolved(bundle);
                        break;
                    case BundleEvent.UPDATED:
                        logger.debug("Receiving a new OSGi bundle event UPDATED");
                        bundleEvent = new BundleEvents.BundleUpdated(bundle);
                        break;
                }
                boolean set = CDIOSGiExtension.currentBundle.get() != null;
                CDIOSGiExtension.currentBundle.set(context.getBundle().getBundleId());
                try {
                    //broadcast the OSGi event through CDI event system
                    container.getEvent().select(BundleEvent.class).fire(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                if (bundleEvent != null) {
                    //broadcast the corresponding CDI-OSGi event
                    fireAllEvent(bundleEvent, container.getEvent());
                }
                if (!set) {
                    CDIOSGiExtension.currentBundle.remove();
                }
            }
        }

        @Override
        public void serviceChanged(ServiceEvent event) {
            ServiceReference[] references = findReferences(context, Instance.class);

            if (references != null) { //if there are some listening bean bundles
                ServiceReference ref = event.getServiceReference();
                AbstractServiceEvent serviceEvent = null;
                switch (event.getType()) {
                    case ServiceEvent.MODIFIED:
                        logger.debug("Receiving a new OSGi service event MODIFIED");
                        serviceEvent = new ServiceEvents.ServiceChanged(ref, context);
                        break;
                    case ServiceEvent.REGISTERED:
                        logger.debug("Receiving a new OSGi service event REGISTERED");
                        serviceEvent = new ServiceEvents.ServiceArrival(ref, context);
                        break;
                    case ServiceEvent.UNREGISTERING:
                        logger.debug("Receiving a new OSGi service event UNREGISTERING");
                        serviceEvent = new ServiceEvents.ServiceDeparture(ref, context);
                        break;
                }
                boolean set = CDIOSGiExtension.currentBundle.get() != null;
                CDIOSGiExtension.currentBundle.set(context.getBundle().getBundleId());
                try {
                    //broadcast the OSGi event through CDI event system
                    container.getEvent().select(ServiceEvent.class).fire(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                if (serviceEvent != null) {
                    //broadcast the corresponding CDI-OSGi event
                    fireAllEvent(serviceEvent, container.getEvent(), container.getInstance());
                }
                if (!set) {
                    CDIOSGiExtension.currentBundle.remove();
                }
            }
        }

        private ServiceReference[] findReferences(BundleContext context, Class<?> type) {
            ServiceReference[] references = null;
            try {
                references = context.getServiceReferences(type.getName(), null);
            } catch (InvalidSyntaxException e) {// Ignored
            }
            return references;
        }

        private void fireAllEvent(AbstractServiceEvent event, Event broadcaster, Instance<Object> instance) {
            List<Class<?>> classes = event.getServiceClasses(getClass());
            Class eventClass = event.getClass();
            for (Class<?> clazz : classes) {
                try {
                    broadcaster.select(eventClass,
                       filteredServicesQualifiers(event,
                          new ExtensionActivator.SpecificationAnnotation(clazz),
                          instance))
                               .fire(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        private Annotation[] filteredServicesQualifiers(AbstractServiceEvent event,
                                                        ExtensionActivator.SpecificationAnnotation specific,
                                                        Instance<Object> instance) {
            Set<Annotation> eventQualifiers = new HashSet<Annotation>();
            eventQualifiers.add(specific);
            CDIOSGiExtension extension = instance.select(CDIOSGiExtension.class).get();
            for (Annotation annotation : extension.getObservers()) {
                String value = ((org.osgi.cdi.api.extension.annotation.Filter) annotation).value();
                try {
                    org.osgi.framework.Filter filter
                            = context.createFilter(value);
                    if (filter.match(event.getReference())) {
                        eventQualifiers.add(new ExtensionActivator.FilterAnnotation(value));
                    }
                } catch (InvalidSyntaxException ex) {
                    //ex.printStackTrace();
                }
            }
            return eventQualifiers.toArray(new Annotation[eventQualifiers.size()]);
        }

        private void fireAllEvent(AbstractBundleEvent event, Event broadcaster) {
            try {
                broadcaster.select(event.getClass(),
                   new ExtensionActivator.BundleNameAnnotation(event.getSymbolicName()),
                   new ExtensionActivator.BundleVersionAnnotation(event.getVersion().toString()))
                       .fire(event);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}


