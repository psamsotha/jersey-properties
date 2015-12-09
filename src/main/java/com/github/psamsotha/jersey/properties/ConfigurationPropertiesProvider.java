

package com.github.psamsotha.jersey.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Configuration;
import org.jvnet.tiger_types.Types;

/**
 * Properties provider for JAX-RS {@code Configuration} map.
 * 
 * @author Paul Samsotha
 */
class ConfigurationPropertiesProvider implements PropertiesProvider {
    
    private final Map<String, String> propertiesMap;
    
    public ConfigurationPropertiesProvider(Configuration configuration) {
        Map<String, Object> configProperties = configuration.getProperties();
        propertiesMap = convertToAcceptStringAndPrimitives(configProperties);
    }

    @Override
    public Map<String, String> getProperties() {
        return propertiesMap;
    }
    
    private Map<String, String> convertToAcceptStringAndPrimitives(Map<String, Object> configProperties) {
        Map<String, String> newMap = new HashMap<String, String>();
        for (Map.Entry<String, Object> prop: configProperties.entrySet()) {
            Object value = prop.getValue();
            if (value instanceof String) {
                newMap.put(prop.getKey(), (String) value);
            } else if (Types.isPrimitive(value.getClass())) {
                newMap.put(prop.getKey(), String.valueOf(value));
            }
        }
        return Collections.unmodifiableMap(newMap);
    }
}
