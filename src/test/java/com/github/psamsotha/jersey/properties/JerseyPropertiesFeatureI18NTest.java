
package com.github.psamsotha.jersey.properties;

import java.util.Locale;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 *
 * @author Paul Samsotha
 */
public class JerseyPropertiesFeatureI18NTest extends JerseyTest {
    
    private static final String MESSAGE_PROP = "message";
    private static final String PROPERTIES_PATH = "properties-path";
    private static final String THREAD_LOCAL_PATH = "thread-local-path";
    private static final String NO_THREAD_LOCAL_MESSAGE = "No locale in thread local";
    private static final String HAS_THREAD_LOCAL_MESSAGE = "Has locale in thread local";
    
    private static final String EN_US_MESSAGE = "Blah in US English";
    private static final String DE_DE_MESSAGE = "Blah in German";
    private static final String FR_FR_MESSAGE = "Blah in French";
    
    @Path(PROPERTIES_PATH)
    public static class I18NPropertiesResource {
        
        @GET
        public String get(@Prop(MESSAGE_PROP) String message) {
            return message;
        }
        
        @GET
        @Path(THREAD_LOCAL_PATH)
        public String get() {
            Locale locale = ThreadLocalLocale.get();
            if (locale == null) {
                return NO_THREAD_LOCAL_MESSAGE;
            } else {
                return HAS_THREAD_LOCAL_MESSAGE;
            }
        }
    }
    
    @Override
    public ResourceConfig configure() {
        return new ResourceConfig(I18NPropertiesResource.class)
                .register(JerseyPropertiesFeature.class)
                .property(JerseyPropertiesFeature.ENABLE_I18N, true)
                .property(JerseyPropertiesFeature.RESOURCE_BUNDLE, "Messages")
                .register(new LoggingFilter(Logger.getAnonymousLogger(), true));
    }
    
    @Test
    public void should_return_default_message() {
        Response response = target(PROPERTIES_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        assertNotNull(message);
        assertEquals(EN_US_MESSAGE, message);
    }
    
    @Test
    public void should_return_fr_FR_message() {
        Response response = target(PROPERTIES_PATH).request()
                .acceptLanguage(new Locale("fr", "FR"))
                .get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        assertNotNull(message);
        assertEquals(FR_FR_MESSAGE, message);
    }
    
    @Test
    public void should_return_de_DE_message() {
        Response response = target(PROPERTIES_PATH).request()
                .acceptLanguage(new Locale("de", "DE"))
                .get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        assertNotNull(message);
        assertEquals(DE_DE_MESSAGE, message);
    }
    
    @Test
    public void should_be_no_thread_local_locale() {
        Response response = target(PROPERTIES_PATH).path(THREAD_LOCAL_PATH).request()
                .acceptLanguage(new Locale("de", "DE"))
                .get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        assertEquals(NO_THREAD_LOCAL_MESSAGE, message);
    }
}
