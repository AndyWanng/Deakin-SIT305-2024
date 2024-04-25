package com.example.task_71p.model;

public class Item {
    private int item_id;
    private String name, description, location, date, post_type, phone;

    public Item() {
    }

    public Item(String name, String description, String location, String date, String post_type, String phone) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.post_type = post_type;
        this.phone = phone;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
