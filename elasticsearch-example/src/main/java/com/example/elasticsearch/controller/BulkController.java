package com.example.elasticsearch.controller;

import com.example.elasticsearch.dto.PageDto;
import com.example.elasticsearch.entry.UserTerm;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 本处只是展示索引的操作方法，项目中不会这样创建索引，而是手写DSL去创建。
 */
@RestController
public class BulkController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 批量插入数据，存在即更新
     *
     * @param userTerms
     * @return 返回插入的文档id列表
     */
    @PostMapping("/bulk/index")
    public Object bulkByCreate(@RequestBody List<UserTerm> userTerms) {
        List<IndexQuery> indexQueries = new ArrayList<>();
        userTerms.stream().forEach(userTerm -> {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setObject(userTerm);
            indexQueries.add(indexQuery);
        });
        // 还可以设置BulkOptions，进行一些配置，比如是否立即刷新refresh
//        BulkOptions bulkOptions = BulkOptions.builder().withRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).build();
//        List<String> strings = elasticsearchRestTemplate.bulkIndex(indexQueries,bulkOptions, IndexCoordinates.of("user_term"));
        List<String> strings = elasticsearchRestTemplate.bulkIndex(indexQueries, IndexCoordinates.of("user_term"));
        return strings;
    }

    /**
     * 批量插入数据，存在即更新
     *
     * @param userTerms
     * @return 返回插入的文档source数据
     */
    @PostMapping("save/list")
    public Object addDocuments(@RequestBody List<UserTerm> userTerms) {
        // 内部也是使用的bulkIndex方法，添加后好像不会立即刷新refresh
        return elasticsearchRestTemplate.save(userTerms);
    }

    /**
     * 批量修改文档内容
     *
     * @param userTerms
     */
    @PostMapping("bulk/update")
    public void editDocumentsPart(@RequestBody List<UserTerm> userTerms) {
        List<UpdateQuery> updateQueryList = new ArrayList<>();
        userTerms.stream().forEach(userTerm -> {
            UpdateQuery updateQuery = null;
            Document document = Document.create();
            document.put("address", userTerm.getAddress());

            if (userTerm.getUserId().equals(4L)) {
                updateQuery = UpdateQuery.builder(String.valueOf(userTerm.getUserId())).withDocument(document).withDocAsUpsert(true).build();
            } else if (userTerm.getUserId().equals(5L)) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("address", userTerm.getAddress());
                String idOrCode = "ctx._source['address']= params.address;";
                updateQuery = UpdateQuery.builder(String.valueOf(userTerm.getUserId()))
                        .withScript(idOrCode).withLang(Script.DEFAULT_SCRIPT_LANG).withParams(map)
                        .withUpsert(document).withScriptedUpsert(true)
                        .build();
            } else {
                updateQuery = UpdateQuery.builder(String.valueOf(userTerm.getUserId())).withDocument(document).build();
            }
            updateQueryList.add(updateQuery);
        });
        elasticsearchRestTemplate.bulkUpdate(updateQueryList, IndexCoordinates.of("user_term"));
    }

    @PostMapping("bulk/delete")
    public void deleteDocument(@RequestBody List<String> userIds) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termsQuery("userId", userIds))
                .build();
        elasticsearchRestTemplate.delete(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    @PostMapping("update/{name}")
    public Object updateDocumentByAddress(@PathVariable("name") String name, @RequestBody UserTerm userTerm) throws IOException {

        UpdateByQueryRequest request = new UpdateByQueryRequest("user_term");
        request.setQuery(QueryBuilders.matchQuery("name", name));

        HashMap<String, Object> map = new HashMap<>();
        map.put("address", userTerm.getAddress());
        request.setScript(new Script(Script.DEFAULT_SCRIPT_TYPE, Script.DEFAULT_SCRIPT_LANG, "ctx._source['address']=params.address;", map));
//        request.setScript(new Script("ctx._source['address']=" + userTerm.getAddress() + ";"));
        BulkByScrollResponse updateByQuery = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);

//        这种写法必须要有文档id
//        Document document = Document.create();
//        document.put("content", blog.getContent());
//        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(document).build();
//        UpdateResponse response = elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("blog"));
        return updateByQuery;
    }

    @PostMapping("delete/{name}")
    public void deleteDocumentByQuery(@PathVariable("name") String name) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", name)).build();

        elasticsearchRestTemplate.delete(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    /**
     * 同一个索引的mget多文档查询
     *
     * @param ids
     * @return 实体类集合
     */
    @GetMapping("mget")
    public Object mGetDocument(@RequestBody List<String> ids) {
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes("name", "address").build();
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withIds(ids).withSourceFilter(sourceFilter).build();
        return elasticsearchRestTemplate.multiGet(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    @GetMapping("mSearch")
    public Object mSearchDocument(@RequestBody List<String> ids) {
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes("name", "address").build();
        List<NativeSearchQuery> list = new ArrayList<>();
        ids.stream().forEach(id -> {
            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("userId", id))
                    .withSourceFilter(sourceFilter).build();
            list.add(nativeSearchQuery);
        });
        return elasticsearchRestTemplate.multiSearch(list, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    @GetMapping("search/range")
    public Object rangeSearchDocument() {
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("userId").gt(2L).lte(4L);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        return elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    @GetMapping("search/fuzzy")
    public Object fuzzySearchDocument(String name, String fuzziness, Integer maxExpansions, Integer prefixLength) {
        Fuzziness fuzziness1 = null;
        if (StringUtils.equals(fuzziness, "1")) {
            fuzziness1 = Fuzziness.ONE;
        } else if (StringUtils.equals(fuzziness, "2")) {
            fuzziness1 = Fuzziness.TWO;
        } else {
            fuzziness1 = Fuzziness.AUTO;
        }
        FuzzyQueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("name", name)
                .fuzziness(fuzziness1).maxExpansions(maxExpansions).prefixLength(prefixLength);

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        return elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    @GetMapping("search/match/fuzzy")
    public Object matchFuzzySearchDocument(String name, String fuzziness, Integer maxExpansions, Integer prefixLength) {
        Fuzziness fuzziness1 = null;
        if (StringUtils.equals(fuzziness, "1")) {
            fuzziness1 = Fuzziness.ONE;
        } else if (StringUtils.equals(fuzziness, "2")) {
            fuzziness1 = Fuzziness.TWO;
        } else {
            fuzziness1 = Fuzziness.AUTO;
        }

        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("name", name)
                .fuzziness(fuzziness1).maxExpansions(maxExpansions).prefixLength(prefixLength);

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        return elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }


    @GetMapping("search/bool/exists")
    public Object matchFuzzySearchDocument(String field) {

//        ExistsQueryBuilder queryBuilder = QueryBuilders.existsQuery(field);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.existsQuery(field));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        return elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

    /**
     * 分页查询
     *
     * @param pageDto
     * @return
     */
    @GetMapping("search/page")
    public Page<UserTerm> getAllPage(@RequestBody PageDto pageDto) {
        // page 页码 from = （page-1）* size;从第0页开始
        // size 页大小
        PageRequest pageRequest = PageRequest.of(pageDto.getPage() - 1, pageDto.getSize());
        SortOrder sortOrder = StringUtils.equals(pageDto.getOrder(), SortOrder.DESC.toString()) ? SortOrder.DESC : SortOrder.ASC;
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes(pageDto.getIncludes()).build();

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(pageRequest)
                .withSort(SortBuilders.fieldSort(pageDto.getSort()).order(sortOrder))
                .withSourceFilter(sourceFilter)
                .build();

        SearchHits<UserTerm> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class);

        List<UserTerm> userTerms = new ArrayList<>();
        searchHits.stream().forEach(v -> userTerms.add(v.getContent()));
        return new PageImpl<>(userTerms, pageRequest, searchHits.getTotalHits());
    }


    @GetMapping("search/bool/wildcard")
    public Object matchWildcardSearchDocument(String field, String value) {

//        ExistsQueryBuilder queryBuilder = QueryBuilders.wildcardQuery(field,value);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery(field, value));

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        return elasticsearchRestTemplate.search(nativeSearchQuery, UserTerm.class, IndexCoordinates.of("user_term"));
    }

}
