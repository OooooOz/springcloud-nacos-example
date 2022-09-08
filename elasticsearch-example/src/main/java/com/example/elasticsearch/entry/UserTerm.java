package com.example.elasticsearch.entry;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Document(indexName = "user_term", shards = 3, replicas = 1)
public class UserTerm {
    //此项作为id，不会写到_source里边。
    @Id
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String name;

    //博客所属分类。
    @Field(type = FieldType.Keyword, name = "programming_languages")
    private List<String> programmingLanguages;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String address;

    // 访问量
    @Field(type = FieldType.Integer, name = "required_matches")
    private int requiredMatches;

}
