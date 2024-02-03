package edu.school21.sockets.app;

import edu.school21.sockets.server.Server;
import edu.school21.sockets.services.UsersService;
import edu.school21.sockets.services.UsersServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

@ComponentScan(basePackages = "edu.school21")
public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        AnnotationConfigApplicationContext context = getArgs(args);
        Server server = context.getBean("server", Server.class);
        UsersService usersService = context.getBean("usersServiceImpl", UsersServiceImpl.class);
        server.init();
        Socket input = server.getServerSocket().accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(input.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(input.getOutputStream()));
        out.write("Hello from Server!\n");
        out.flush();


        if("singUp".equals(in.readLine())) {
            out.write("Enter username:\n");
            out.flush();
            String username = in.readLine();

            out.write("Enter password:\n");
            out.flush();
            String password = in.readLine();
            usersService.singUp(username, password);
        } else System.out.println("мимо");


        input.close();
        server.getServerSocket().close();

    }

    private static AnnotationConfigApplicationContext getArgs(String[] args) {
        org.springframework.core.env.PropertySource propertySource = new SimpleCommandLinePropertySource(args);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(propertySource);
        context.register(Main.class);
        context.refresh();
        return context;
    }

}
