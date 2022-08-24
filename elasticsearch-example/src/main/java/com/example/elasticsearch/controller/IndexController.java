package com.example.elasticsearch.controller;

import cn.hutool.json.JSONUtil;
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

    @PutMapping("/create")
    public String createIndex() {
        // 创建索引，会根据Blog类的@Document注解信息来创建
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        indexOperations.create();

        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
//        indexOperations.putMapping(indexOperations.createMapping(Blog.class));
        return "success";
    }

    @GetMapping("/get")
    public String getIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        return JSONUtil.toJsonStr(indexOperations.getSettings());
    }

    @DeleteMapping("/delete")
    public Object deleteIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Blog.class);
        indexOperations.delete();
        return indexOperations.delete();
    }
}
