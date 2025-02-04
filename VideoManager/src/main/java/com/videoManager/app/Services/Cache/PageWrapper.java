package com.videoManager.app.Services.Cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.videoManager.app.Models.Records.VideoRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
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
