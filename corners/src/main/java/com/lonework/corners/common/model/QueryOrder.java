package com.lonework.corners.common.model;

public enum QueryOrder {
    ASC("asc"),
    DESC("desc");

    private final String value;

    QueryOrder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
