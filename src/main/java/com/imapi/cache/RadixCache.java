package com.imapi.cache;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

/**
 * Radix tree cache implementation based on {@link ConcurrentRadixTree}. This cache allow to store different
 * {@link CharSequence} and query them by closest match (e. g. within records {@code "Test me", "Test one", "Go home"}
 * for query {@code "Test him"} it will return {@code "Test me", "Test one"}.
 * <p>Also {@link RadixCache#startingWith(String)} method is available.</p>
 * <p><tt>Note</tt> that this cache not designed with restriction in size or number of elements.</p>
 *
 * @param <U> subtype of {@link CharSequence}
 * @author Ivan Bondarenko
 */
public class RadixCache<U extends CharSequence> extends AbstractCache<U> {

    private final RadixTree<Integer> tree = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Iterable<U> elements) {
        for (U element : elements) {
            tree.putIfAbsent(element, 0);
        }
    }

    /**
     * Returns a lazy iterable which returns the set of keys in the tree which are the closest match for the given
     * candidate key.
     * <p/>
     * Example:<br/>
     * Tree contains: {@code Ford Focus}, {@code Ford Mondeo}, {@code BMW M3}<br/>
     * <code>getClosestKeys("Ford F150")</code> -> returns {@code Ford Focus}, {@code Ford Mondeo}<br/>
     * <p/>
     * This is <i>inclusive</i> - if the given candidate is an exact match for a key in the tree, that key is also
     * returned.
     *
     * @param candidate A candidate key
     * @return The set of keys in the tree which most closely match the candidate key, inclusive
     */
    public Iterable<CharSequence> closest(String candidate) {
        return tree.getClosestKeys(candidate);
    }

    /**
     * Returns a lazy iterable which returns the set of values associated with keys in the tree which start with the
     * given prefix.
     * <p/>
     * This is <i>inclusive</i> - if the given prefix is an exact match for a key in the tree, the value associated
     * with that key is also returned.
     * <p/>
     * Note that although the same value might originally have been associated with multiple keys, the set returned
     * does not contain duplicates (as determined by the value objects' implementation of {@link #equals(Object)}).
     *
     * @param prefix A prefix of keys in the tree for which associated values are sought
     * @return The set of values associated with keys in the tree which start with the given prefix, inclusive
     */
    public Iterable<CharSequence> startingWith(String prefix) {
        return tree.getKeysStartingWith(prefix);
    }
}
