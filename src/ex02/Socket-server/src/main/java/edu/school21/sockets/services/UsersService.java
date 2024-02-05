package edu.school21.sockets.services;


import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component("usersService")
public interface UsersService {
    boolean singUp(String login, String password) throws SQLException;
    boolean singIn(String login, String password) throws SQLException;
}

