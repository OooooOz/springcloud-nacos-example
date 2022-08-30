package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("document")
public class DocumentByBulkController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @PostMapping("add/list")
    public Object addDocuments(@RequestBody List<Blog> blogs) {

        return elasticsearchRestTemplate.save(blogs);
    }

    /**
     * 批量修改文档内容
     *
     * @param id    文档id
     * @param blogs
     */
    @PutMapping("update/part/list/{id}")
    public void editDocumentsPart(@PathVariable("id") String id, @RequestBody List<Blog> blogs) {
        List<UpdateQuery> updateQueryList = new ArrayList<>();
        blogs.stream().forEach(v -> {
            Document document = Document.create();
            document.put("content", v.getContent());
            UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(document).build();
            updateQueryList.add(updateQuery);
        });

        elasticsearchRestTemplate.bulkUpdate(updateQueryList, IndexCoordinates.of("blog"));
    }


    @DeleteMapping("delete/all")
    public void deleteDocumentAll() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        elasticsearchRestTemplate.delete(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
    }
}
