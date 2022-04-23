package com.nhnacademy;

import java.util.HashMap;
import java.util.Map;

public class BodyResourceByPost {

    private Map<String, String> args = new HashMap<>();
    private String data;
    private Map<String, String> files = new HashMap<>();
    private Map<String, String> form = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> json = new HashMap<>();
    private String origin;
    private String url;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    //TODO : 사용할거
    public void setFiles(String key, String value) {
        this.files.put(key, value);
    }

    public Map<String, String> getForm() {
        return form;
    }

    public void setForm(Map<String, String> form) {
        this.form = form;
    }

    public Map<String, String> getJson() {
        return json;
    }

    public void setJson(Map<String, String> json) {
        this.json = json;
    }
}
