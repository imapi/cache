package com.imapi.cache.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Dummy source which generates char sequences.
 */
public class RandomSource implements Callable<Iterable<String>> {

    private static final Random RND = new Random();

    @Override
    public List<String> call() {
        int numberOfWords = RND.nextInt(10);
        List<String> words = new ArrayList<>(numberOfWords);
        for (int i = 0; i < numberOfWords; i++) {
            char[] word = new char[RND.nextInt(8) + 3];
            for (int j = 0; j < word.length; j++) {
                word[j] = (char) ('a' + RND.nextInt(26));
            }
            words.add(String.valueOf(word));
        }
        return words;
    }
}
