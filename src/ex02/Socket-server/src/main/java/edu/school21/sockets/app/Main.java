package edu.school21.sockets.app;

import edu.school21.sockets.server.Server;
import edu.school21.sockets.services.UsersService;
import edu.school21.sockets.services.UsersServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.io.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ComponentScan(basePackages = "edu.school21")
public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        AnnotationConfigApplicationContext context = getArgs(args);
        Server server = context.getBean("server", Server.class);
        server.init();


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
