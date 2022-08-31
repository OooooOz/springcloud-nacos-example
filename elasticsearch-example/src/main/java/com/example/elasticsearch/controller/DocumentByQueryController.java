package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("document")
public class DocumentByQueryController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

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

    @PostMapping("update/{index}")
    public Object updateDocumentAll(@PathVariable("index") String index, @RequestBody Blog blog) throws IOException {

        UpdateByQueryRequest request = new UpdateByQueryRequest(index);
        request.setQuery(new TermQueryBuilder("blogId", blog.getBlogId()));
        request.setScript(new Script("ctx._source['content']=" + blog.getContent() + ";"));
        BulkByScrollResponse updateByQuery = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);

//        这种写法必须要有文档id
//        Document document = Document.create();
//        document.put("content", blog.getContent());
//        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(document).build();
//        UpdateResponse response = elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("blog"));
        return updateByQuery;
    }
}
