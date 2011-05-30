package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.AutoPublishedService;
import com.sample.osgi.bundle1.api.RandomInterface;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish(contracts = {
        RandomInterface.class
})
public class AutoPublishedServiceImpl2 implements AutoPublishedService {
    
    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
