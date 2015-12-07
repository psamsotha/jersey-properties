
package com.github.psamsotha.jersey.properties;

import org.glassfish.hk2.api.Factory;

/**
 *
 * @author Paul Samsotha
 */
final class ConfigPropertiesFactory implements Factory<ConfigProperties> {
    
    private final ConfigProperties configProperties;
    
    ConfigPropertiesFactory(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public ConfigProperties provide() {
        return configProperties;
    }

    @Override
    public void dispose(ConfigProperties props) {}
}
