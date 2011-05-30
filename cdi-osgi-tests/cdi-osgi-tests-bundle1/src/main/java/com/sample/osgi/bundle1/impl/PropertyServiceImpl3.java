package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.PropertyService;
import org.osgi.cdi.api.extension.annotation.Property;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish(properties = {
        @Property(name = "name",value = "2")
})
//@Name("2")
public class PropertyServiceImpl3 implements PropertyService {

    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
