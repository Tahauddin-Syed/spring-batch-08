package com.tahauddin.syed.springbatch08;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 *
 create table batch_logs (
 id integer,
 request longtext,
 exception_message longtext,
 skip_point varchar(100),
 created_date date,
 updated_date date,
 created_by varchar(100),
 updated_by varchar(100),
 primary key(id)
 );
 */
@SpringBootApplication
@EnableBatchProcessing()
public class SpringBatch08Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatch08Application.class, args);
	}

}
