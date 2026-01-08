package com.mordiniaa.backend.utils;

import com.mordiniaa.backend.payload.PageMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private T data;
    private PageMeta pageMeta;

    public void setUpPage(Page<?> page) {
        this.pageMeta = new PageMeta();
        pageMeta.setPage(page.getNumber());
        pageMeta.setTotalPages(page.getTotalPages());
        pageMeta.setTotalItems(page.getTotalElements());
        pageMeta.setSize(page.getSize());
    }
}
