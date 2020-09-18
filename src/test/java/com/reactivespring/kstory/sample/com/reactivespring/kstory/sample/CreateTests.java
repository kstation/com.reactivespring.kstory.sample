package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class CreateTests {
    @Test
    public void test(){

        Flux<Integer> source = Flux.<Signal<Integer>>create(e -> {
            e.next(Signal.next(1));
            e.next(Signal.next(2));
            e.next(Signal.next(3));
            e.next(Signal.complete());
            System.out.println(e.isCancelled());
            System.out.println(e.requestedFromDownstream());
        }).dematerialize();

        List<Integer> list = new ArrayList<>();
        source.subscribe(list::add);

        List<Integer> expected = Arrays.asList(1, 2, 3);

        Assertions.assertEquals(list.size(), 3);
        Assertions.assertTrue(list.equals(expected));
    }

    @Test
    public void fluxCreateBuffered(){

        AtomicInteger onDispose = new AtomicInteger();
        AtomicInteger onCancel = new AtomicInteger();
        Flux<String> created = Flux.create(s -> {
            s.onDispose(onDispose::getAndIncrement)
                    .onCancel(onCancel::getAndIncrement);
            s.next("test1");
            s.next("test2");
            s.next("test3");
            s.complete();
        });

        created.subscribe();

        Assertions.assertEquals(onDispose.get(), 1);
        Assertions.assertEquals(onCancel.get(), 0);
    }

    @Test
    public void flux_error(){
        Flux<String> created = Flux.create(s -> {
            s.error(new Exception("test"));
        });

        created.subscribe();
    }
}
