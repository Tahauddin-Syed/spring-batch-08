package com.tahauddin.syed.springbatch08.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tahauddin.syed.springbatch08.domain.entity.BatchLogs;
import com.tahauddin.syed.springbatch08.domain.entity.Customer;
import com.tahauddin.syed.springbatch08.domain.repo.BatchLogsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class MyCustomSkipPolicy /*implements SkipListener<Customer, Customer>*/ {

    private final BatchLogsRepo batchLogsRepo;
    private final ObjectMapper objectMapper;


    //  @Override
    @OnSkipInRead
    public void onSkipInRead(Throwable t) {
        BatchLogs batchLogs = new BatchLogs();
        batchLogs.setException_message(t.getMessage());
        batchLogs.setSkip_point("SKIP_READ");
        batchLogs.setCreated_by("BATCH_ADMIN");
        batchLogs.setUpdated_by("BATCH_ADMIN");
        batchLogsRepo.save(batchLogs);
    }

    //   @Override
    @OnSkipInWrite
    public void onSkipInWrite(Customer item, Throwable t) {
        try {
            String customerJson = objectMapper.writeValueAsString(item);
            BatchLogs batchLogs = new BatchLogs();
            batchLogs.setException_message(t.getMessage());
            batchLogs.setRequest(customerJson);
            batchLogs.setSkip_point("SKIP_WRITE");
            batchLogs.setCreated_by("BATCH_ADMIN");
            batchLogs.setUpdated_by("BATCH_ADMIN");
            batchLogsRepo.save(batchLogs);
        } catch (Exception e) {
            log.info("Error while saving the info");
        }
    }

    //  @Override
    @OnSkipInProcess
    public void onSkipInProcess(Customer item, Throwable t) {
        try {
            String customerJson = objectMapper.writeValueAsString(item);
            BatchLogs batchLogs = new BatchLogs();
            batchLogs.setException_message(t.getMessage());
            batchLogs.setRequest(customerJson);
            batchLogs.setSkip_point("SKIP_PROCESS");
            batchLogs.setCreated_by("BATCH_ADMIN");
            batchLogs.setUpdated_by("BATCH_ADMIN");
            batchLogsRepo.save(batchLogs);
        } catch (Exception e) {
        }
    }
}
