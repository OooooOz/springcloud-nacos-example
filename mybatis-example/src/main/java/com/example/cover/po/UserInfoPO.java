package com.example.cover.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoPO {

    private Long userId;

    private String userName;

    private String age;

    private String address;

    private JobPO job;
}