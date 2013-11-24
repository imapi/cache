package com.imapi.cache.invalidator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Abstract Invalidator {@link Invalidator} implementation with List sources list field.
 * Sources are unmodifiable List, so they could be set up only during initialization process.
 *
 * @param <U> cache element type
 * @author Ivan Bondarenko
 */
public abstract class AbstractInvalidator<U> implements Invalidator<U> {

    private List<Callable<Iterable<U>>> sources;

    /**
     * @return Unmodifiable List of sources
     */
    protected List<Callable<Iterable<U>>> getSources() {
        return sources;
    }

    /**
     * @param sources setter for sources, should be not empty
     */
    public void setSources(List<? extends Callable<Iterable<U>>> sources) {
        this.sources = Collections.unmodifiableList(sources);
    }
}
