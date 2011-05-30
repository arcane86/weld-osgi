package com.sample.osgi.bundle2.impl;

import com.sample.osgi.bundle1.api.AutoPublishedService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.context.ApplicationScoped;

@Publish
@ApplicationScoped
public class AutoPublishedServiceImpl implements AutoPublishedService {
    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
