package com.tahauddin.syed.springbatch08.domain.repo;

import com.tahauddin.syed.springbatch08.domain.entity.BatchLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchLogsRepo extends JpaRepository<BatchLogs, Long> {
}
