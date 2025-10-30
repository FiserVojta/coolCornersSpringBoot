package com.lonework.corners.common.model;

public class ResultOrder {

    // name of the column to order By
    private String orderBy;

    // direction DESC or ASC are viable values
    private String orderDirection;

    public ResultOrder() {
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

}
