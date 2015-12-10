package com.github.psamsotha.jersey.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default properties provider. Will turn a [@code Properties} object into a
 * map.
 *
 * @author Paul Samsotha
 */
class DefaultPropertiesProvider implements PropertiesProvider {
    
    private static final Logger LOGGER = Logger.getLogger(DefaultPropertiesProvider.class.getName());

    private final Map<String, String> propertiesMap;

    public DefaultPropertiesProvider(String propertiesPath) {
        Properties properties = loadPropsFromPath(propertiesPath);
        propertiesMap = mapFromProperties(properties);
    }

    @Override
    public Map<String, String> getProperties() {
        return propertiesMap;
    }

    private Map<String, String> mapFromProperties(Properties properties) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration propNames = properties.propertyNames();
        while (propNames.hasMoreElements()) {
            String next = (String) propNames.nextElement();
            map.put(next, properties.getProperty(next));
        }
        return Collections.unmodifiableMap(map);
    }

    private Properties loadPropsFromPath(String path) {
        Properties properties = new Properties();
        InputStream is = this.getClass().getResourceAsStream(path);
        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream(path);
        }
        if (is == null) {
            LOGGER.log(Level.WARNING, "Error loading resource with path {0}. "
                    + "Properties will not correctly set", new Object[]{path});
        }
        try {
            properties.load(is);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error loading Properties.", ex);
            // does not fail.
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Error closing stream.", ex);
                }
            } 
        }
        return properties;
    }
}
