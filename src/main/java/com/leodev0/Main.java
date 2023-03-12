package com.leodev0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/greet")
    public GreepResponse greet(@RequestParam(value = "name", required = false) String name) {
        String greetMessage = name == null || name.isBlank() ? "Hello" : "Hello " + name;

        return new GreepResponse(
                greetMessage,
                List.of("Java", "Python", "Typescript"),
                new Person("Leo", 29, 40_000)
        );
    }

    record GreepResponse(
            String greet,
            List<String> favProgrammingLanguages,
            Person person
    ) {}

    record Person(String name, int age, double savings) {}
}
