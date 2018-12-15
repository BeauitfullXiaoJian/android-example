package com.example.cool1024.android_example.http;

public class RequestParam {
    private String name;
    private String value;

    public RequestParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public RequestParam(String name, int value) {
        this.name = name;
        this.value = String.valueOf(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
