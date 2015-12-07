package com.github.psamsotha.jersey.properties;

import java.net.URL;
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
public class JerseyPropertiesFeatureDataTypesTest extends JerseyTest {

    private static final String RESOURCE = "/app.properties";
    private static final String PROPERTY_RESOURCE_PATH = "property";

    private static final String URL_PATH = "url-path";
    private static final String URL_PROP_KEY = "url.prop";
    private static final String URL_PROP_VALUE = "http://localhost:8080/test/url";

    private static final String FLOAT_PATH = "float-path";
    private static final String FLOAT_PROP_KEY = "float.prop";
    private static final String FLOAT_PROP_VALUE = "1234.56";

    private static final String DOUBLE_PATH = "double-path";
    private static final String DOUBLE_PROP_KEY = "double.prop";
    private static final String DOUBLE_PROP_VALUE = FLOAT_PROP_VALUE;

    private static final String INT_PATH = "int-path";
    private static final String INT_PROP_KEY = "int.prop";
    private static final String INT_PROP_VALUE = "123456";

    private static final String BOOLEAN_PATH = "boolean-path";
    private static final String BOOLEAN_PROP_KEY = "boolean.prop";
    private static final String BOOLEAN_PROP_VALUE = "true";

    private static final String HELLO_PATH = "hello-path";
    private static final String HELLO_PROP_KEY = "hello.prop";
    private static final String HELLO_PROP_VALUE = "Hello Props";

    @Path(PROPERTY_RESOURCE_PATH)
    public static class OtherTypesPropertiesResource {

        @GET
        @Path(URL_PATH)
        public Response getUrl(@Prop(URL_PROP_KEY) URL url) {
            if (url != null) {
                return Response.ok(url.toString()).build();
            }
            return Response.serverError().entity("url is null").build();
        }

        @GET
        @Path(FLOAT_PATH)
        public String getFloat(@Prop(FLOAT_PROP_KEY) float value) {
            return String.valueOf(value);
        }

        @GET
        @Path(DOUBLE_PATH)
        public String getDouble(@Prop(DOUBLE_PROP_KEY) double value) {
            return String.valueOf(value);
        }

        @GET
        @Path(INT_PATH)
        public String getInt(@Prop(INT_PROP_KEY) int value) {
            return String.valueOf(value);
        }

        @GET
        @Path(BOOLEAN_PATH)
        public String getBoolean(@Prop(BOOLEAN_PROP_KEY) boolean value) {
            return String.valueOf(value);
        }

        @GET
        @Path(HELLO_PATH)
        public String getHello(@Prop(HELLO_PROP_KEY) HelloProperty prop) {
            return prop.toString();
        }
    }

    @Override
    public ResourceConfig configure() {
        return new ResourceConfig(OtherTypesPropertiesResource.class)
                .register(JerseyPropertiesFeature.class)
                .register(HelloPropertyParamProvider.class)
                .property(JerseyPropertiesFeature.RESOURCE_PATH, RESOURCE)
                .register(new LoggingFilter(Logger.getAnonymousLogger(), true));
    }

    @Test
    public void url_property_should_inject_ok() {
        Response response = target(PROPERTY_RESOURCE_PATH).path(URL_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        response.close();
        assertEquals(URL_PROP_VALUE, message);
    }

    @Test
    public void float_property_should_inject_ok() {
        Response response = target(PROPERTY_RESOURCE_PATH).path(FLOAT_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        response.close();
        assertEquals(FLOAT_PROP_VALUE, message);
    }

    @Test
    public void double_property_should_inject_ok() {
        Response response = target(PROPERTY_RESOURCE_PATH).path(DOUBLE_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        response.close();
        assertEquals(DOUBLE_PROP_VALUE, message);
    }

    @Test
    public void int_property_should_inject_ok() {
        Response response = target(PROPERTY_RESOURCE_PATH).path(INT_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        response.close();
        assertEquals(INT_PROP_VALUE, message);
    }

    @Test
    public void boolean_property_should_inject_ok() {
        Response response = target(PROPERTY_RESOURCE_PATH).path(BOOLEAN_PATH).request().get();
        assertEquals(200, response.getStatus());
        String message = response.readEntity(String.class);
        response.close();
        assertEquals(BOOLEAN_PROP_VALUE, message);
    }

    @Test
    public void helloProperty_converter_should_be_used() {
        Response response = target(PROPERTY_RESOURCE_PATH).path(HELLO_PATH).request().get();
        assertEquals(200, response.getStatus());

        String message = response.readEntity(String.class);
        response.close();
        assertEquals(HELLO_PROP_VALUE, message);
    }
}
