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

package com.sample.web.app;

import com.sample.web.fwk.HttpServiceTracker;
import org.osgi.cdi.api.extension.events.BundleContainerEvents;
import org.osgi.cdi.api.extension.events.Invalid;
import org.osgi.cdi.api.extension.events.Valid;
import org.osgi.util.tracker.ServiceTracker;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class App {

    private static final String CONTEXT_ROOT = "/app";

    @Inject @Any Instance<Object> instances;
        
    private ServiceTracker tracker;
    private AtomicBoolean valid = new AtomicBoolean(false);

    public void start(@Observes BundleContainerEvents.BundleContainerInitialized init) throws Exception {
        this.tracker = new HttpServiceTracker(init.getBundleContext(), 
                getClass().getClassLoader(), instances, CONTEXT_ROOT);
        this.tracker.open();
    }
    
    public void validate(@Observes Valid event) {
        valid.compareAndSet(false, true);
    }

    public void invalidate(@Observes Invalid event) {
        valid.compareAndSet(true, false);
    }

    public boolean isValid() {
        return valid.get();
    }
}
