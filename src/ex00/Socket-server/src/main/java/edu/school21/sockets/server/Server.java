package edu.school21.sockets.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
@Component("server")
public class Server {

    @Value("${port}")
    private String port;

    private ServerSocket serverSocket;

    public Server() {}

    public void init() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(port));
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
