package com.lonework.corners.common.model;

import org.springframework.data.repository.query.Param;


public record PagingQueryParams(
        @Param("page") Integer page,
        @Param("size") Integer size,
        @Param("orderBy") ResultOrder orderBy,
        @Param("order") QueryOrder order
) {
}
