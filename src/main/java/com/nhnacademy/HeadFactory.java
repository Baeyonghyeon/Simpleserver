package com.nhnacademy;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HeadFactory {

    public String getDate() {
        ZonedDateTime now = ZonedDateTime.now();
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(now) + System.lineSeparator();
    }

    public String getStateMessage() {
        String message = "HTTP/1.1 200 OK" + System.lineSeparator();
        return message;
    }

    public String getContentType(){
        String defaultContentType = "Content-Type: application/json" + System.lineSeparator();
        return defaultContentType;
    }


    public String contentLength(int length) {
        String contentLength = "Content-Length: " + length + System.lineSeparator();

        return contentLength;
    }

    public String getResponseHeaderOptionField(){
        String optionField = "Server: gunicorn/19.9.0" + System.lineSeparator();
        optionField += "Access-Control-Allow-Origin: *" + System.lineSeparator();
        optionField += "Access-Control-Allow-Credentials: true" + System.lineSeparator();

        return optionField;
    }






}
