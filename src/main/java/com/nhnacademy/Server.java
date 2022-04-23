package com.nhnacademy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

            System.out.println(requestHeader);          //헤더 출력용
            //System.out.println("---------------------------------");    // 구분자 출력용
            if (method.equals("POST")) {
                requestBody = input[1];
                body = requestBody;
                bodyDataExtract();
                bodyInfo = parsePost(socket);
                System.out.println(requestBody);        //바디 출력용
            }
            if (method.equals("GET")) {
                bodyInfo = parseGet(socket);
            }

            //테스트 공간
            System.out.println("contentJson : " + contentJson);
            //테스트 공간

            //TODO 헤더정보 (8줄)
            StringBuilder output = new StringBuilder();
            output.append(headFactory.getStateMessage());
            output.append(headFactory.getDate());
            output.append(headFactory.getContentType());
            output.append(headFactory.contentLength(bodyInfo.length()));
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

    private String parsePost(Socket socket) throws IOException {

        BodyResourceByPost bodyResourceByPost = new BodyResourceByPost();
        ObjectMapper objectMapper = new ObjectMapper();

        if (contentType.contains("application/json")) {
            bodyResourceByPost.setData(body);
            bodyResourceByPost.setJson(bodyJson);
        }
        if (contentType.contains("multipart/form-data")) {
            System.out.println("json이 번역되어 나와야 합니다 : "+createFileObject());
            System.out.println("json이 번역되어 나와야 합니다");
            bodyResourceByPost.setFiles("upload", "josn파일_읽은것_넣기"); //TODO  실제 파일 열어서 json파일가져와서 넣기.
        }
        bodyResourceByPost.setHeaders("Accept", accept);
        bodyResourceByPost.setHeaders("Content-Type", contentType);
        bodyResourceByPost.setHeaders("Host", host);
        bodyResourceByPost.setHeaders("User-Agent", userAgent);
        bodyResourceByPost.setHeaders("Accept", accept);
        bodyResourceByPost.setHeaders("Content-Length", contentLength);
        bodyResourceByPost.setOrigin(socket.getRemoteSocketAddress().toString());
        bodyResourceByPost.setUrl(uri());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bodyResourceByPost) + System.lineSeparator();
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
            if (line.startsWith("Content-Type")) {
                contentType = line.split(" ")[1] + " " + line.split(" ")[2];
                contentJson = line.split(" ")[2].split("=")[1];
            }
            if (line.startsWith("Content-Length")) {
                contentLength = line.split(" ")[1];
            }
            if (line.equals("")) {
                break;
            }
        }
    }

    private String createFileObject() throws IOException {

        byte[] bytes = contentType.getBytes(StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        boolean start = false;

        for (byte b: bytes) {
            String s = Character.toString(b);
            System.out.println(s);
            if ("{".equals(s)) {
                start = true;
            }
            if (start) {
                sb.append(s);
            }
            if ("}".equals(s)) {
                start = false;
            }
        }

        System.out.println("data:" + sb);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, String> tempMap = objectMapper.readValue(sb.toString(), new TypeReference<Map<String, String>>() {
//        });
//
//        System.out.println(tempMap);

        return sb.toString();
    }
}