package com.lonework.corners.model.request;

public class TagCreateRequest {

    public String name;
    public String creator;

    public TagCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
