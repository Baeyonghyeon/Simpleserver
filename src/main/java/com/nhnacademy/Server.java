package com.nhnacademy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.nhnacademy.UriParseFactory.*;

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
    private HeadFactory headFactory = new HeadFactory();

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public void run() {
        String requestHeader;
        String requestBody;
        String bodyInfo = null;

        //local : 127.0.0.1, port : 80
        try (ServerSocket serverSocket = new ServerSocket(80);
             Socket socket = serverSocket.accept();
             PrintStream networkOut = new PrintStream(socket.getOutputStream())) {

            byte[] bytes = new byte[4096];
            int numOfBytes = socket.getInputStream().read(bytes);
            String response = new String(bytes, 0, numOfBytes, "UTF-8");

            String input[] = response.strip().split("\r\n\r\n");
            requestHeader = input[0];
            headerParse(requestHeader);
            methodLineSeparate();

            System.out.println(requestHeader);
            System.out.println("---------------------------------");
            if(method.equals("POST")){
                //bodyInfo = parsePost(socket);  // 기능 구현 중
                requestBody = input[1];
                body = requestBody;
                System.out.println(requestBody);        //지울것
            }
            if (method.equals("GET")) {
                bodyInfo = parseGet(socket);
            }

            //TODO 헤더정보 (8줄)
            StringBuilder output = new StringBuilder();
            output.append(headFactory.getStateMessage());
            output.append(headFactory.getDate());
            output.append(headFactory.getContentType());
            output.append(headFactory.contentLength(2222));
            output.append(headFactory.getResponseHeaderOptionField());
            output.append(System.lineSeparator());

            //TODO 바디정보
            output.append(bodyInfo);
            networkOut.append(output);

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

        if (loop[0].equals("GET")) {


        } else if (loop[0].equals("POST")) {
            //bodyInfo = bodyParse(loop, socket);
        }
        System.out.println(bodyInfo);
        return bodyInfo;
    }


    private String parseGet(Socket socket) throws JsonProcessingException {
        BodyResourceByGet bodyResourceByGet = new BodyResourceByGet();
        BodyResourceByIp bodyResourceByIp = new BodyResourceByIp();
        ObjectMapper objectMapper = new ObjectMapper();

        if (location.equals("/ip")) {
            bodyResourceByIp.setOrigin(socket.getRemoteSocketAddress().toString());
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyResourceByIp) + System.lineSeparator();
        } else {
            if (location.contains("?")) {
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

    private String parsePost(String[] loop, Socket socket) throws JsonProcessingException {

        BodyResourceByPost bodyResourceByPost = new BodyResourceByPost();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> nullValue = new HashMap<>();
        Map<String, String> headerValue = new HashMap<>();
        headerValue.put("Accept", accept);
        headerValue.put("Content-Length", "나중에 길이 받아서 넣기");   //TODO 컨탠츠 길이 변수 파싱해서 넣기
        headerValue.put("Content-Type", contentType);
        headerValue.put("Host", host);
        headerValue.put("User-Agent", userAgent);

        if (contentType.contains("application/json")) {
            bodyResourceByPost.setData(body); //TODO 변수 넣기
            bodyResourceByPost.setFiles(nullValue);
            bodyResourceByPost.setJson(bodyJson);  //TODO dataJson으로 넘겨받은 parse를 json으로 넣기
        } else if (contentType.contains("multipart/form-data")) {

            bodyResourceByPost.setFiles(nullValue); //TODO  실제 파일 열어서 json파일가져와서 넣기.
        }
        bodyResourceByPost.setHeaders(headerValue);
        bodyResourceByPost.setArgs(nullValue);
        bodyResourceByPost.setForm(nullValue);
        bodyResourceByPost.setOrigin(socket.getRemoteSocketAddress().toString());
        bodyResourceByPost.setUrl(uri());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyResourceByPost) + System.lineSeparator();
    }



        private void bodyParse(String[] loop, Socket socket) {

    }

    private void headerParse(String requestHeader) throws IOException {
        String str[] = requestHeader.split("\r\n");

        for (String line : str) {
            if (line.startsWith("GET") || line.startsWith("POST")) {
                UriParseFactory.methodLine = line;
            }
            if (line.startsWith("Host")) {
                host = line.split(" ")[1];
            }
            if (line.startsWith("User-Agent")) {
                userAgent = line.split(" ")[1];
            }
            if (line.startsWith("Accept")) {
                accept = line.split(" ")[1];
            }
            if (line.equals("Content-Type")) {
                contentType = line.split(" ")[1];
            }
            if (line.equals("")) {
                break;
            }
        }
    }

}