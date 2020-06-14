package com.example.jiptalk.vo;

public class MessageVO {

    String name;
    String title;
    String content;
    String time;
    String token;


    public MessageVO() {
    }

    public MessageVO(String name, String title, String content, String time, String token) {
        this.name = name;
        this.title = title;
        this.content = content;
        this.time = time;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "MessageVO{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
