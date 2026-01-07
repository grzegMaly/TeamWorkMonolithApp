package com.mordiniaa.backend.utils;

import com.mordiniaa.backend.payload.PageMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private T data;
    private PageMeta page;
}
