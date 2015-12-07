
package com.github.psamsotha.jersey.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

/**
 *
 * @author Paul Samsotha
 */
public class HelloPropertyParamProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] antns) {
        if (HelloProperty.class != rawType) {
            return null;
        }
        
        return (ParamConverter<T>) new ParamConverter<HelloProperty>() {

            @Override
            public HelloProperty fromString(String string) {
                HelloProperty prop = new HelloProperty();
                prop.setValue(string);
                return prop;
            }

            @Override
            public String toString(HelloProperty prop) {
                return prop.toString();
            }
        };
    } 
}
