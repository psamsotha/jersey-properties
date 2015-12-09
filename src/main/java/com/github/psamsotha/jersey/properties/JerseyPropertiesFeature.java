package com.github.psamsotha.jersey.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.ws.rs.core.Configuration;

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

    /**
     * Disable the default {@code PropertiesProvider}.
     */
    public static final String DISABLE_DEFAULT_PROPERTIES_PROVIDER = "com.github.psamsotha.jersey.properties.DisableDefaultPropertiesProvider";

    private final PropertiesProvider[] userDefinedProviders;

    public JerseyPropertiesFeature() {
        this(new PropertiesProvider[0]);
    }

    public JerseyPropertiesFeature(PropertiesProvider... propertiesProviders) {
        this.userDefinedProviders = propertiesProviders;
    }

    @Override
    public boolean configure(FeatureContext configurable) {

        final Map<String, String> newMap = new HashMap<String, String>();
        addConfigProviderToMap(newMap, configurable.getConfiguration());

        final Map<String, Object> jerseyProps = configurable.getConfiguration().getProperties();

        ConfigProperties configProperties;
        final Locale defaultLocale 
                = PropertiesHelper.getValue(jerseyProps, DEFAULT_LOCALE, Locale.getDefault(), null);

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

            boolean disableDefaultProvider
                    = PropertiesHelper.getValue(jerseyProps, DISABLE_DEFAULT_PROPERTIES_PROVIDER, false, null);
            if (disableDefaultProvider) {
                if (userDefinedProviders.length == 0) {
                    LOGGER.log(Level.WARNING, "DefaultPropertiesProvider is disabled, "
                            + "and there are no other PropertyProviders registered.");
                }
            } else {
                String resourcePath = PropertiesHelper.getValue(jerseyProps, RESOURCE_PATH, String.class, null);
                if (resourcePath == null) {
                    LOGGER.log(Level.WARNING, "Resource path not set. To set a path to a"
                            + " properties file, use the configuration property {0}"
                            + " or disable this warning with property {1}.",
                            new Object[]{RESOURCE_PATH, DISABLE_DEFAULT_PROPERTIES_PROVIDER});
                } else {
                    addDefaultProviderToMap(newMap, resourcePath);
                } 
            }

            addUserProvidersToMap(newMap);
            configProperties = new DefaultConfigProperties(defaultLocale, newMap);
        }

        configurable.register(new Binder(new ConfigPropertiesFactory(configProperties)));
        return true;
    }

    /**
     * Add user defined {@code PropertiesProvider} to global properties.
     *
     * @param propertiesMap the global properties map.
     */
    private void addUserProvidersToMap(Map<String, String> propertiesMap) {
        if (userDefinedProviders.length != 0) {
            for (PropertiesProvider provider : userDefinedProviders) {
                propertiesMap.putAll(provider.getProperties());
            }
        }
    }

    /**
     * Add default {@code PropertiesProvider} to global properties.
     *
     * @param propertiesMap the global properties.
     * @param propertiesPath the path for the provider to find the properties
     * file.
     */
    private void addDefaultProviderToMap(Map<String, String> propertiesMap, String propertiesPath) {
        DefaultPropertiesProvider provider = new DefaultPropertiesProvider(propertiesPath);
        propertiesMap.putAll(provider.getProperties());
    }

    /**
     * Add JAX-RS {@code Configuration} properties to global properties.
     * @param propertiesMap the global properties map.
     * @param config the JAX-RX {@code Configuration} object.
     */
    private void addConfigProviderToMap(Map<String, String> propertiesMap, Configuration config) {
        ConfigurationPropertiesProvider provider = new ConfigurationPropertiesProvider(config);
        propertiesMap.putAll(provider.getProperties());
    }

    private Map<Locale, ResourceBundle> convertResourceBundles(String bundleName) {
        Map<Locale, ResourceBundle> bundlesMap = new HashMap<Locale, ResourceBundle>();

        for (Locale locale : Locale.getAvailableLocales()) {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            bundlesMap.put(locale, bundle);
        }

        return Collections.unmodifiableMap(bundlesMap);
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
