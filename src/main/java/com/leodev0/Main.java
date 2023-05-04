package com.leodev0;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.Number;
import com.leodev0.customer.Customer;
import com.leodev0.customer.CustomerRepository;
import com.leodev0.customer.enums.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

//        ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class, args);
//        printBeans(applicationContext);
    }

    private static void printBeans(ConfigurableApplicationContext ctx) {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();

        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

//    @Bean
//    CommandLineRunner runner(CustomerRepository customerRepository) {
//        return args -> {
//            Faker faker = new Faker();
//            ArrayList<Customer> customers = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                String name = faker.name().fullName();
//                String[] nameWords = name.split(" ");
//                String firstName = nameWords[0].toLowerCase();
//                String lastName = nameWords[nameWords.length - 1].toLowerCase();
//                String email = firstName + "." + lastName + "@" + faker.internet().domainName();
//                int age = faker.number().numberBetween(18, 70);
//                Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
//                Customer customer = new Customer(
//                        name,
//                        email,
//                        age,
//                        gender
//                );
//                customers.add(customer);
//            }
//
//            customerRepository.saveAll(customers);
//        };
//    }
}
