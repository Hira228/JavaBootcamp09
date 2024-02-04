package edu.school21.sockets.server;

import edu.school21.sockets.repositories.UsersRepository;
import edu.school21.sockets.repositories.UsersRepositoryImpl;
import edu.school21.sockets.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component("server")
public class Server {
    public final List<Socket> sockets = Collections.synchronizedList(new ArrayList<>());
    ExecutorService executorService = Executors.newCachedThreadPool();
    @Value("${port}")
    private String port;

    private ServerSocket serverSocket;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepositoryImpl usersRepository;

    public Server() {
    }

    public void init() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(port));
        while (true) {
            Socket socket = serverSocket.accept();
            sockets.add(socket);
            executorService.execute(() -> {
                try {
                    inOrUp(socket, usersService);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private void inOrUp(Socket input, UsersService usersService) throws IOException, SQLException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(input.getOutputStream()));
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
            sendAll(in);
        } else System.out.println("что-то пошло не так!");
    }

    public void sendAll(BufferedReader in) throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            String message = in.readLine();
            if(message != null && !"".equals(message) && !sockets.isEmpty()) {
                Iterator it = sockets.iterator();
                while(it.hasNext()) {
                    Socket socket = (Socket) it.next();
                    if("exit".equalsIgnoreCase(message.split(": ")[1])){
                        System.out.println("Пользователь с логином " + message.split(":")[0] + " вышел из системы.");
                        it.remove();
                        Thread.currentThread().interrupt();
                    } else {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        bufferedWriter.write(message + '\n');
                        bufferedWriter.flush();
                    }
                }
                if(!"exit".equalsIgnoreCase(message.split(": ")[1]))
                    usersRepository.saveMessage(message.split(":")[0], message.split(": ")[1]);
            }
        }
    }
}
