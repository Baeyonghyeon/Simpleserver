package com.nhnacademy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static com.nhnacademy.UriParseFactory.*;

public class Server2 {

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
    private HeadFactory headFactory = new HeadFactory();

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
    public void run(){
        //local : 127.0.0.1, port : 80
        try (ServerSocket serverSocket = new ServerSocket(80);
             Socket socket = serverSocket.accept();
             PrintStream networkOut = new PrintStream(socket.getOutputStream());
        ) {
            //BufferedReader networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
            // 응답 내용
//                String line = null;
//                String bodyInfo = null;
//                while ((line = networkIn.readLine()) != null) {
//
//                    if(line.startsWith("GET") || line.startsWith("POST")){
//                        UriParseFactory.methodLine = line;
//                    }
//                    if(line.startsWith("Host")){
//                        host = line.split(" ")[1];
//                    }
//                    if(line.startsWith("User-Agent")){
//                        userAgent = line.split(" ")[1];
//                    }
//                    if(line.startsWith("Accept")){
//                        accept = line.split(" ")[1];
//                    }
//                    if(line.equals("Content-Type")){
//                        contentType = line.split(" ")[1];
//                    }
//                    if(line.equals("")){
//                        break;
//                    }
//                    System.out.println("클라이언트에서 보냄:[ " + line + " ]");
//                }
//                System.out.println("head end");

            byte[] bytes = new byte[300];
            socket.getInputStream().read(bytes);
            StringBuilder sb = new StringBuilder();
            BufferedReader bodyIn = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
            bodyIn.lines().peek(System.out::println).forEach(lin -> sb.append(lin).append(System.lineSeparator()));

//                System.out.println("data : "+ data.toString());
//                bodyInfo = loop(UriParseFactory.methodLine, socket);

//                BodyResourceByIp bodyRes = new BodyResourceByIp();
//                ObjectMapper objectMapper = new ObjectMapper();
//                bodyRes.setOrigin(socket.getRemoteSocketAddress().toString());
//                String bodyInfo = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyRes);
//                bodyInfo += System.lineSeparator();

//                //TODO 헤더정보 (8줄)
//                StringBuilder output = new StringBuilder();
//                output.append(headFactory.getStateMessage());
//                output.append(headFactory.getDate());
//                output.append(headFactory.getContentType());
//                output.append(headFactory.contentLength(bodyInfo.length()));
//                output.append(headFactory.getResponseHeaderOptionField());
//                output.append(System.lineSeparator());
//
//                //TODO 바디정보
//                output.append(bodyInfo);
//                networkOut.append(output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /***
     *  BodyResourceByIp bodyRes = new BodyResourceByIp();
     *  ObjectMapper objectMapper = new ObjectMapper();
     *  bodyRes.setOrigin(socket.getRemoteSocketAddress().toString());
     *  String bodyInfo = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyRes);
     *  bodyInfo += System.lineSeparator();
     */
    public String loop(String line, Socket socket) throws JsonProcessingException {
        String bodyInfo = null;

        String loop[] = line.split(" ");

        if(loop[0].equals("GET")){
            bodyInfo = parseGet(loop, socket);

        } else if(loop[0].equals("POST")){
            bodyInfo = parsePost(loop, socket);
        }
        System.out.println(bodyInfo);
        return bodyInfo;
    }


    private String parseGet(String[] loop, Socket socket) throws JsonProcessingException {
        //TODO: 다형성 만들어야함
        BodyResourceByGet bodyResourceByGet = new BodyResourceByGet();
        BodyResourceByIp bodyResourceByIp = new BodyResourceByIp();
        ObjectMapper objectMapper = new ObjectMapper();

        if (loop[1].equals("/ip")) {
            bodyResourceByIp.setOrigin(socket.getRemoteSocketAddress().toString());
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyResourceByIp) + System.lineSeparator();
        } else {
            if (loop[1].contains("?")) {
                argExtract();
                bodyResourceByGet.setArgs(args);
            }
            bodyResourceByGet.setHeaders("Host", host);
            bodyResourceByGet.setHeaders("User-Agent", userAgent);
            bodyResourceByGet.setHeaders("Accept", accept);
            bodyResourceByGet.setOrigin(socket.getRemoteSocketAddress().toString());
            bodyResourceByGet.setUrl(uri());
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyResourceByGet) + System.lineSeparator();
        }
    }


    private String parsePost(String[] loop, Socket socket) {
        return null;
    }


}