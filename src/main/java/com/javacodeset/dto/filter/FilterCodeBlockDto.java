package com.javacodeset.dto.filter;

import com.javacodeset.enumeration.CodeBlockType;
import lombok.Data;

@Data
public class FilterCodeBlockDto {
    private String filterQuery;
    private Boolean filterTitle;
    private Boolean filterDescription;
    private Boolean filterContent;
    private Boolean filterTags;
    private CodeBlockType[] allowedTypes;
}
