package com.example.elasticsearch.controller;


import com.example.elasticsearch.entry.AggregationEntry;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.aggregations.support.MultiValuesSourceFieldConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("aggregation")
public class AggregationController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 指定mapping创建索引
     *
     * @return
     */
    @PutMapping("/index/create")
    public String createIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(AggregationEntry.class);
        indexOperations.create();
        // 手动映射
        indexOperations.putMapping(indexOperations.createMapping(AggregationEntry.class));
        return "success";
    }

    @PostMapping("/document/add")
    public Object addDocument(@RequestBody AggregationEntry blog) {
        return elasticsearchRestTemplate.save(blog);
    }

    @GetMapping("/mapping/get")
    public Object getMapping() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(AggregationEntry.class);
        return indexOperations.getMapping();
    }

    // -----------------------------------------------聚合-平均------------------------------------------------//

    /**
     * 聚合-平均
     *
     * @return
     */
    @GetMapping("/avg")
    public Object getAggsAvg() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        // 作为聚合的字段不能是text类型。
        query.addAggregation(AggregationBuilders.avg("avg_age").field("age").missing(10L));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedAvg avg = aggregations.get("avg_age");

        return avg.getValue();
    }

    @GetMapping("/weighted/avg")
    public Object getAggsWeightedAvg() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        // 作为聚合的字段不能是text类型。
        MultiValuesSourceFieldConfig value = new MultiValuesSourceFieldConfig.Builder().setFieldName("age").setMissing(10L).build();
        MultiValuesSourceFieldConfig weight = new MultiValuesSourceFieldConfig.Builder().setFieldName("weight").setMissing(1L).build();

        query.addAggregation(AggregationBuilders.weightedAvg("weighted_avg_age").value(value).weight(weight));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedWeightedAvg avg = aggregations.get("weighted_avg_age");

        return avg.getValue();
    }

    @GetMapping("/cardinality")
    public Object getAggsCardinality() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        query.addAggregation(AggregationBuilders.cardinality("cardinality_age").field("age").precisionThreshold(100));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedCardinality avg = aggregations.get("cardinality_age");

        return avg.getValue();
    }

    @GetMapping("/max")
    public Object getAggsMax() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        query.addAggregation(AggregationBuilders.max("max_age").field("age").missing(10L));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedMax avg = aggregations.get("max_age");

        return avg.getValue();
    }

    @GetMapping("/min")
    public Object getAggsMin() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        query.addAggregation(AggregationBuilders.min("min_age").field("age").missing(5L));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedMin avg = aggregations.get("min_age");

        return avg.getValue();
    }

    @GetMapping("/count")
    public Object getAggsCount() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        query.addAggregation(AggregationBuilders.count("count_age").field("age").missing(5L));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedValueCount avg = aggregations.get("count_age");

        return avg.getValue();
    }

    @GetMapping("/sum")
    public Object getAggsSum() {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();

        query.addAggregation(AggregationBuilders.sum("sum_age").field("age").missing(10L));
        // 不需要获取source结果集，在aggregation里可以获取结果
        // query.withSourceFilter(new FetchSourceFilterBuilder().build());

        SearchHits<AggregationEntry> searchHits = elasticsearchRestTemplate.search(query.build(), AggregationEntry.class);

        Aggregations aggregations = searchHits.getAggregations();
        ParsedSum avg = aggregations.get("sum_age");

        return avg.getValue();
    }
}
