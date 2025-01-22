package com.example.common.model.dto;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonEventDTO<T> extends ApplicationEvent {

    private T targetClass;

    public CommonEventDTO(Object source, T targetClass) {
        super(source);
        this.targetClass = targetClass;
    }
}
