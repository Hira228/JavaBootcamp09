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

        out.close();
        socket.close();

    }
}
