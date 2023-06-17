package com.tahauddin.syed.springbatch08.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDTO implements Serializable {

    private static final long serialVersionUID = -4133540112260767306L;
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String gender;

    private String ipAddress;
}
