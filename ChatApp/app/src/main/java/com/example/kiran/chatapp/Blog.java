package com.example.kiran.chatapp;

public class Blog {

    private String desc;
    private String title;
    private String ImageUrl;



    private  String username;

    public Blog(){

    }

    public Blog(String desc, String title, String imageUrl) {
        this.desc = desc;
        this.title = title;
        this.username = username;
        ImageUrl = imageUrl;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
