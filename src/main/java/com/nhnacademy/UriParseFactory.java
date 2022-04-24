package com.nhnacademy;

import java.util.HashMap;

public class UriParseFactory {

    //header
    static String methodLine;
    static String method; // GET
    static String location; // /get
    static String httpVersion; // HTTP/1.1

    static String contentLength;
    static String host;
    static String userAgent;
    static String accept;
    static String contentType = "application/json";
    //헤더에 인자를 넣어 get 요청을 할때 사용합니다.
    static HashMap<String, String> args = new HashMap<>();
    static String keyName;

    //body
    static String contentJson;
    static String body;
    static HashMap<String, String> bodyJson = new HashMap<>();


    public static String uri() {
        methodLineSeparate();
        StringBuilder sb = new StringBuilder();
        sb.append("https://").append(host).append(location);

        return sb.toString();
    }

    public static void methodLineSeparate() {
        String loop[] = methodLine.split(" ");
        method = loop[0];
        location = loop[1];
        httpVersion = loop[2];
    }


    public static void argExtract() {
        methodLineSeparate();
        String arg[] = location.split("\\?")[1].split("\\&");
        for (String str : arg) {
            String ary[] = str.split("=");
            args.put(ary[0], ary[1]);
        }
    }

    public static void bodyDataExtract() {
        methodLineSeparate();
        String arg[] = body.split(",");
        for (String str : arg) {
            str = str.replace("{", "");
            str = str.replace("}", "");
            str = str.replace("\"", "");
            String ary[] = str.split(":");
            bodyJson.put(ary[0], ary[1]);
        }
    }
}
