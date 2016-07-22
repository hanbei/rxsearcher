package de.hanbei.rxsearch;

import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FlatMapTest {

    @Ignore
    @Test
    public void testFlatMapErrorHandling() throws IOException {
        Thread t = new Thread(() -> {
            ArrayList<Observable<?>> observables = Lists.newArrayList(Observable.interval(10, 10, TimeUnit.MILLISECONDS), Observable.error(new RuntimeException("")), Observable.interval(27, 10, TimeUnit.MILLISECONDS));
            Observable.from(observables).flatMap(observable -> observable.onErrorResumeNext(Observable.empty())).subscribe(System.out::println, System.err::println);
        });
        t.start();
        System.in.read();
    }
}
