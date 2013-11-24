package com.imapi.cache;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract Cache implementation with close method, which closes opened resources (invalidators, etc) added to closeOnExit.
 *
 * @param <U> cache type
 * @author Ivan Bondarenko
 */
public abstract class AbstractCache<U> implements Cache<U> {

    private final List<AutoCloseable> closeables = new ArrayList<>(1);

    /**
     * Close resource with cache exit. Could be used for closing several resources.
     * For example:
     * {@code closeOnExit(first); closeOnExit(second);} - {@code first} and {@code second} will be closed.
     *
     * @param closeable closeable resource {@link AutoCloseable}
     */
    void closeOnExit(AutoCloseable closeable) {
        closeables.add(closeable);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>After closing cache data would be accessible, but invalidation would not occur</p>
     */
    @Override
    public void close() {
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
