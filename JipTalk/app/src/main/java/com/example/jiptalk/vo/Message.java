package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    String from;
    String message;
    long time;
    String title;
    String type;


    public Message() {
    }

    public Message(String from, String message, long time, String title, String type) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.title = title;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                '}';
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("message", message);
        result.put("time", time);
        result.put("title", title);
        result.put("type", type);
        return result;
    }
}
