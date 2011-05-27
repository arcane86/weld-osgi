package com.sample.osgi.bundle1.impl;

import com.sample.osgi.bundle1.api.MyService;
import org.osgi.cdi.api.extension.annotation.Publish;

import javax.enterprise.context.ApplicationScoped;

@Publish
@ApplicationScoped
public class MyServiceImpl implements MyService {

    @Override
    public String doThat(String action) {
        return action;
    }
}
