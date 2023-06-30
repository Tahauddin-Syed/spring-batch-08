package com.tahauddin.syed.springbatch08.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.tahauddin.syed.springbatch08.domain.entity.Customer;
import com.tahauddin.syed.springbatch08.domain.repo.BatchLogsRepo;
import com.tahauddin.syed.springbatch08.domain.repo.CustomerRepository;
import com.tahauddin.syed.springbatch08.exceptions.MyOwnException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {


    private final CustomerRepository customerRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final BatchLogsRepo batchLogsRepo;
    private final ObjectMapper objectMapper;
    private final String CUSTOMER_UPDATE_QUERY = "UPDATE customer_table SET ADDRESS = :ADDRESS WHERE EMAIL = :EMAIL";
    private final String CUSTOMER_UPDATE_QUERY_HASH = "UPDATE customer_table SET Hash_Value = :Hash_Value WHERE EMAIL = :EMAIL";
    private final String CUSTOMER_NEW_INSERT_QUERY_HASH = "INSERT INTO customer_table_new " +
            "(" +
            "first_name, " +
            "last_name, " +
            "pin_code, " +
            "address, " +
            "created_date, " +
            "updated_date, " +
            "Hash_Value) " +
            "values (" +
            ":first_name," +
            ":last_name," +
            ":pin_code," +
            ":address," +
            ":created_date," +
            ":updated_date," +
            ":Hash_Value" +
            ")";

    @Bean
    public JobBuilder jobBuilder() {
        JobBuilder jobBuilder = new JobBuilder("JobOne");
        jobBuilder.repository(jobRepository);
        return jobBuilder;
    }

    @Bean
    public StepBuilder stepBuilder() {
        StepBuilder stepOne = new StepBuilder("StepOne");
        stepOne.repository(jobRepository);
        return stepOne;
    }

    @Bean
    public Job job() {
        return jobBuilder()
                .incrementer(new RunIdIncrementer())
                .start(step())
        //        .listener(myCustomSkipPolicy())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilder()
                .<Customer, Customer>chunk(500)
                .reader(itemReader())
                .processor(itemProcessor())
                //        .writer(itemWriter())
                .writer(batchItemWriterTwo())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
         //       .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(myCustomSkipPolicy())
                .transactionManager(platformTransactionManager)
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public RepositoryItemReader<Customer> itemReader() {
        RepositoryItemReader<Customer> repositoryItemReader = new RepositoryItemReader<>();
        repositoryItemReader.setRepository(customerRepository);
        repositoryItemReader.setMethodName("findAll");
        List<Object> strings = new ArrayList<>();
        //    strings.add("Tahauddin.Syed@gmail.com");
        repositoryItemReader.setArguments(strings);
        repositoryItemReader.setPageSize(500);
        Map<String, Direction> map = new HashMap<>();
        map.put("id", Direction.ASC);
        repositoryItemReader.setSort(map);
        return repositoryItemReader;
    }

    public ItemProcessor<Customer, Customer> itemProcessor() {

        return item -> {
            item.setCreatedDate(Timestamp.from(Instant.now()));
            item.setUpdatedDate(Timestamp.from(Instant.now()));
           String hashValue = Hashing.sha256().hashString(item.getFirstName(), StandardCharsets.UTF_8).toString();
    //        String hashValue = "Some Hash Value..";
            item.setHashValue(hashValue);
            log.info(" -- In Processor -- Hash Value :: {} for Id :: {}", hashValue,item.getId());
            log.info(" -- In Processor -- Current Thread Group Name :: {}", Thread.currentThread().getThreadGroup().getName());
            log.info(" -- In Processor -- Current Thread Name :: {}", Thread.currentThread().getName());
            return item;
        };
    }


    @Bean
    public RepositoryItemWriter<Customer> itemWriter() {
        RepositoryItemWriter<Customer> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(customerRepository);
        repositoryItemWriter.setMethodName("save");
        return repositoryItemWriter;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("My Own Thread - ");
        threadPoolTaskExecutor.setQueueCapacity(10);
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        return threadPoolTaskExecutor;
    }


    @Bean
    public JdbcBatchItemWriter<Customer> batchItemWriter() {
        log.info(" -- In Writer -- Current Thread Group Name :: {}", Thread.currentThread().getThreadGroup().getName());
        log.info(" -- In Writer -- Current Thread Name :: {}", Thread.currentThread().getName());
        JdbcBatchItemWriter<Customer> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        //   jdbcBatchItemWriter.setSql(CUSTOMER_UPDATE_QUERY);
        jdbcBatchItemWriter.setSql(CUSTOMER_UPDATE_QUERY_HASH);
        jdbcBatchItemWriter.setDataSource(dataSource);

        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(item -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Hash_Value", item.getHashValue());
            return new MapSqlParameterSource(map);
        });

        return jdbcBatchItemWriter;
    }

    @Bean
    public ItemWriter<Customer> batchItemWriterOne() {

        JdbcBatchItemWriter<Customer> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        //   jdbcBatchItemWriter.setSql(CUSTOMER_UPDATE_QUERY);
        jdbcBatchItemWriter.setSql(CUSTOMER_UPDATE_QUERY_HASH);
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setItemPreparedStatementSetter((item, ps) -> {
            ps.setString(1, item.getAddress());
           ps.setString(2, item.getEmail());
        });

        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(item -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Hash_Value", item.getHashValue());
            map.put("EMAIL", item.getEmail());
            return new MapSqlParameterSource(map);
        });
        return jdbcBatchItemWriter;
    }

    @Bean
    public ItemWriter<Customer> batchItemWriterTwo() {

        JdbcBatchItemWriter<Customer> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setSql(CUSTOMER_NEW_INSERT_QUERY_HASH);
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(item -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("first_name", item.getFirstName());
            map.put("last_name", item.getLastName());
            map.put("pin_code", item.getPinCode());
            map.put("address", item.getAddress());
            map.put("created_date", item.getCreatedDate());
            map.put("updated_date", item.getUpdatedDate());
            map.put("Hash_Value", item.getHashValue());
            return new MapSqlParameterSource(map);
        });

        return jdbcBatchItemWriter;
    }


    @Bean
    public MyCustomSkipPolicy myCustomSkipPolicy(){
        return new MyCustomSkipPolicy(batchLogsRepo, objectMapper);
    }

}
