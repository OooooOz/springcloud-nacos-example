package com.example.elasticsearch.controller;

import com.example.elasticsearch.entry.BlogMatch;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

/**
 * 本处只是展示索引的操作方法，项目中不会这样创建索引，而是手写DSL去创建。
 */
@RestController
@RequestMapping("/match")
public class SearchBlogMatchController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 指定mapping创建索引
     *
     * @return
     */
    @PutMapping("/index/create")
    public String createIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(BlogMatch.class);
        indexOperations.create();
        // 手动映射
        indexOperations.putMapping(indexOperations.createMapping(BlogMatch.class));
        return "success";
    }

    @PostMapping("/document/add")
    public Object addDocument(@RequestBody BlogMatch blog) {
        return elasticsearchRestTemplate.save(blog);
    }

    @GetMapping("/mapping/get")
    public Object getMapping() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(BlogMatch.class);
        return indexOperations.getMapping();
    }


    @GetMapping("get/title")
    public Object findByString(String title, String shouldMatch, String operator) {
        Operator operator1 = StringUtils.equals(operator, "and") ? Operator.AND : Operator.OR;
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("title", title)
                .minimumShouldMatch(shouldMatch)
//                .analyzer("ik_max_word")
                .operator(operator1);
        System.out.println(queryBuilder.analyzer());

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        // 查询一个结果，如果有多个取第一个
//        SearchHit<BlogMatch> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }

    @GetMapping("get/content")
    public Object findByContent(String content, String shouldMatch, String operator, String analyzer) {
        Operator operator1 = StringUtils.equals(operator, "and") ? Operator.AND : Operator.OR;
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("content", content)
                .minimumShouldMatch(shouldMatch)
                .analyzer(analyzer)
                .operator(operator1);
        System.out.println(queryBuilder.analyzer());

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }


    @GetMapping("/phrase/get/title")
    public Object findByPhrase(String title, String slop) {
        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", title)
//                .analyzer("ik_max_word")
                .slop(Integer.parseInt(slop));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchPhraseQueryBuilder)
                .build();
        // 查询一个结果，如果有多个取第一个
//        SearchHit<BlogMatch> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }

    @GetMapping("/phrase/prefix/get/title")
    public Object findByPhrasePrefix(String title, String slop, String maxExpansions) {
        MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder = QueryBuilders.matchPhrasePrefixQuery("title", title)
                .maxExpansions(Integer.parseInt(maxExpansions))
//                .analyzer("ik_max_word")
                .slop(Integer.parseInt(slop));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchPhrasePrefixQueryBuilder)
                .build();
        // 查询一个结果，如果有多个取第一个
//        SearchHit<BlogMatch> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }
//
//    @GetMapping("/bool/prefix/get/title")
//    public Object findByBoolPrefix(String title, String maxExpansions, String operator, String shouldMatch) {
//        Operator operator1 = StringUtils.equals(operator, "and") ? Operator.AND : Operator.OR;
//
//        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("title", title)
////                .analyzer("ik_max_word")
//                .operator(operator1)
//                .minimumShouldMatch(shouldMatch);
//        if (maxExpansions != null) {
//            queryBuilder.maxExpansions(Integer.parseInt(maxExpansions));
//        }
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(queryBuilder);
//        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
//
//        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
//        return searchHits;
//    }

    @GetMapping("get/all")
    public Object findAll() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }


    @GetMapping("/multi")
    public Object findMultiMath(String query, Float tieBreaker) {
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query, "title", "content");
//                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
        if (tieBreaker != null) {
            queryBuilder.tieBreaker(tieBreaker);
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }

    @GetMapping("/multi/most")
    public Object findMultiMathWithMost(String query, Float tieBreaker) {
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query, "title", "content")
                .type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
        if (tieBreaker != null) {
            queryBuilder.tieBreaker(tieBreaker);
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }

    @GetMapping("/multi/cross")
    public Object findMultiMathWithCross(String query, Float tieBreaker, String operator) {
        Operator operator1 = StringUtils.equals(operator, "and") ? Operator.AND : Operator.OR;
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query, "author", "content")
                .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                .operator(operator1);
        if (tieBreaker != null) {
            queryBuilder.tieBreaker(tieBreaker);
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<BlogMatch> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, BlogMatch.class, IndexCoordinates.of("blog_match"));
        return searchHits;
    }
}
