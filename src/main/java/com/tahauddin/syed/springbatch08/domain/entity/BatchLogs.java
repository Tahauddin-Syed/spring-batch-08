package com.tahauddin.syed.springbatch08.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.NonLeaked;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchLogs {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private String request;

    @Lob
    private String exception_message;
    private String skip_point;

    @CreationTimestamp
    private Timestamp created_date;

    @UpdateTimestamp
    private Timestamp updated_date;
    private String created_by;
    private String updated_by;



}
