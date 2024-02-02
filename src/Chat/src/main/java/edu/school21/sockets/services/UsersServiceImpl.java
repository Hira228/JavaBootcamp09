package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component("usersServiceImpl")
public class UsersServiceImpl implements UsersService {
    UsersRepository<User> usersRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository<User> usersRepository) {
        this.usersRepository = usersRepository;
    }

    public boolean singUp(String login, String password) throws SQLException {
        if(usersRepository.findByLogin(login).isPresent()) {
            System.out.println("Пользователь с такм логином уже зарегистрирован.");
            return false;
        }
        usersRepository.save(new User(login,passwordEncoder.encode(password)));
        System.out.println("Пользователь успешно зарегистрирован.");
        return true;
    }

    public boolean singIn(String login, String password) throws SQLException {
        Optional<User> user = usersRepository.findByLogin(login);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPasswordHash())) {
            System.out.println("Пользователь с логином " + user.get().getLogin() + " вошел в систему.");
            return true;
        }
        System.out.println("Проверьте логин или пароль.");
        return false;
    }
}
