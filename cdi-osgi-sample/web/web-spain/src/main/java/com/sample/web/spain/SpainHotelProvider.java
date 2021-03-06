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

package com.sample.web.spain;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class SpainHotelProvider implements HotelProvider {

    @Override
    public Collection<Hotel> hotels() {
        Collection<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("Catalonia Plaza Mayor", "Madrid", "Spain"));
        hotels.add(new Hotel("emperador", "Madrid", "Spain"));
        hotels.add(new Hotel("Il Castillas hotel", "Madrid", "Spain"));
        hotels.add(new Hotel("Ada Palace", "Madrid", "Spain"));
        hotels.add(new Hotel("Palafox Central Suites", "Madrid", "Spain"));
        return hotels;
    }

    @Override
    public String getCountry() {
        return "Spain";
    }
}
