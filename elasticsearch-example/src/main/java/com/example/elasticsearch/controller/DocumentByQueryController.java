package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("document")
public class DocumentByQueryController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 根据blogId获取文档内容
     *
     * @param blogId 实体类blogId
     * @return
     */
    @GetMapping("get/blogId/{blogId}")
    public Blog findByBlogId(@PathVariable("blogId") Long blogId) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("blogId", blogId))
                .build();
        SearchHit<Blog> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return searchHit.getContent();
    }

    @DeleteMapping("delete/blogId/{blogId}")
    public void deleteDocumentByQuery(@PathVariable("blogId") String blogId) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("blogId", blogId)).build();

        elasticsearchRestTemplate.delete(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
    }

    @DeleteMapping("delete/all")
    public void deleteDocumentAll() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        elasticsearchRestTemplate.delete(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
    }
}
