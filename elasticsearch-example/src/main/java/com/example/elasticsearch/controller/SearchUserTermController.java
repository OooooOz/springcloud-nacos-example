package com.example.elasticsearch.controller;

import com.example.elasticsearch.entry.UserTerm;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.indices.TermsLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 本处只是展示索引的操作方法，项目中不会这样创建索引，而是手写DSL去创建。
 */
@RestController
public class SearchUserTermController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 指定mapping创建索引
     *
     * @return
     */
    @PutMapping("/term/index/create")
    public String createIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(UserTerm.class);
        indexOperations.create();
        // 手动映射
        indexOperations.putMapping(indexOperations.createMapping(UserTerm.class));
        return "success";
    }

    @PostMapping("/term/document/add")
    public Object addDocument(@RequestBody UserTerm blog) {
        return elasticsearchRestTemplate.save(blog);
    }

    @GetMapping("/term/mapping/get")
    public Object getMapping() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(UserTerm.class);
        return indexOperations.getMapping();
    }


    @GetMapping("/term/get/name")
    public Object findByString(String name) {
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", name);

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        // 查询一个结果，如果有多个取第一个
//        SearchHit<UserTerm> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
        SearchHits<UserTerm> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
        return searchHits;
    }

    @GetMapping("/term/get/names")
    public Object findByTerms(@RequestBody List<String> names) {
        // 可以是动态参数也可使collection
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("name", names);

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        // 查询一个结果，如果有多个取第一个
//        SearchHit<UserTerm> searchHit = elasticsearchRestTemplate.searchOne(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
        SearchHits<UserTerm> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
        return searchHits;
    }

    @GetMapping("/term/get/lookup")
    public Object findByTermsLookUp(String outIndex, String innerIndex, String innerId, String innerPath) {
        TermsQueryBuilder queryBuilder = QueryBuilders.termsLookupQuery("name", new TermsLookup(innerIndex, innerId, innerPath));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        SearchHits<UserTerm> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of(outIndex));
        return searchHits;
    }

//    @GetMapping("get/terms/set")
//    public Object findByTermsSet(String outIndex, String innerIndex, String innerId, String innerPath) {
//        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("name", new TermsLookup(innerIndex, innerId, innerPath)).t.;
//
//        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
//                .withQuery(queryBuilder)
//                .build();
//        SearchHits<UserTerm> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of(outIndex));
//        return searchHits;
//    }

    @GetMapping("/bool/get")
    public Object findByBool() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.matchQuery("programming_languages", "java"))
                .must(QueryBuilders.termQuery("required_matches", "2"))
                .should(QueryBuilders.matchQuery("address", "SZ"))
                .should(QueryBuilders.termQuery("name", "Tom Wilson"))
                .minimumShouldMatch("1");

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<UserTerm> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
        return searchHits;
    }
}
