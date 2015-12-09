/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.psamsotha.jersey.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author PaulSamsotha
 */
public class JerseyPropertiesFeatureProvidersTest extends JerseyTest {
    
    private static final String PROP_ONE_KEY = "prop.one";
    private static final String PROP_ONE_VALUE = "propOneValue";
    
    private static final String PROPERTY_PATH = "property-path";
    
    static class MorePropertiesProvider implements PropertiesProvider {
        
        private final Map<String, String> moreProps = new HashMap<String, String>();
        
        public MorePropertiesProvider() {
            moreProps.put(PROP_ONE_KEY, PROP_ONE_VALUE);
        }

        @Override
        public Map<String, String> getProperties() {
            return moreProps;
        }
    }
    
    @Singleton
    @Path(PROPERTY_PATH) 
    public static class MorePropertiesResource {
        
        private final String propOne;
        
        public MorePropertiesResource(@Prop(PROP_ONE_KEY) String propOne) {
            this.propOne = propOne;
        }
        
        @GET
        public String get() {
            return propOne;
        }
    }
    
    @Override
    public ResourceConfig configure() {
        return new ResourceConfig(MorePropertiesResource.class)
                .register(new JerseyPropertiesFeature(new MorePropertiesProvider()))
                .register(new LoggingFilter(Logger.getAnonymousLogger(), true))
                .property(JerseyPropertiesFeature.DISABLE_DEFAULT_PROPERTIES_PROVIDER, true);
    }
    
    @Test
    public void custom_PropertiesProvider_should_be_added_to_global_properties() {
        Response response = target(PROPERTY_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        assertEquals(PROP_ONE_VALUE, message);
        response.close();
    }
}
