package com.github.psamsotha.jersey.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.server.model.Parameter;

/**
 * This class is not meant to be used as a {@code MultivaluedMap} will map
 * functionality. It is immutable, and lacks other expected functionality of a
 * multi-valued map. It is only created as such, so it can be passed to the
 * extractor in the {@code AbstractValueFactoryProvider}. Other than the
 * required {@code getFirst} called by extractor, other {@code MultivaluedMap}
 * methods, are pretty much moot as they will not be used. This class is only
 * meant to be used internally.
 *
 * Concrete subclasses should have a caching mechanism to cache the parameter
 * value, with a {@code Parameter} type key.
 *
 * @author Paul Samsotha
 */
abstract class ConfigProperties<K, V> implements MultivaluedMap<String, String> {

    protected final Locale defaultLocale;
    protected final Map<K, V> delegate;

    /**
     * Construct a {@code ConfigProperties} with a default locale and the
     * underlying delegate map.
     *
     * @param defaultLocale the default locale
     * @param delegate the delegate map.
     */
    ConfigProperties(Locale defaultLocale, Map<K, V> delegate) {
        if (defaultLocale == null) {
            throw new NullPointerException("Default locale cannot be null.");
        }
        if (delegate == null) {
            throw new NullPointerException("ConfigProperties delegate map must not be null.");
        }
        this.defaultLocale = defaultLocale;
        this.delegate = delegate;
    }

    /**
     * Retrieves the cached value for the {@code Parameter} key.
     *
     * @param parameter the method parameter used as the cache key.
     * @return the cached value for the {@code Parameter).
     */
    abstract Object getCachedPropery(Parameter parameter);

    /**
     * Puts a new value into the cache with the {@code Parameter} key.
     *
     * @param key the method parameter object
     * @param value the value to cache.
     */
    abstract void putCachedProperty(Parameter key, Object value);

    protected static final String UNSUPPORTED_MESSAGE = "ConfigProperties is immutable.";

    @Override
    public Set keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @Override
    public Set entrySet() {
        return delegate.entrySet();
    }

    @Override
    public void putSingle(String k, String v) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void add(String k, String v) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void addAll(String k, String... vs) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void addAll(String k, List<String> list) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void addFirst(String k, String v) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public boolean equalsIgnoreValueOrder(MultivaluedMap<String, String> mm) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public List<String> remove(Object key) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ConfigProperties)) {
            return false;
        }

        ConfigProperties that = (ConfigProperties) o;

        return delegate.equals(that.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
