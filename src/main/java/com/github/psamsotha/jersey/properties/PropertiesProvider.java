
package com.github.psamsotha.jersey.properties;

import java.util.Map;

/**
 * Provides a properties map for the {@code JerseyPropertiesFeature}.
 * 
 * @author Paul Samsotha
 */
public interface PropertiesProvider {
    
    /**
     * Get provider map.
     * 
     * @return the properties map.
     */
    Map<String, String> getProperties();
}
