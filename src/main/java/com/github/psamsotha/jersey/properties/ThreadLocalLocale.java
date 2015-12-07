
package com.github.psamsotha.jersey.properties;

import java.util.Locale;

/**
 *
 * @author Paul Samsotha
 */
class ThreadLocalLocale {
    
    private static final ThreadLocal<Locale> locales = new ThreadLocal<Locale>();
    
    public static void set(Locale locale) {
        locales.set(locale);
    }
    
    public static Locale get() {
        return locales.get();
    }
    
    public static void remove() {
        locales.remove();
    }
}
