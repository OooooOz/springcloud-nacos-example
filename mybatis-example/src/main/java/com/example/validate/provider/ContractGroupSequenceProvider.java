package com.example.validate.provider;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import com.example.validate.dto.BaseGroupSequenceProviderSubmitDTO;
import com.example.validate.dto.GroupSequenceProviderSubmitDTO;
import com.example.validate.dto.GroupValid;

import cn.hutool.core.util.ObjectUtil;

public class ContractGroupSequenceProvider implements DefaultGroupSequenceProvider<GroupSequenceProviderSubmitDTO> {

    @Override
    public List<Class<?>> getValidationGroups(GroupSequenceProviderSubmitDTO dto) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(BaseGroupSequenceProviderSubmitDTO.class);

        if (ObjectUtil.equals("PERSON", dto.getContractType())) {
            // 个人合同校验
            defaultGroupSequence.add(GroupValid.Person.class);

        } else if ("COMPANY".equals(dto.getContractType())) {
            // 企业合同校验
            defaultGroupSequence.add(GroupValid.Company.class);
        }

        return defaultGroupSequence;
    }
}
