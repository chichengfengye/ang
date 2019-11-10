package com.ang.reptile.model;

public class Message {
    private String msg;
    private int code;
    private Object data;

    public Message() {

    }

    public Message(int code, String msg) {
        this.msg = msg;
        this.code = code;
        this.data = null;
    }

    public Message(int code, String msg, Object data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static Message failure(String msg) {
        Message message = new Message(-1, msg);
        return message;
    }

    public static Message failure() {
        Message message = new Message(-1, "error");
        return message;
    }
    public static Message success(String msg) {
        Message message = new Message(1, msg);
        return message;
    }

    public static Message success() {
        Message message = new Message(1, "success");
        return message;
    }

    public static Message success(Object data) {
        Message message = new Message(1, "success",data);
        return message;
    }
}
