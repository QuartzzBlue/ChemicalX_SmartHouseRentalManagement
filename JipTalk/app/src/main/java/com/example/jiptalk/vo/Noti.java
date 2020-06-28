package com.example.jiptalk.vo;

public class Noti {
    String buildingKey;
    String buildingName;
    String title;
    String content;
    long time;
    String key;
    String from;


    public Noti() {
    }

    public Noti(String buildingKey, String buildingName, String title, String content, long time, String key, String from) {
        this.buildingKey = buildingKey;
        this.buildingName = buildingName;
        this.title = title;
        this.content = content;
        this.time = time;
        this.key = key;
        this.from = from;
    }

    public String getBuildingKey() {
        return buildingKey;
    }

    public void setBuildingKey(String buildingKey) {
        this.buildingKey = buildingKey;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "Noti{" +
                "buildingKey='" + buildingKey + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", key='" + key + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
