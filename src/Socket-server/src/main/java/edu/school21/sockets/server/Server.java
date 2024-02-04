package edu.school21.sockets.server;

import edu.school21.sockets.models.Room;
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
    public List<Room> rooms = new ArrayList<>();
    public int countRooms;
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
        out.write("1. signIn\n");
        out.flush();
        out.write("2. SignUp\n");
        out.flush();
        out.write("3. Exit\n");
        out.flush();
        String sing = in.readLine();
        String username = null;
        String password = null;
        switch (sing) {
            case "1":
                out.write("Enter username:\n");
                out.flush();
                username = in.readLine();

                out.write("Enter password:\n");
                out.flush();
                password = in.readLine();
                usersService.singIn(username, password);
                createOrChoiceRoom(out, in, input);
                break;

            case "2":
                out.write("Enter username:\n");
                out.flush();
                username = in.readLine();

                out.write("Enter password:\n");
                out.flush();
                password = in.readLine();
                usersService.singUp(username, password);
                createOrChoiceRoom(out, in, input);
                break;

            case "3":
                break;

            default:
        }


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

    public void createOrChoiceRoom(BufferedWriter out, BufferedReader in, Socket socketUser) throws IOException {
        out.write("1.\tCreate room\n");
        out.flush();
        out.write("2.\tChoose room\n");
        out.flush();
        out.write("3.\tExit\n");
        out.flush();

        String choice = in.readLine();

        switch (choice) {
            case "1":
                out.write("Write down the name of the room.\n");
                out.flush();
                countRooms++;
                rooms.add(new Room(in.readLine()));
                rooms.get(countRooms - 1).roomAttendees.add(socketUser);
                out.write("The room has been successfully created.\n");
                out.flush();
                sendAll(in);
                break;
            case "2":
                out.write(Integer.toString(countRooms));
                out.flush();
                for(int i = 0; i < countRooms; ++i) {
                    out.write(i + ". " + rooms.get(i).getName() + "\n");
                    out.flush();
                }
                out.write((countRooms + 1) + ". Exit\n");
                out.flush();

                String numberRoom = in.readLine();
                if(!(Integer.parseInt(numberRoom) > countRooms) || !(Integer.parseInt(numberRoom) < 0)) {
                    rooms.get(Integer.parseInt(numberRoom)).roomAttendees.add(socketUser);
                    sendAll(in);
                }
                break;
        }

    }
}
