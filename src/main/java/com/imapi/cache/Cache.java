package com.imapi.cache;

/**
 * General cache contract, only contains insert operation, because effective querying depends on implementation.
 * For example {@link RadixCache} and {@link SuffixCache}.
 *
 * @param <U> cache type parameter
 * @author Ivan Bondarenko
 */
public interface Cache<U> extends AutoCloseable {
    /**
     * Add {@link Iterable} to cache, method should be implemented as thread safe.
     *
     * @param elements elements to add
     */
    void put(Iterable<U> elements);
}
