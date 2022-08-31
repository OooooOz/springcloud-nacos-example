package com.example.elasticsearch.controller;

import com.example.elasticsearch.entry.Blog;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CloseIndexResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 本处只是展示索引的操作方法，项目中不会这样创建索引，而是手写DSL去创建。
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

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

    @PostMapping("/close/{index}")
    public Object closeIndex(@PathVariable("index") String index) throws IOException {
        CloseIndexRequest closeIndexRequest = new CloseIndexRequest(index);
        CloseIndexResponse closeIndexResponse = restHighLevelClient.indices().close(closeIndexRequest, RequestOptions.DEFAULT);
        return closeIndexResponse;
    }

    @PostMapping("/open/{index}")
    public Object openIndex(@PathVariable("index") String index) throws IOException {
        OpenIndexRequest openIndexRequest = new OpenIndexRequest(index);
        OpenIndexResponse openIndexResponse = restHighLevelClient.indices().open(openIndexRequest, RequestOptions.DEFAULT);
        return openIndexResponse;
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

    @PostMapping("/reindex")
    public Object reIndex(String sourceIndex, String destIndex) throws IOException {
        ReindexRequest request = new ReindexRequest();
        request.setSourceIndices(sourceIndex);
        request.setDestIndex(destIndex);
        request.setSourceBatchSize(5000);
        request.setDestOpType("create");
        request.setConflicts("proceed");
        BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.reindex(request, RequestOptions.DEFAULT);

        return bulkByScrollResponse;
    }

}
