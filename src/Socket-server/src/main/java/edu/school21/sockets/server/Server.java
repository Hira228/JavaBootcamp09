package edu.school21.sockets.server;

import edu.school21.sockets.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("server")
public class Server {
    final List<BufferedWriter> list = new ArrayList<>();
    @Value("${port}")
    private String port;

    private ServerSocket serverSocket;

    @Autowired
    private UsersService usersService;

    public Server() {
    }

    public void init() throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        serverSocket = new ServerSocket(Integer.parseInt(port));
        while (true) {
            try {
                Socket input = getServerSocket().accept();
                executorService.execute(() -> {
                    try {
                        inOrUp(input, usersService);
                    } catch (IOException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException ignore) {

            }
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private void inOrUp(Socket input, UsersService usersService) throws IOException, SQLException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(input.getOutputStream()));
        synchronized (list) {
            list.add(out);
        }
        out.write("Hello from Server!\n");
        out.flush();
        String sing = in.readLine();
        if ("singUp".equals(sing)) {
            out.write("Enter username:\n");
            out.flush();
            String username = in.readLine();

            out.write("Enter password:\n");
            out.flush();
            String password = in.readLine();
            usersService.singUp(username, password);
        } else if ("singIn".equals(sing)) {
            out.write("Enter username:\n");
            out.flush();
            String username = in.readLine();

            out.write("Enter password:\n");
            out.flush();
            String password = in.readLine();
            usersService.singIn(username, password);
            echo(in);
        } else System.out.println("что-то пошло не так!");
    }

    public void echo(BufferedReader in) throws IOException {
        while (true) {
            String message = in.readLine();
            if(message != null && !list.isEmpty()) {
                for (BufferedWriter bufferedReader : list) {
                    try {
                        bufferedReader.write(message + '\n');
                        bufferedReader.flush();
                    } catch (IOException e) {
                        synchronized (list) {
                            list.remove(bufferedReader);
                            System.out.println("Пользователь с логином " + message.split(":")[0] + " вышел из системы.");
                        }
                    }
                }
            }
        }
    }
}
