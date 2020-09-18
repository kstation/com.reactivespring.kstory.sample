package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

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

	@Test
	public void test_flux_fromArray(){
		List<String> list = new ArrayList<>();

		Flux<String> flux = Flux.fromArray(new String[]{"kstory", "kim", "yuna"}).log();
		flux.subscribe(list::add);

		Assertions.assertEquals(list, Arrays.asList("kstory", "kim", "yuna"));
	}

	@Test
	public void test_flux_fromIterable(){
		List<String> list = new ArrayList<>();

		Flux<String> flux = Flux.fromIterable(Arrays.asList("kstory", "kim", "yuna")).log();
		flux.subscribe(list::add);

		Assertions.assertEquals(list, Arrays.asList("kstory", "kim", "yuna"));
	}

	@Test
	public void test_flux_empty(){
		List<String> list = new ArrayList<>();

		Flux<String> flux = Flux.empty();
		flux.subscribe(list::add);

		Assertions.assertEquals(0, list.size());
	}

	@Test
	public void test_flux_lazy(){
		Flux<Integer> flux = Flux.range(1, 9)
								.flatMap(n ->{
									try{
										Thread.sleep(1000);
									}catch(InterruptedException ex){
										ex.printStackTrace();
									}

									return Mono.just(3 * n);
								}).log();

		//아직 구독 전
		System.out.println(" not subscribe");

		flux.subscribe(value -> {
			System.out.println(value);
		},
			null,
			() -> {
			System.out.println("Receive Complete");
		});

		System.out.println("All Complete");
	}

	@Test
	public void test_mono_just(){
		List<Signal<Integer>> signals = new ArrayList<>(4);

		final Integer[] result = new Integer[1];

		Mono<Integer> mono = Mono.just(1).log()
								.doOnEach(integerSignal ->
								{
									signals.add(integerSignal);
									System.out.println("Signal... : " + integerSignal);
								});

		mono.subscribe(integer -> result[0] = integer);

		mono.subscribe( s -> {
			System.out.println("mono consumer: " + s);
		});

		for(Signal<Integer> a: signals){
			System.out.print("signals is =>");
			System.out.println(a);
		}
	}

	@Test
	public void test_mono_null(){
		Mono<String> result = Mono.empty();

		Assertions.assertNull(result.block());
	}

	@Test
	public void test_flux_subscribe(){
		List<String> names = new ArrayList<>();

		Flux<String> flux = Flux.just("kim", "lee").log();
		flux.subscribe(names::add);

		Assertions.assertEquals(names, Arrays.asList("kim", "lee"));
	}

	@Test
	public void test_flux_subscribe_debugging(){
		Flux<Integer> flux = Flux.fromArray(new Integer[]{1,5,7}).log();

		flux.subscribe(System.out::println);
	}

	@Test
	public void test_flux_subscribe_complete() {

		List<String> names = new ArrayList<>();

		List<Signal<String>> signals = new ArrayList<>(10);

		Flux<String> flux = Flux.just("에디킴", "아이린", "아이유", "수지")
				.log()
				.doOnEach(signals::add);

		flux.subscribe(names::add,
				error -> {
				},
				() -> {
					Assertions.assertEquals(names, Arrays.asList("에디킴", "아이린", "아이유", "수지"));
					Assertions.assertEquals(signals.size(), 5);
					Assertions.assertFalse(signals.get(3).isOnComplete());
					Assertions.assertTrue(signals.get(3).isOnNext());
					Assertions.assertTrue(signals.get(4).isOnComplete());
				});

	}

	//custom subscriber는 비 추천
	@Test
	public void test_flux_custom_subscriber() {

		List<Integer> integerList = new ArrayList<>();

		Flux.just(1, 2, 3, 4)
				.log()
				.subscribe(new Subscriber<Integer>() {
					@Override
					public void onSubscribe(Subscription s) {
						s.request(Long.MAX_VALUE);
					}
					@Override
					public void onNext(Integer integer) {
						integerList.add(integer);
					}
					@Override
					public void onError(Throwable t) {

					}
					@Override
					public void onComplete() {
						//TODO: request(1) 인 경우, Complete 가 되지 않은 상황에서 테스트가 통과하는것은 잘못된 테스트인듯.. 검토해보자.
						//TODO: 결국 Reactor 에서 제공하는 StepVerifier 와 같은 Test 코드를 사용하는게 좋을 듯
						Assertions.assertEquals(integerList.size(), 4);
					}
				});

	}
}
