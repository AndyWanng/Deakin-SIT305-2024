package com.example.task51c_youtube.entity;

import java.util.List;

public class Playlist {

    private String userId;
    private List<String> links;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

}