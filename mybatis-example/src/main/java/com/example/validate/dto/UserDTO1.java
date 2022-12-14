package com.example.validate.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class UserDTO1 {

    @NotNull(groups = {Update.class})
    private Long userId;

    @Length(min = 2, max = 10, groups = {Save.class})
    private String userName;

    @Valid
    private Job job;

    @Data
    public static class Job {

        @NotNull(groups = {Update.class})
        private Long jobId;

        @Length(min = 2, max = 10, groups = {Save.class})
        private String jobName;

    }

    /**
     * 保存的时候校验分组
     */
    public interface Save {
    }

    /**
     * 更新的时候校验分组
     */
    public interface Update {
    }
}
