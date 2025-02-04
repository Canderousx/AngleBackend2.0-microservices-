package com.commentsManager.app.Services.Cache;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
public class PageWrapper<T> implements Serializable {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    public Page<T> toPage() {
        return new org.springframework.data.domain.PageImpl<>(content,
                org.springframework.data.domain.PageRequest.of(pageNumber, pageSize),
                totalElements);
    }



    public PageWrapper(Page<T> page){
        this.content = page.getContent();
        this.pageNumber =page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
    }



}
