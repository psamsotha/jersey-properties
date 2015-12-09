
package com.github.psamsotha.jersey.properties;

import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author Paul Samsotha
 */
public class JerseyPropertiesFeatureTest extends JerseyTest {
    
    private static final String RESOURCE = "/app.properties";
    
    private static final String FIELD_PROP_KEY = "field.prop";
    private static final String CONSTRUCTOR_PROP_KEY = "constructor.prop";
    private static final String METHOD_PARAM_PROP_KEY = "method.param.prop";
    
    private static final String FIELD_PROP_VALUE = "fieldPropValue";
    private static final String CONSTRUCTOR_PROP_VALUE = "constructorPropValue";
    private static final String METHOD_PARAM_PROP_VALUE = "methodParamPropValue";
    
    private static final String PROPERTY_RESOURCE_PATH = "property";

    private static final String MESSAGE_FORMAT = "field: %s; constructor: %s; method-param: %s; moxy-prop: %s";
    
    @Singleton
    @Path(PROPERTY_RESOURCE_PATH)
    public static class PropertyResource {
        
        @Prop(FIELD_PROP_KEY)
        private String fieldProp;
        
        @Prop(ServerProperties.MOXY_JSON_FEATURE_DISABLE)
        private String moxyProp;
        
        private final String constructorProp;
        
        public PropertyResource(
                @Prop(CONSTRUCTOR_PROP_KEY) String constructorProp) {
            this.constructorProp = constructorProp;
        }
        
        @GET
        public String get(@Prop(METHOD_PARAM_PROP_KEY) String methodProp) {
            return String.format(MESSAGE_FORMAT, fieldProp, constructorProp, methodProp, moxyProp);
        }  
    }

    
    @Override
    public ResourceConfig configure() {
        return new ResourceConfig(PropertyResource.class)
                .register(JerseyPropertiesFeature.class)
                .property(JerseyPropertiesFeature.RESOURCE_PATH, RESOURCE)
                .property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, "true")
                .register(new LoggingFilter(Logger.getAnonymousLogger(), true));
    }
    
    @Test
    public void properties_should_be_set_with_Prop_annotation() {
        Response response = target(PROPERTY_RESOURCE_PATH).request().get();
        assertEquals(200, response.getStatus());
        
        String message = response.readEntity(String.class);
        response.close();
        
        assertThat(message, containsString(FIELD_PROP_VALUE));
        assertThat(message, containsString(CONSTRUCTOR_PROP_VALUE));
        assertThat(message, containsString(METHOD_PARAM_PROP_VALUE));
        assertThat(message, containsString("true"));
    }
}
