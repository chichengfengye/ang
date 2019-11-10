package com.ang.reptile.exception;

public class DBException extends Exception{
    private String msg;
    private int code;

    public DBException(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

}
