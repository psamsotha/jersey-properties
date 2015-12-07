/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.psamsotha.jersey.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.jersey.server.model.Parameter;

/**
 *
 * @author PaulSamsotha
 */
public class DefaultConfigProperties extends ConfigProperties<String, String> {
    
    private final Map<Parameter, Object> cachedProperties = new ConcurrentHashMap<Parameter, Object>();
    
    DefaultConfigProperties(Locale defaultLocale, Map<String, String> delegate) {
        super(defaultLocale, delegate);
    }

    @Override
    Object getCachedPropery(Parameter parameter) {
        return cachedProperties.get(parameter);
    }

    @Override
    void putCachedProperty(Parameter key, Object value) {
        cachedProperties.put(key, value);
    }

    @Override
    public String getFirst(String key) {
        return delegate.get(key);
    }

    @Override
    public List<String> get(Object key) {
        String value = getFirst((String) key);
        return value != null ? Collections.unmodifiableList(Arrays.asList(value)) : null;
    }
}
