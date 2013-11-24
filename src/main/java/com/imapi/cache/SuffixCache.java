package com.imapi.cache;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;

/**
 * Suffix tree cache implementation based on {@link ConcurrentSuffixTree}. Simular to {@link RadixCache}, the main
 * difference is that suffix tree allow containing match {@link SuffixCache#containg(String)}
 * as well as ending match {@link SuffixCache#endingWith(String)}
 * <p><tt>Note</tt> that this cache not designed with restriction in size or number of elements.</p>
 *
 * @param <U> subtype of {@link CharSequence}
 * @author Ivan Bondarenko
 */
public class SuffixCache<U extends CharSequence> extends AbstractCache<U> {

    private final SuffixTree<Integer> tree = new ConcurrentSuffixTree<>(new DefaultCharArrayNodeFactory());

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Iterable<U> elements) {
        for (U element : elements) {
            tree.put(element, 0);
        }
    }

    /**
     * Returns a lazy iterable which returns the set of keys in the tree which contain the given fragment.
     * <p/>
     * This is <i>inclusive</i> - if the given fragment is an exact match for a key in the tree, that key is also
     * returned.
     *
     * @param fragment A fragment of sought keys in the tree
     * @return The set of keys in the tree which contain the given fragment, inclusive
     */
    public Iterable<CharSequence> containg(String fragment) {
        return tree.getKeysContaining(fragment);
    }

    /**
     * Returns a lazy iterable which returns the set of keys in the tree which end with the given suffix.
     * <p/>
     * This is <i>inclusive</i> - if the given suffix is an exact match for a key in the tree, that key is also
     * returned.
     *
     * @param suffix A suffix of sought keys in the tree
     * @return The set of keys in the tree which end with the given suffix, inclusive
     */
    public Iterable<CharSequence> endingWith(String suffix) {
        return tree.getKeysEndingWith(suffix);
    }
}
