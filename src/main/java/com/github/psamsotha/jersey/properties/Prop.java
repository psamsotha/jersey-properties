
package com.github.psamsotha.jersey.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 *
 * @author Paul Samsotha
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, CONSTRUCTOR, PARAMETER})
public @interface Prop {
    
    String value();
}
