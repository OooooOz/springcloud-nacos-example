package com.example.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FileCheckMd5StatusEnum {

    FILE_UPLOADED(200, "文件已存在！"),

    FILE_NO_UPLOAD(404, "该文件没有上传过。"),

    FILE_UPLOAD_SOME(206, "该文件上传了一部分。");

    private final int value;

    private final String reasonPhrase;

    FileCheckMd5StatusEnum(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
