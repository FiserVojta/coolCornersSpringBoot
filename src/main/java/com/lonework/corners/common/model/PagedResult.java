package com.lonework.corners.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class PagedResult<T>{

    @JsonProperty("totalItems")
    public Long totalItems;

    @JsonProperty("data")
    public List<T> data;

    public PagedResult(List<T> data, Long totalItems) {
        this.data = data;
        this.totalItems = totalItems;
    }
}
