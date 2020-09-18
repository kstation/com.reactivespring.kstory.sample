package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class GenerateTests {

    @Test
    public void generate_test(){

        /*
            Project Reactor 공식 레퍼런스
            https://projectreactor.io/docs/core/release/reference/#producing.generate
         */
        Flux<String> flux = Flux.generate(
                () -> 1, //1.초기 상태를 1 으로
                (state, sink) -> {

                    //2.We use the state to choose what to emit
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 9) {
                        sink.complete(); //3.We also use it to choose when to stop.
                    }
                    //4.We return a new state that we use in the next invocation (unless the sequence terminated in this one).
                    return state + 1;
                });

        flux.subscribe(System.out::println,
                error -> System.out.println("에러"),
                () -> System.out.println("완료"));
    }

    @Test
    public void generate_test02() {

        List<Integer> list = new ArrayList<>();

        Flux.<Integer, Integer>generate(
                () -> 1,
                (state, sink) -> {
                    if (state < 11) {
                        sink.next(state);
                    }
                    else {
                        sink.complete();
                    }
                    return state + 1;
                })
                .subscribe(list::add);

        //expected: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        //TODO: assertArray....
        Assertions.assertTrue(list.equals(expected));
    }


    @Test
    public void generate_test03() {

        List<Integer> list = new ArrayList<>();

        Flux.<Integer, Integer>generate(
                () -> 1,
                (state, sink) -> {
                    if (state < 11) {
                        sink.next(state);
                    }
                    else {
                        sink.complete();
                    }
                    return state + 1;
                })
                .subscribe(list::add);

        //expected: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        //TODO: assertArray....
        Assertions.assertTrue(list.equals(expected));
    }

    @Test
    public void generate_iterableSource() {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Flux.<Integer, Iterator<Integer>>generate(list::iterator, (state, sink) -> {
            if (state.hasNext()) {
                sink.next(state.next());
            }
            else {
                sink.complete();
            }
            return state;
        }).subscribe();

        //TODO: 테스트
    }


    @Test
    public void flux_generate_delay(){
        Flux.generate(sink -> {
            sink.next(System.currentTimeMillis());
            try {
                System.out.println("Thread ID : " + Thread.currentThread().getName());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        })
                .subscribe(System.out::println);
    }

    @Test
    public void flux_generate_delay2(){

        Flux.generate(
                () -> new AtomicInteger(0),
                (counter, sink) -> {
                    if(counter.get() == 10) {
                        sink.complete();
                    }
                    sink.next(System.currentTimeMillis());
                    counter.incrementAndGet();
                    try {
                        System.out.println("Thread ID : " + Thread.currentThread().getName());
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return counter;
                }
        )
                .subscribe(System.out::println);
    }
}
