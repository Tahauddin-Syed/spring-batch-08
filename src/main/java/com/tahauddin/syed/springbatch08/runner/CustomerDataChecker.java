package com.tahauddin.syed.springbatch08.runner;

import com.tahauddin.syed.springbatch08.domain.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerDataChecker implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        customerRepository.findAll().forEach(l -> log.info("Customer Info is :: {}", l));
    }
}
