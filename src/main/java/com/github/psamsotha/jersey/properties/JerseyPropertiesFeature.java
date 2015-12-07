package com.github.psamsotha.jersey.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

/**
 * Feature class to register for the properties support.
 *
 * @author Paul Samsotha
 */
public class JerseyPropertiesFeature implements Feature {

    private static final Logger LOGGER = Logger.getLogger(JerseyPropertiesFeature.class.getName());

    /**
     * The class-path resource path for the properties file. Used only for the
     * default (non-i18n) feature.
     */
    public static final String RESOURCE_PATH = "com.github.psamsotha.jersey.properties.resourcePath";

    /**
     * Enable i18n. If this is used, {@code RESOURCE_BUNDLE} should also b set.
     */
    public static final String ENABLE_I18N = "com.github.psamsotha.jersey.properties.enableI18N";

    /**
     * The resource bundle name to use for i18n support.
     */
    public static final String RESOURCE_BUNDLE = "com.github.psamsotha.jersey.properties.ResourceBundle";

    /**
     * The default {@code Locale} to use for i18n support. If not specified, and
     * i18n support has been turned on, the default locale will be
     * {@code Lccale.getDefault()}.
     */
    public static final String DEFAULT_LOCALE = "com.github.psamsotha.jersey.properties.DefaultLocale";

    @Override
    public boolean configure(FeatureContext configurable) {
        Map<String, Object> jerseyProps = configurable.getConfiguration().getProperties();

        ConfigProperties configProperties;
        final Locale defaultLocale = PropertiesHelper.getValue(jerseyProps, DEFAULT_LOCALE, Locale.getDefault(), null);

        boolean i18nEnabled = PropertiesHelper.getValue(jerseyProps, ENABLE_I18N, false, null);
        if (i18nEnabled) {
            String bundleName = PropertiesHelper.getValue(jerseyProps, RESOURCE_BUNDLE, String.class, null);
            if (bundleName == null) {
                LOGGER.warning("Resource bundle property not set.");
                throw new RuntimeException("Resource bundle property required for i18n support.");
            }

            Map<Locale, ResourceBundle> bundles = convertResourceBundles(bundleName);
            configProperties = new I18NConfigProperties(defaultLocale, bundles);
        } else {
            String resourcePath = PropertiesHelper.getValue(jerseyProps, RESOURCE_PATH, String.class, null);
            if (resourcePath == null) {
                LOGGER.log(Level.INFO, "Resource path not set. To set a path to a"
                        + " properties file, use the configuration property {0}",
                        new Object[]{RESOURCE_PATH});
            }

            Properties properties = loadPropsFromPath(resourcePath);
            for (String jerseyProp : jerseyProps.keySet()) {
                Object value = jerseyProps.get(jerseyProp);
                if (value instanceof String) {
                    properties.put(jerseyProp, (String) value);
                }
            }

            Map<String, String> delegate = mapFromProperties(properties);
            configProperties = new DefaultConfigProperties(defaultLocale, delegate);
        }

        configurable.register(new Binder(new ConfigPropertiesFactory(configProperties)));
        return true;
    }

    private Map<Locale, ResourceBundle> convertResourceBundles(String bundleName) {
        Map<Locale, ResourceBundle> bundlesMap = new HashMap<Locale, ResourceBundle>();

        for (Locale locale : Locale.getAvailableLocales()) {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            bundlesMap.put(locale, bundle);
        }

        return Collections.unmodifiableMap(bundlesMap);
    }

    private Map<String, String> mapFromProperties(Properties properties) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration propNames = properties.propertyNames();
        while (propNames.hasMoreElements()) {
            String next = (String) propNames.nextElement();
            map.put(next, properties.getProperty(next));
        }
        return Collections.unmodifiableMap(map);
    }

    private Properties loadPropsFromPath(String path) {
        Properties properties = new Properties();
        InputStream is = this.getClass().getResourceAsStream(path);
        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream(path);
        }
        if (is == null) {
            LOGGER.log(Level.WARNING, "Error loading resource with path {0}. "
                    + "Properties will not correctly set", new Object[]{path});
        }
        try {
            properties.load(is);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error loading Properties.", ex);
            // does not fail.
        }
        return properties;
    }

    private static class Binder extends AbstractBinder {

        private final ConfigPropertiesFactory configPropertiesFactory;

        public Binder(ConfigPropertiesFactory configPropertiesFactory) {
            this.configPropertiesFactory = configPropertiesFactory;
        }

        @Override
        protected void configure() {
            bindFactory(configPropertiesFactory).to(ConfigProperties.class);

            bind(PropertiesValueFactoryProvider.PropertyInjectionResolver.class)
                    .to(new TypeLiteral<InjectionResolver<Prop>>() {
                    }).in(Singleton.class);

            bind(PropertiesValueFactoryProvider.class)
                    .to(ValueFactoryProvider.class)
                    .in(Singleton.class);
        }
    }
}
