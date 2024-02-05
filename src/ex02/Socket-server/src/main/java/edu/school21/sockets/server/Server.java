package edu.school21.sockets.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.Room;
import edu.school21.sockets.repositories.UsersRepositoryImpl;
import edu.school21.sockets.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Component("server")
public class Server {
    public final List<Socket> sockets = Collections.synchronizedList(new ArrayList<>());
    public final List<Room> rooms = new ArrayList<>();
    @Autowired
    ObjectMapper objectMapper;

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

            default:
                break;
        }


    }

    public void sendAll(BufferedReader in, Room room) throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            String message = in.readLine();
            if(message != null && !"".equals(message) && !sockets.isEmpty()) {
                Iterator it = room.getRoomAttendees().iterator();
                while(it.hasNext()) {
                    Socket socket = (Socket) it.next();
                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        bufferedWriter.write(message + '\n');
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        System.out.println("Пользователь с логином " + message.split(":")[0] + " вышел из системы.");
                        it.remove();
                    }
                }
                if(!"exit".equalsIgnoreCase(message.split(": ")[1])) {
                    Message messageClass = new Message(message.split(": ")[1], usersRepository.findByLogin(message.split(":")[0]).get().getId(), room.getId());
                    String jsonString = objectMapper.writeValueAsString(messageClass);
                    System.out.println(jsonString);
                    usersRepository.saveMessage(message.split(":")[0], message.split(": ")[1]);
                    room.addMessage(message);
                }
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
                synchronized (rooms) {
                    rooms.add(new Room(in.readLine()));
                }
                rooms.get(rooms.size() - 1).getRoomAttendees().add(socketUser);
                out.write("The room has been successfully created.\n");
                out.flush();
                sendAll(in, rooms.get(rooms.size() - 1));
                break;
            case "2":
                out.write(Long.toString(rooms.size()) + "\n");
                out.flush();
                for(int i = 1; i <= rooms.size(); ++i) {
                    out.write(i + ". " + rooms.get(i - 1).getName() + "\n");
                    out.flush();
                }
                out.write((rooms.size() + 1) + ". Exit\n");
                out.flush();

                String numberRoom = in.readLine();
                try {
                    if (!(Integer.parseInt(numberRoom) > rooms.size()) || !(Integer.parseInt(numberRoom) < 0)) {
                        rooms.get(Integer.parseInt(numberRoom) - 1).getRoomAttendees().add(socketUser);
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketUser.getOutputStream()));
                        for (String message : rooms.get(Integer.parseInt(numberRoom) - 1).getMessagesRoom()) {
                            bufferedWriter.write(message + '\n');
                            bufferedWriter.flush();
                        }
                        sendAll(in, rooms.get(Integer.parseInt(numberRoom) - 1));
                    }
                } catch (Exception ignore) {}
                break;

            default:
                break;
        }

    }
}
