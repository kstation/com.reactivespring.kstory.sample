package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class ApplicationTests {

	@Test
	public void test_flux_just_consumer(){
		List<String> names =  new ArrayList<>();
		Flux<String> flux = Flux.just("kstory", "kim").log();

		flux.subscribe( s -> {
			System.out.println("sequence receive : " + s);
			names.add(s);
		});
		// 위 구문과 동일한 람다
		// flux.subscribe(names::add);

		Assertions.assertEquals(names, Arrays.asList("kstory", "kim"));
	}

	@Test
	public void test_flux_range(){
		List<Integer> list = new ArrayList<>();
		Flux<Integer> flux = Flux.range(1, 5).log();

		flux.subscribe(list::add);

		Assertions.assertEquals(5, list.size());
		Assertions.assertEquals(1, (int)list.get(0));
		Assertions.assertNotEquals(5, (int)list.get(0));
	}
}
