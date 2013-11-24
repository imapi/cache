package com.imapi.cache;

import com.imapi.cache.invalidator.AbstractInvalidator;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Cache builder helper. Helps to build different caches with different sources and invalidators.
 * <p>To implement custom cache builder {@link AbstractBuilder} could be used.</p>
 *
 * @author Ivan Bondarenko
 */
public class CacheBuilder {

    private CacheBuilder() {
        //static helper class
    }

    /**
     * Generic Builder with invalidator and sources handling.
     *
     * @param <B> concrete Builder implementation
     * @param <U> cache type
     */
    protected static abstract class AbstractBuilder<B extends AbstractBuilder<B, U>, U> {

        /**
         * Generic invalidator, check for {@code null} in {@link CacheBuilder.AbstractBuilder#build()} present.
         */
        protected AbstractInvalidator<U> invalidator;

        /**
         * List of sources, should be set and not empty.
         */
        protected List<? extends Callable<Iterable<U>>> sources;

        /**
         * Self method for handling inheritance and type erasure
         *
         * @return {@code this} should be returned
         */
        protected abstract B self();

        /**
         * General build method with invalidator and sources handling.
         *
         * @return always null
         */
        protected Cache build() {
            if (invalidator == null) throw new IllegalArgumentException("Invalidator should be set");
            if (sources == null || sources.isEmpty()) throw new IllegalArgumentException("Sources should not be empty");
            invalidator.setSources(sources);
            return null;
        }

        /**
         * Invalidator setter
         *
         * @param invalidator {@link AbstractInvalidator}
         *
         * @return concrete builder
         */
        public B invalidator(AbstractInvalidator<U> invalidator) {
            this.invalidator = invalidator;
            return self();
        }

        /**
         * Sources setter
         *
         * @param sources {@link List} of sources, for example {@link com.imapi.cache.source.RandomSource}
         *
         * @return concrete builder
         */
        public B sources(List<? extends Callable<Iterable<U>>> sources) {
            this.sources = sources;
            return self();
        }
    }

    /**
     * Radix cache builder
     *
     * @param <U> type which extends {@link CharSequence}
     */
    public static class RadixBuilder<U extends CharSequence> extends AbstractBuilder<RadixBuilder<U>, U> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected RadixBuilder<U> self() {
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public RadixCache<U> build() {
            super.build();
            RadixCache<U> cache = new RadixCache<>();
            cache.closeOnExit(invalidator);
            invalidator.scheduleOn(cache);
            return cache;
        }
    }

    /**
     * Suffix cache builder
     *
     * @param <U> type which extends {@link CharSequence}
     */
    public static class SuffixBuilder<U extends CharSequence> extends AbstractBuilder<SuffixBuilder<U>, U> {

        /**
         * {@inheritDoc}
         */
        @Override
        public SuffixCache<U> build() {
            super.build();
            SuffixCache<U> cache = new SuffixCache<>();
            cache.closeOnExit(invalidator);
            invalidator.scheduleOn(cache);
            return cache;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SuffixBuilder<U> self() {
            return this;
        }
    }
}
