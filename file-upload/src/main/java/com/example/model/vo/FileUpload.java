package com.example.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload {

    private boolean uploadComplete;

    private Map<Integer, String> chunkMd5Info;

    private List<Integer> missChunks;

    private String fileName;

}
