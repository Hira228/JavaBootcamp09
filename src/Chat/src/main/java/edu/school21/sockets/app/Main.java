package edu.school21.sockets.app;

import edu.school21.sockets.services.UsersService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.sql.SQLException;

@SpringBootApplication
@ComponentScan(basePackages = "edu.school21")
public class Main {
    public static void main(String[] args) throws SQLException {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class)) {
            UsersService usersService = context.getBean("usersServiceImpl", UsersService.class);
//            usersService.singUp("mama", "1234");
            //usersService.singIn("mama", "1234");
        }
    }
}
