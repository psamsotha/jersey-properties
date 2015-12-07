package com.github.psamsotha.jersey.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.jersey.server.model.Parameter;

/**
 * i18n implementation of {@code ConfigProperties} with underlying resource
 * bundles.
 *
 * @author Paul Samsotha
 */
class I18NConfigProperties extends ConfigProperties<Locale, ResourceBundle> {

    private final Map<Parameter, Map<Locale, Object>> cachedBundles
            = new ConcurrentHashMap<Parameter, Map<Locale, Object>>();

    I18NConfigProperties(Locale defaultLocale, Map<Locale, ResourceBundle> delegate) {
        super(defaultLocale, delegate);
    }

    @Override
    Object getCachedPropery(Parameter parameter) {
        Locale locale = ThreadLocalLocale.get();
        Map<Locale, Object> props = cachedBundles.get(parameter);
        if (props == null) {
            props = new ConcurrentHashMap<Locale, Object>();
            cachedBundles.put(parameter, props);
            return null;
        } else {
            return props.get(locale);
        }
    }

    @Override
    void putCachedProperty(Parameter key, Object value) {
        Map<Locale, Object> props = cachedBundles.get(key);
        if (props == null) {
            props = new ConcurrentHashMap<Locale, Object>();
        }
        Locale locale = ThreadLocalLocale.get();
        if (locale == null) {
            locale = defaultLocale;
        }
        props.put(locale, value);
        cachedBundles.put(key, props);
    }

    @Override
    public String getFirst(String key) {
        Locale locale = ThreadLocalLocale.get();
        if (locale == null) {
            locale = defaultLocale;
        }
        ResourceBundle bundle = delegate.get(locale);
        if (bundle == null) {
            locale = defaultLocale;
        }
        bundle = delegate.get(locale);
        return bundle.getString(key);
    }

    @Override
    public List<String> get(Object key) {
        Locale locale = ThreadLocalLocale.get();
        if (locale == null) {
            locale = defaultLocale;
        }
        ResourceBundle bundle = delegate.get(locale);
        String value = bundle.getString((String) key);
        return value == null ? Collections.unmodifiableList(Arrays.asList(value)) : null;
    }
}
