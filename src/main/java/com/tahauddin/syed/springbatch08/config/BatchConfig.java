package com.tahauddin.syed.springbatch08.config;

import com.tahauddin.syed.springbatch08.domain.entity.Customer;
import com.tahauddin.syed.springbatch08.domain.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
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
    private final String CUSTOMER_UPDATE_QUERY = "UPDATE customer_table SET ADDRESS = :ADDRESS WHERE EMAIL = :EMAIL";

    @Bean
    public JobBuilder jobBuilder(){
        JobBuilder jobBuilder = new JobBuilder("JobOne");
        jobBuilder.repository(jobRepository);
        return jobBuilder;
    }

    @Bean
    public StepBuilder stepBuilder(){
        StepBuilder stepOne = new StepBuilder("StepOne");
        stepOne.repository(jobRepository);
        return stepOne;
    }

    @Bean
    public Job job(){
        return jobBuilder()
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilder()
                .<Customer, Customer>chunk(100)
                .reader(itemReader())
                .processor(itemProcessor())
        //        .writer(itemWriter())
                .writer(batchItemWriter())
                .transactionManager(platformTransactionManager)
                .taskExecutor(taskExecutor())
                .build();
    }




    @Bean
    public RepositoryItemReader<Customer> itemReader(){
        RepositoryItemReader<Customer> repositoryItemReader = new RepositoryItemReader<>();
        repositoryItemReader.setRepository(customerRepository);
        repositoryItemReader.setMethodName("findByEmail");
        List<Object> strings = new ArrayList<>();
        strings.add("Tahauddin.Syed@gmail.com");
        repositoryItemReader.setArguments(strings);
        repositoryItemReader.setPageSize(500);
        Map<String, Direction> map = new HashMap<>();
        map.put("id", Direction.ASC);
        repositoryItemReader.setSort(map);
        return repositoryItemReader;
    }

    public ItemProcessor<Customer, Customer> itemProcessor(){
        return item -> {
            item.setCreatedDate(Timestamp.from(Instant.now()));
            log.info("Current Thread Group Name :: {}", Thread.currentThread().getThreadGroup().getName());
            log.info("Current Thread Name :: {}", Thread.currentThread().getName());
            log.info("Current Thread Id :: {}", Thread.currentThread().getId());
            log.info("Current Thread State :: {}", Thread.currentThread().getState());
            return item;
        };
    }


    @Bean
    public RepositoryItemWriter<Customer> itemWriter(){
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
    public JdbcBatchItemWriter<Customer> batchItemWriter(){

        JdbcBatchItemWriter<Customer> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setSql(CUSTOMER_UPDATE_QUERY);
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setItemPreparedStatementSetter((item, ps) -> {
            ps.setString(1,item.getAddress());
            ps.setString(2, item.getEmail());
        });
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(item -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ADDRESS",  "Hyderabad");
            map.put("EMAIL",  "Tahauddin.Syed@gmail.com");
            return new MapSqlParameterSource(map);
        });
        return jdbcBatchItemWriter;
    }



}
