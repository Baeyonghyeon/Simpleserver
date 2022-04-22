package com.nhnacademy;

import java.util.HashMap;
import java.util.Map;

public class BodyResourceByGet {

    private Map<String, String> args = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String origin;
    private String url;

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeaders(String name, String value) {
        headers.put(name,value);
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getOrigin() {
        return origin;
    }

    public String getUrl() {
        return url;
    }
}
