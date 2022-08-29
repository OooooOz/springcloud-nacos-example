package com.example.elasticsearch.controller;

import com.example.elasticsearch.entry.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.*;

/**
 * 本处只是展示索引的操作方法，项目中不会这样创建索引，而是手写DSL去创建。
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 指定mapping创建索引
     *
     * @return
     */
    @PutMapping("/create")
    public String createIndex() {
        // 创建索引，会根据Blog类的@Document注解信息来创建
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        indexOperations.create();
        // 手动映射
        indexOperations.putMapping(indexOperations.createMapping(Blog.class));
        return "success";
    }

    @DeleteMapping("/delete")
    public Object deleteIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        indexOperations.delete();
        return indexOperations.delete();
    }

    @PostMapping("/close")
    public Object closeIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        indexOperations.getSettings(true).put("verified_before_close", true);
        return "success";
    }

    @GetMapping("/exists")
    public Object existsIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        return indexOperations.exists();
    }


    @GetMapping("/get")
    public Object getIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        return indexOperations;
    }
}
