package com.example.personalizedquizapp;

public class TopicModel {
    private String topicId, topicName, topicImage;

    public TopicModel(String topicId, String topicName, String topicImage) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicImage = topicImage;
    }

    public TopicModel() {}

    public String getTopicId() { return topicId; }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }
}
