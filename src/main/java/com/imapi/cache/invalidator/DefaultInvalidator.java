package com.imapi.cache.invalidator;

import com.imapi.cache.Cache;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation for invalidator which is using fixed timeout for querying sources.
 * Source queries happens in sequential order, however it is not guaranteed the responses order.
 * <p/>
 * <p>Scheduling with fixed timeout could lead to problems with cache invalidation, especially with several sources.
 * To avoid such situation timeout should be set large enough to complete all loading task.</p>
 * <p/>
 * <p>Also, in case of different nature of sources and frequent updates, it is better to use another invalidator
 * with more invalidation policy.</p>
 *
 * @param <U> cache element type
 * @author Ivan Bondarenko
 */
public class DefaultInvalidator<U> extends AbstractInvalidator<U> {

    /**
     * Default timeout in milliseconds
     */
    public static final long DEFAULT_TIMEOUT = 1000;

    private static final Logger LOGGER = Logger.getLogger(DefaultInvalidator.class.getName());

    private final long timeout;

    private ScheduledExecutorService scheduler;
    private ExecutorService worker;

    /**
     * Constructor with default invalidation timeout.
     */
    public DefaultInvalidator() {
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Constructor with timeout in milliseconds.
     *
     * @param timeout in milliseconds
     */
    public DefaultInvalidator(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Constructor with timeout in {@link TimeUnit}. Internally would be converted to milliseconds.
     *
     * @param timeout in {@link TimeUnit} measure
     * @param unit    TimeUnit
     */
    public DefaultInvalidator(long timeout, TimeUnit unit) {
        this(unit.toMillis(timeout));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Cache<U>> void scheduleOn(final T cache) {
        if (scheduler != null) throw new IllegalStateException("Cache Invalidator cannot be scheduled twice");

        scheduler = Executors.newScheduledThreadPool(1);
        final List<Callable<Iterable<U>>> sources = getSources();
        worker = Executors.newFixedThreadPool(sources.size());

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Future<Iterable<U>>> futures = worker.invokeAll(sources, timeout, TimeUnit.MILLISECONDS);
                    for (Future<Iterable<U>> future : futures) {
                        cache.put(future.get());
                    }
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, e.getMessage());
                } catch (ExecutionException e) {
                    LOGGER.log(Level.WARNING, e.getMessage());
                }
            }
        }, 0, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Have to be called when invalidation is not needed, otherwise invalidation won't stop.
     */
    @Override
    public void close() {
        scheduler.shutdown();
        worker.shutdown();
    }
}
