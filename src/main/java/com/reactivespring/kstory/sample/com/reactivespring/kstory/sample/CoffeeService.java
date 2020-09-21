package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import reactor.core.publisher.Flux;

public interface CoffeeService {
    Flux<Coffee> findAll();
}
