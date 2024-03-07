package com.example.service;

import com.example.model.dto.ContentDTO;

public interface ConsumerService {

    String getFeignValue();

    void updateOne(ContentDTO dto);
}
