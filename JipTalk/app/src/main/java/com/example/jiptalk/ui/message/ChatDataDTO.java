package com.example.jiptalk.ui.message;

public class ChatDataDTO {
    private String key;
    private String from;
    private String message;
    private long time;
    private String subject;
    private boolean hasResponsed;
    private String response;


    public ChatDataDTO() {

    }

    public ChatDataDTO(String from, String message, long time) {
        this.from = from;
        this.message = message;
        this.time = time;
    }

    public ChatDataDTO(String key, String from, String message, long time, String subject, boolean hasResponsed, String response) {
        this.key = key;
        this.from = from;
        this.message = message;
        this.time = time;
        this.subject = subject;
        this.hasResponsed = hasResponsed;
        this.response = response;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isHasResponsed() {
        return hasResponsed;
    }

    public void setHasResponsed(boolean hasResponsed) {
        this.hasResponsed = hasResponsed;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ChatDataDTO{" +
                "key='" + key + '\'' +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", subject='" + subject + '\'' +
                ", hasResponsed=" + hasResponsed +
                ", response='" + response + '\'' +
                '}';
    }
}
