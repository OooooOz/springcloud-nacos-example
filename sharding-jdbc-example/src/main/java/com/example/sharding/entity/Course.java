package com.example.sharding.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {
    private Long cId;
    private String cName;
    private Long userId;
    private String cStatus;
}
