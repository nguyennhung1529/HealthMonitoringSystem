package com.example.healthmonitor.object;

import android.net.Uri;

import java.util.Date;

public class Post {
    private String title;
    private String description;
    private String source;
    private String urlImage;

    public Post() {
    }

    public Post(String title, String description, String source, String urlImage) {
        this.title = title;
        this.description = description;
        this.source = source;
        this.urlImage = urlImage;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", urlImage='" + urlImage + '\'' +
                '}';
    }
}
