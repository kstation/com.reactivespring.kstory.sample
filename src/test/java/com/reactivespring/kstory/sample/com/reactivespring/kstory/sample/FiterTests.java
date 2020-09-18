package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FiterTests {

    @Test
    public void flux_filter_not_equals(){

        List<String> colors = new ArrayList<>();

        Flux<String> flux = Flux.just("blue", "green", "orange", "purple").log();

        //blue 는 전달하지 않도록 filter 추가
        flux.filter(color -> !color.equals("blue"))
                .subscribe(colors::add);

        Assertions.assertEquals(3, colors.size());
        Assertions.assertTrue(colors.contains("green"));
        Assertions.assertFalse(colors.contains("blue"));
    }

    @Test
    public void flux_filter_equals(){

        List<String> colors = new ArrayList<>();

        Flux<String> flux = Flux.just("blue", "green", "orange", "purple").log();

        //orange 전달
        flux.filter(color -> color.equals("orange"))
                .subscribe(colors::add);

        Assertions.assertEquals(1, colors.size());
        Assertions.assertTrue(colors.contains("orange"));
    }

    @Test
    public void flux_take(){

        List<String> colors = new ArrayList<>();

        Flux<String> flux = Flux.just("blue", "green", "orange", "purple").log();

        /*
        flux.take(2)
                .subscribe(colors::add);
        */

        flux.take(2)
                .subscribe(colors::add,
                        null,
                        () -> System.out.println("onComplete 는 실행되는가? 되는데.."));


        Assertions.assertEquals(2, colors.size());
        Assertions.assertTrue(colors.contains("green"));
        Assertions.assertFalse(colors.contains("orange"));

    }

    @Test
    public void flux_skip(){

        List<String> colors = new ArrayList<>();

        Flux<String> flux = Flux.just("blue","green","orange","purple")
                .log();

        flux.skip(3)
                .subscribe(colors::add);

        Assertions.assertEquals(1, colors.size());

        Assertions.assertTrue(colors.contains("purple"));
        Assertions.assertFalse(colors.contains("blue"));

    }

    @Test
    public void flux_repeat(){

        List<String> names = new ArrayList<>();

        Flux<String> nameFlux = Flux.just("에디킴", "아이린").doOnEach(signal -> {
            //TODO: signal check
        });
        nameFlux.repeat(2).subscribe(names::add);

        Assertions.assertEquals(6,names.size());


    }

}
