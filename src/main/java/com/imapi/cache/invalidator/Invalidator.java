package com.imapi.cache.invalidator;

import com.imapi.cache.Cache;

/**
 * Scheduling data loading from different sources {@link com.imapi.cache.source.RandomSource}.
 *
 * @param <U> cache element type
 * @author Ivan Bondarenko
 */
public interface Invalidator<U> extends AutoCloseable {

    /**
     * Scheduling an invalidator to update cache from different sources (or another invalidation policy).
     *
     * @param cache {@link Cache} to invalidate
     * @param <T>   cache implementation type (if some specific handling needed)
     */
    <T extends Cache<U>> void scheduleOn(T cache);
}
