package com.nhnacademy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("클라이언트 입장");

        while (true) {
            System.out.print("입력하세요:");
            String theLine = userIn.readLine();
            out.println(theLine);
            out.flush();
            if (theLine.equalsIgnoreCase("Quit"))
                break;
            System.out.println("서버에서 보냄:[ " + networkIn.readLine() + " ]");
        }
        System.out.println("클라이언트 종료..");
        networkIn.close();
        out.close();
        socket.close();
    }
}