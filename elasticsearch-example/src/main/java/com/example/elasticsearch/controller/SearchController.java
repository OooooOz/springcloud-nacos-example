package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

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

    // ------------------------------------------------match 条件查询------------------------------------------------//

    /**
     * 根据blogId获取文档内容
     *
     * @param blogId 实体类blogId
     * @return
     */
    @GetMapping("get/match/blogId/{blogId}")
    public Blog findByBlogId(@PathVariable("blogId") Long blogId) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("blogId", blogId))
                .build();
        SearchHit<Blog> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return searchHit.getContent();
    }

    /**
     * 单条件精确匹配
     *
     * @param title 实体类title
     * @return
     */
    @GetMapping("get/match/phrase")
    public Object findByMatchPhraseQuery(String title) {

        MatchPhraseQueryBuilder builder = null;
        if (StringUtils.isNotBlank(title)) {
            builder = QueryBuilders.matchPhraseQuery("title", title);
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return searchHits;
    }


    /**
     * 获取所有文档内容
     *
     * @return
     */
    @GetMapping("get/match/all")
    public Object findAll() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        SearchHits<Blog> blog = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return blog;
    }

    // ------------------------------------------------bool 组合条件查询------------------------------------------------//

    /**
     * bool组合条件查询-must：根据title和author条件获取文档内容 -- 也就是 and 有分词权重的
     *
     * @param title  实体类title
     * @param author 实体类author
     * @return
     */
    @GetMapping("get/match/bool/must")
    public Object findByMust(String title, String author) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", title));
        }

        if (StringUtils.isNotBlank(author)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("author", author));
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return searchHits;
    }

    /**
     * bool组合条件查询-should：相当于or
     *
     * @param authors 实体类author
     * @return
     */
    @GetMapping("get/match/bool/should")
    public Object findByShould(@RequestBody List<String> authors) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        authors.stream().filter(v -> StringUtils.isNotBlank(v))
                .forEach(author -> boolQueryBuilder.should(QueryBuilders.matchQuery("author", author)));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return searchHits;
    }

    /**
     * bool组合条件查询-filter：条件过滤
     *
     * @param authors 实体类author
     * @return
     */
    @GetMapping("get/match/bool/filter/{blogId}")
    public Object findByFilter(@RequestBody List<String> authors, @PathVariable("blogId") Long blogId) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        authors.stream().filter(v -> StringUtils.isNotBlank(v))
                .forEach(author -> boolQueryBuilder.should(QueryBuilders.matchQuery("author", author)));

//        // 单个条件 = blogId
//        boolQueryBuilder.filter(QueryBuilders.termQuery("blogId", blogId));
        // 范围条件: >=blogId
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("blogId").gte(blogId));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class, IndexCoordinates.of("blog"));
        return searchHits;
    }

    // -----------------------------------------------match 分页查询------------------------------------------------//

    /**
     * 分页查询
     *
     * @param page 页码 from = （page-1）* size
     * @param size 页大小
     * @return
     */
    @GetMapping("get/all/page")
    public Page<Blog> getAllPage(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(pageRequest)
                .build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class);


        List<Blog> blogs = new ArrayList<>();
        searchHits.stream().forEach(v -> blogs.add(v.getContent()));

        return new PageImpl<>(blogs, pageRequest, searchHits.getTotalHits());
    }

    // -----------------------------------------------排序------------------------------------------------//

    /**
     * 排序
     *
     * @param sort  排序字段
     * @param order 升降序
     * @return
     */
    @GetMapping("get/sort")
    public Object getAllPage(String sort, String order) {

        SortOrder sortOrder = StringUtils.equals(order, SortOrder.DESC.toString()) ? SortOrder.DESC : SortOrder.ASC;
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(SortBuilders.fieldSort(sort).order(sortOrder))
                .build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class);


        List<Blog> blogs = new ArrayList<>();
        searchHits.stream().forEach(v -> blogs.add(v.getContent()));

        return blogs;
    }

    // -----------------------------------------------结果字段过滤------------------------------------------------//

    /**
     * 结果字段过滤
     *
     * @param page 页码 from = （page-1）* size
     * @param size 页大小
     * @return
     */
    @GetMapping("get/filter")
    public Page<Blog> filterBlogIdAndContent(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        // Blog结果字段过滤仅显示"blogId", "content" ，其他字段为null
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes("blogId", "content").build();

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(pageRequest)
                .withSourceFilter(sourceFilter)
                .build();

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, Blog.class);


        List<Blog> blogs = new ArrayList<>();
        searchHits.stream().forEach(v -> blogs.add(v.getContent()));

        return new PageImpl<>(blogs, pageRequest, searchHits.getTotalHits());
    }
}
