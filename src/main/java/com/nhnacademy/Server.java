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
        String requestJson;
        String bodyInfo = null;

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

            if (method.equals("POST")) {
                requestBody = input[1];
                bodyParse(requestBody);

                if (contentType.contains("multipart/form-data")) {
                    requestJson = input[2];
                    bodyJsonParse(requestJson);

                }

                body = requestBody;
                bodyDataExtract();
                bodyInfo = parsePost(socket);

            }
            if (method.equals("GET")) {
                bodyInfo = parseGet(socket);

            }

            StringBuilder output = new StringBuilder();
            output.append(headFactory.getStateMessage());
            output.append(headFactory.getDate());
            output.append(headFactory.getContentType());
            output.append(headFactory.contentLength(bodyInfo.length()));
            output.append(headFactory.getResponseHeaderOptionField());
            output.append(System.lineSeparator());

            output.append(bodyInfo);
            networkOut.append(output);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private String parsePost(Socket socket) throws IOException {

        BodyResourceByPost bodyResourceByPost = new BodyResourceByPost();
        ObjectMapper objectMapper = new ObjectMapper();

        if (contentType.contains("application/json")) {
            bodyResourceByPost.setData(body);
            bodyResourceByPost.setJson(bodyJson);
        }
        if (contentType.contains("multipart/form-data")) {
            bodyResourceByPost.setFiles(keyName, contentJson);
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

    private void bodyParse(String requestBody) {
        String str[] = requestBody.split("\r\n");

        for (String line : str) {
            if (line.contains("Content-Disposition")) {
                keyName = line.split(" ")[2].split("\"")[1];
            }
        }
    }

    private void bodyJsonParse(String requestJson) {
        contentJson = requestJson.split("-")[0];
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
                contentType = line.split(" ")[1];
                if (contentType.contains("--")) {
                    contentType = line.split(" ")[1] + " " + line.split(" ")[2];
                }
            }
            if (line.startsWith("Content-Length")) {
                contentLength = line.split(" ")[1];
            }
            if (line.equals("")) {
                break;
            }
        }
    }
}