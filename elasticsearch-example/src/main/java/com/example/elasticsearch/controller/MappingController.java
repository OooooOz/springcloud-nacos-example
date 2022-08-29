package com.example.elasticsearch.controller;

import com.example.elasticsearch.entry.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mapping")
public class MappingController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @GetMapping("/get")
    public Object getIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        return indexOperations.getMapping();
    }

    @PostMapping("/extend")
    public Object extendIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        indexOperations.putMapping(indexOperations.createMapping(Blog.class));
        return indexOperations.getMapping();
    }
}
