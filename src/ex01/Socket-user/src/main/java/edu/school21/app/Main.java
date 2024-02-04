package edu.school21.app;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8081);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Scanner scanner = new Scanner(System.in);
        System.out.println(in.readLine());

        String singUp = scanner.nextLine();
        out.write(singUp + "\n");
        out.flush();

        System.out.println(in.readLine());

        String username = scanner.nextLine();
        out.write(username + "\n");
        out.flush();

        System.out.println(in.readLine());

        String password = scanner.nextLine();
        out.write(password + "\n");
        out.flush();
        echo(in, out, scanner, username);
        socket.close();
    }

    public static void echo(BufferedReader in,BufferedWriter out, Scanner scanner, String username) throws IOException {
        Thread thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String inStr = in.readLine();
                    if(inStr!=null)System.out.println(inStr);
                } catch (IOException ignore) {

                }
            }
        });
        thread.start();
        String str = null;
        while(!"exit".equalsIgnoreCase(str)) {
            str = scanner.nextLine();
            if(!"".equals(str)) {
                out.write(username + ": " + str + "\n");
                out.flush();
            }
        }
        System.out.println("You have left the chat.");
        thread.interrupt();
    }
}
