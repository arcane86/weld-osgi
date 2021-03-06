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

package com.sample.osgi.extension;

import com.sample.osgi.bundle1.api.Name;
import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.annotation.OSGiService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.inject.Inject;

@Publish
public class ServiceExtensionProvider {

    @Inject
    @Name("extension")
    private PropertyService serviceExtension;

    @Inject
    @OSGiService
    @Name("extension")
    private PropertyService serviceExtensionService;

    public PropertyService getServiceExtension() {
        return serviceExtension;
    }

    public PropertyService getServiceExtensionService() {
        return serviceExtensionService;
    }
}
