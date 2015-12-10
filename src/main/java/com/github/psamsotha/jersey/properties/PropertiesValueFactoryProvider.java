package com.github.psamsotha.jersey.properties;

import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Configuration;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;

/**
 *
 * @author Paul Samsotha
 */
@Singleton
final class PropertiesValueFactoryProvider extends AbstractValueFactoryProvider {

    private final ServiceLocator serviceLocator;
    private final boolean i18nEnabled;

    @Inject
    public PropertiesValueFactoryProvider(MultivaluedParameterExtractorProvider mpep,
            ServiceLocator locator,
            Configuration configuration) {
        super(mpep, locator, Parameter.Source.UNKNOWN);
        this.serviceLocator = locator;
        this.i18nEnabled = PropertiesHelper.getValue(configuration.getProperties(),
                RuntimeType.SERVER, JerseyPropertiesFeature.ENABLE_I18N, false, null);
    }

    @Singleton
    static final class PropertyInjectionResolver extends ParamInjectionResolver<Prop> {

        public PropertyInjectionResolver() {
            super(PropertiesValueFactoryProvider.class);
        }
    }

    private static class PropertyFactory extends AbstractContainerRequestValueFactory<Object> {

        private final MultivaluedParameterExtractor<?> extractor;
        private final Parameter parameter;
        private final boolean i18nEnabled;

        public PropertyFactory(MultivaluedParameterExtractor<?> extractor, Parameter parameter, boolean i18nEnabled) {
            this.extractor = extractor;
            this.parameter = parameter;
            this.i18nEnabled = i18nEnabled;
        }

        @Inject
        private ConfigProperties properties;

        @Override
        public Object provide() {
            if (i18nEnabled) {
                List<Locale> languages = getContainerRequest().getAcceptableLanguages();
                // not wildcard will be null, and ConfigProperties will use its default
                if (!"*".equals(languages.get(0).toString())) {
                    Locale locale = languages.get(0);
                    ThreadLocalLocale.set(locale);
                }
            }
            try {
                Object cached = properties.getCachedPropery(parameter);
                if (cached != null) {
                    return cached;
                }
                Object value = extractor.extract(properties);
                if (value != null) {
                    properties.putCachedProperty(parameter, value);
                }
                return value;
            } catch (Exception ex) {
                throw new ServerErrorException("Error processing property.", 500, ex);
            } finally {
                if (i18nEnabled) {
                    ThreadLocalLocale.remove();
                }
            }
        }
    }

    @Override
    protected Factory<?> createValueFactory(Parameter parameter) {

        if (!parameter.isAnnotationPresent(Prop.class)) {
            return null;
        }

        String parameterName = parameter.getSourceName();
        if (parameterName == null || parameterName.length() == 0) {
            // Invalid parameter name
            return null;
        }

        MultivaluedParameterExtractor e = get(parameter);
        if (e == null) {
            return null;
        }

        PropertyFactory factory = new PropertyFactory(e, parameter, i18nEnabled);
        serviceLocator.inject(factory);
        return factory;
    }
}
