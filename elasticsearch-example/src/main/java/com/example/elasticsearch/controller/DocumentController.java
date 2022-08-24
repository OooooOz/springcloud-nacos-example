package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("document")
public class DocumentController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 添加文档，重复请求会全量更新文档
     *
     * @return
     */
    @PostMapping("add")
    public Blog addDocument() {
        Long id = 1L;
        Blog blog = new Blog();
        blog.setBlogId(id);
        blog.setTitle("Spring Data ElasticSearch学习教程" + id);
        blog.setContent("这是添加单个文档的实例" + id);
        blog.setAuthor("Tony");
        blog.setCategory("ElasticSearch");
        blog.setCreateTime(new Date());
        blog.setStatus(1);
        blog.setSerialNum(id.toString());

        return elasticsearchRestTemplate.save(blog);
    }

    @PostMapping("add/list")
    public Object addDocuments(Integer count) {
        List<Blog> blogs = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Long id = (long) i;
            Blog blog = new Blog();
            blog.setBlogId(id);
            blog.setTitle("Spring Data ElasticSearch学习教程" + id);
            blog.setContent("这是添加单个文档的实例" + id);
            blog.setAuthor("Tony");
            blog.setCategory("ElasticSearch");
            blog.setCreateTime(new Date());
            blog.setStatus(1);
            blog.setSerialNum(id.toString());
            blogs.add(blog);
        }

        return elasticsearchRestTemplate.save(blogs);
    }

    /**
     * 跟新增是同一个方法。若id已存在，则修改。
     * 无法只修改某个字段，只能覆盖所有字段。若某个字段没有值，则会写入null。
     *
     * @return 成功写入的数据
     */
    @PutMapping("update/all")
    public Blog editDocument(@RequestBody Blog blog) {

        return elasticsearchRestTemplate.save(blog);
    }

    /**
     * 局部修改文档内容
     *
     * @param id   文档id
     * @param blog
     * @return
     */
    @PutMapping("update/part/{id}")
    public UpdateResponse editDocumentPart(@PathVariable("id") String id, @RequestBody Blog blog) {
        Document document = Document.create();
        document.put("content", blog.getContent());

        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(document).build();

        UpdateResponse response = elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("blog"));

        return response;
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


    /**
     * http://localhost:9200/blog/_doc/100
     *
     * @param id 这里的id是指文档id，并不是blog中@id的属性值
     * @return
     * @throws UnsupportedTemporalTypeException 如果Unsupported field: InstantSeconds异常，确认springboot版本2.3.5+
     */
    @GetMapping("get/id/{id}")
    public Blog findById(@PathVariable("id") Long id) {
        // 从索引库blog中获取指定文档id的数据，封装回Blog.class
        return elasticsearchRestTemplate.get(id.toString(), Blog.class, IndexCoordinates.of("blog"));
    }

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

    @PostMapping("deleteDocumentById")
    public String deleteDocumentById(Long id) {
        return elasticsearchRestTemplate.delete(id.toString(), Blog.class);
    }

    @PostMapping("deleteDocumentByQuery")
    public void deleteDocumentByQuery(String title) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", title))
                .build();

        elasticsearchRestTemplate.delete(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
    }

    @PostMapping("deleteDocumentAll")
    public void deleteDocumentAll() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        elasticsearchRestTemplate.delete(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
    }
}
