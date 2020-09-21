package com.reactivespring.kstory.sample.com.reactivespring.kstory.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final CoffeeService coffeeService;

    public ApiController(CoffeeService coffeeService) {
        this.coffeeService = coffeeService;
    }

    @GetMapping("/coffees")
    Flux<Coffee> getCoffees() {
        return coffeeService.findAll();
    }
}
