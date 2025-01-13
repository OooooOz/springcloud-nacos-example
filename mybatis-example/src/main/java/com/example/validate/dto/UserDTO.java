package com.example.validate.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UserDTO {

    @NotNull
    private Long userId;

    @Length(min = 2, max = 10)
    private String userName;

    @Valid
    private Job job;

    @Valid
    @NotEmpty
    private List<Bank> banks;

    @Data
    public static class Job {

        @NotNull
        private Long jobId;

        @Length(min = 2, max = 10)
        private String jobName;

    }

    @Data
    public static class Bank {

        @Length(min = 2, max = 10)
        private String bankName;

        @NotNull
        @Length(min = 2, max = 10)
        private String position;
    }
}
