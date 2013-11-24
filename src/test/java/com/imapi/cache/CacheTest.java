package com.imapi.cache;

import com.imapi.cache.invalidator.DefaultInvalidator;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.Callable;

import static org.testng.AssertJUnit.assertEquals;

public class CacheTest {

    private static final List<String> data = Arrays.asList("a", "aa", "aaa", "ab", "abb", "abbb");

    private static <T extends CharSequence> List<T> copyIterator(Iterable<T> iterable) {
        List<T> copy = new ArrayList<>();
        Iterator<T> iter = iterable.iterator();
        while (iter.hasNext()) copy.add(iter.next());
        return copy;
    }

    private RadixCache<String> rCache;
    private SuffixCache<String> sCache;

    private static class DummySource implements Callable<Iterable<String>> {
        @Override
        public Iterable<String> call() throws Exception {
            Thread.sleep(50);
            return data;
        }
    }

    @BeforeTest
    public void setUp() {
        rCache = new CacheBuilder.RadixBuilder<String>()
                .invalidator(new DefaultInvalidator<String>(100))
                .sources(Collections.singletonList(new DummySource()))
                .build();

        sCache = new CacheBuilder.SuffixBuilder<String>()
                .invalidator(new DefaultInvalidator<String>(100))
                .sources(Collections.singletonList(new DummySource()))
                .build();
    }

    @AfterTest
    public void tearDown() {
        rCache.close();
        sCache.close();
    }

    @Test(threadPoolSize = 10, invocationCount = 100, timeOut = 600)
    public void testRadixCache() throws InterruptedException {
        Thread.sleep(200);
        assertEquals("Radix cache contains all matches", data.size(), copyIterator(rCache.closest("a")).size());
        assertEquals("Radix cache starting with all matches", data.size(), copyIterator(rCache.startingWith("a")).size());
    }

    @Test(threadPoolSize = 10, invocationCount = 100, timeOut = 600)
    public void testSuffixCache() throws InterruptedException {
        Thread.sleep(200);
        assertEquals("Suffix cache contains all matches", data.size(), copyIterator(sCache.containg("a")).size());
        assertEquals("Suffix cache ending with half matches", data.size() / 2, copyIterator(sCache.endingWith("b")).size());
    }
}
