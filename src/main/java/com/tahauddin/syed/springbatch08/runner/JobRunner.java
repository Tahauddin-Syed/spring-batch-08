package com.tahauddin.syed.springbatch08.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job job;


    @Override
    public void run(String... args) throws Exception {
        jobLauncher.run(job, new JobParametersBuilder().addDate("TodaysDate", new Date()).toJobParameters());
    }
}
