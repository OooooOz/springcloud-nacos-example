package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.UnsupportedTemporalTypeException;

@RestController
@RequestMapping("document")
public class DocumentByIdController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 添加文档，重复请求会全量更新文档
     *
     * @return
     */
    @PostMapping("add")
    public Blog addDocument(@RequestBody Blog blog) {
        return elasticsearchRestTemplate.save(blog);
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
     * @param id 文档id
     * @return
     */
    @DeleteMapping("delete/id/{id}")
    public String deleteDocumentById(@PathVariable("id") Long id) {
        return elasticsearchRestTemplate.delete(id.toString(), Blog.class);
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
}
