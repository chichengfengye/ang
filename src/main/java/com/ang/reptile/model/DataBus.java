package com.ang.reptile.model;

public class DataBus<T> {
    public static final int SUCCESS_CODE = 1;
    public static final int FAILURE_CODE = -1;
    public static final String SUCCESS_MSG = "success";
    public static final String FAILURE_MSG = "failure";

    private String msg;
    private int code;
    private T data;

    public DataBus() {

    }

    public DataBus(int code, String msg, T data) {
        this.msg = msg;
        this.code = code;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static DataBus failure(String msg) {
        DataBus message = new DataBus(-1, msg, null);
        return message;
    }

    public static DataBus failure() {
        DataBus message = new DataBus(-1, "error", null);
        return message;
    }
    public static DataBus success(String msg) {
        DataBus message = new DataBus(1, msg, null);
        return message;
    }

    public static DataBus success() {
        DataBus message = new DataBus(1, "success", null);
        return message;
    }
}
