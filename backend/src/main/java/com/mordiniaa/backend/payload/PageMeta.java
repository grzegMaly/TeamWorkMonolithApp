package com.mordiniaa.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageMeta {

    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
