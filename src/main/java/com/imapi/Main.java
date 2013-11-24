package com.imapi;


import com.imapi.cache.CacheBuilder;
import com.imapi.cache.SuffixCache;
import com.imapi.cache.invalidator.DefaultInvalidator;
import com.imapi.cache.source.RandomSource;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {

        final SuffixCache<String> cache = new CacheBuilder.SuffixBuilder<String>()
                .invalidator(new DefaultInvalidator<String>(10))
                .sources(Collections.singletonList(new RandomSource()))
                .build();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (CharSequence suggestion : cache.containg("de")) {
                    System.out.println(suggestion);
                }
                cache.close();
                timer.cancel();
                System.out.println();
            }
        }, 500, 40);

    }
}
