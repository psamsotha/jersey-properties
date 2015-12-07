
package com.github.psamsotha.jersey.properties;

/**
 *
 * @author Paul Samsotha
 */
public class HelloProperty {
    
    private String value;
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
