package com.nhnacademy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.*;

public class Server {
    /**
     * < HTTP/1.1 200 OK                            200이랑 400번만 하는걸로.
     * < Date: Thu, 21 Apr 2022 02:56:58 GMT        date사용
     * < Content-Type: application/json             default : application/json
     * < Content-Length: 33                         body 길이
     * < Connection: keep-alive                     Connection: keep-alive 가 default인듯..
     * //고정
     * < Server: gunicorn/19.9.0                    이건 자유(?)
     * < Access-Control-Allow-Origin: *             응답 헤더
     * < Access-Control-Allow-Credentials: true     이것도 고정
     * <
     */
    public static void main(String[] args) {
        //local : 127.0.0.1, port : 80
        try (ServerSocket serverSocket = new ServerSocket(80);
             Socket socket = serverSocket.accept();
             PrintStream networkOut = new PrintStream(socket.getOutputStream());
             BufferedReader networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {

            //값을 보낼때 StringBuilder 를 사용해 한번에 보내야 하나.. 이건 고민
            // 응답 내용
            String line = null;

            //TODO 헤더정보 (8줄)
            networkOut.println("HTTP/1.1 200 OK");
            networkOut.println("Date: Thu, 21 Apr 2022 17:47:24 GMT");
            networkOut.println("Content-Type: application/80json");
            networkOut.println("Content-Length: 0");
            networkOut.println("Connection: keep-alive");
            networkOut.println("Server: gunicorn/19.9.0");
            networkOut.println("Access-Control-Allow-Origin: *");
            networkOut.println("Access-Control-Allow-Credentials: true");
            networkOut.println();
            //TODO 바디정보
            networkOut.println("{\"ip\" :\" 아이피\"}");

            while ((line = networkIn.readLine()) != null) {
                System.out.println("클라이언트에서 보냄:[ " + line + " ]");
                //TODO : 파싱해서 추가하면 된다.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}