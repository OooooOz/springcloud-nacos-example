package com.example.elasticsearch.dto;

import lombok.Data;

@Data
public class PageDto {

    private Integer page;
    private Integer size;
    private String sort;
    private String order;
    private String[] includes;
    private String[] excludes;
}
