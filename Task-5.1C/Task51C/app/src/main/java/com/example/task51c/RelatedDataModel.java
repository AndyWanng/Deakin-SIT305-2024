package com.example.task51c;

import java.io.Serializable;

public class RelatedDataModel implements Serializable{

    String title, description, imageUrl, content;
    int image;

    public RelatedDataModel(String title, String description, String imageUrl, String content) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() { return content; }

    public void setContent(String content) {
        this.content = content;
    }
}
