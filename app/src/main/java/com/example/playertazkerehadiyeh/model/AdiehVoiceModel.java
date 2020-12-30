package com.example.playertazkerehadiyeh.model;

public class AdiehVoiceModel {
    String url;
    String title;
    String singer;
    String endpoint;
    int id;

    public AdiehVoiceModel(String url, String title, String singer, String endpoint, int id) {
        this.url = url;
        this.title = title;
        this.singer = singer;
        this.endpoint = endpoint;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
