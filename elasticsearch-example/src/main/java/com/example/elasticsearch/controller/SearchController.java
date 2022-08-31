package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.Blog;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


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

    // -----------------------------------------------聚合-分组统计------------------------------------------------//

    /**
     * 聚合-分组统计
     *
     * @return
     */
    @GetMapping("get/aggs/count")
    public Object getAggsCount() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        // 作为聚合的字段不能是text类型。所以，author的mapping要有keyword，且通过author.keyword聚合。
        query.addAggregation(AggregationBuilders.terms("author_group").field("author.keyword"));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(query.build(), Blog.class);

        Aggregations aggregations = searchHits.getAggregations();
        assert aggregations != null;
        //因为结果为字符串类型 所以用ParsedStringTerms。其他还有ParsedLongTerms、ParsedDoubleTerms等
        ParsedStringTerms per_count = aggregations.get("author_group");
        Map<String, Long> map = new HashMap<>();
        for (Terms.Bucket bucket : per_count.getBuckets()) {
            map.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return map;
    }

    // -----------------------------------------------聚合-平均------------------------------------------------//

    /**
     * 聚合-平均
     *
     * @return
     */
    @GetMapping("get/aggs/avg")
    public Object getAggsAvg() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        // 作为聚合的字段不能是text类型。
        // todo 异常
        query.addAggregation(AggregationBuilders.avg("visits_avg").field("visits"));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<Blog> searchHits = elasticsearchRestTemplate.search(query.build(), Blog.class);

        Aggregations aggregations = searchHits.getAggregations();
        assert aggregations != null;
        //因为结果为字符串类型 所以用ParsedStringTerms。其他还有ParsedLongTerms、ParsedDoubleTerms等
        ParsedAvg avg = aggregations.get("visits_avg");

        return avg.getValue();
    }
}
