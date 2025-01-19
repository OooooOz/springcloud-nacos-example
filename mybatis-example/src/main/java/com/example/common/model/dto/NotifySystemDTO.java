package com.example.common.model.dto;

import com.example.common.model.vo.DetailVo;
import lombok.Data;

import java.util.List;

@Data
public class NotifySystemDTO {

    private Long id;

    private List<DetailVo> detailVos;
}
