package com.example.jiptalk.ui.message;

public class ChatDataDTO {
    private String from;
    private String message;
    private long time;


    public ChatDataDTO() {

    }

    public ChatDataDTO(String from, String message, long time) {
        this.from = from;
        this.message = message;
        this.time = time;
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

    @Override
    public String toString() {
        return "ChatDataDTO{" +
                "from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
