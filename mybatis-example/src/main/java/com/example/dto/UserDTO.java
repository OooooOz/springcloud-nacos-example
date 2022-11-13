package com.example.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserDTO {

    @NotNull
    private Long userId;

    @Length(min = 2, max = 10)
    private String userName;

    @Valid
//    @NotNull
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