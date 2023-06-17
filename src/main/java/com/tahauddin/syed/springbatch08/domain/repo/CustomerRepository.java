package com.tahauddin.syed.springbatch08.domain.repo;

import com.tahauddin.syed.springbatch08.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Page<Customer> findByEmail(String email, Pageable pageable);


}
