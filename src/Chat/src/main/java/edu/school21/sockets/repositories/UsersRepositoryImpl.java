package edu.school21.sockets.repositories;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.sockets.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;


@Component("usersRepositoryImpl")
public class UsersRepositoryImpl implements UsersRepository<User> {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersRepositoryImpl(HikariDataSource hikariDataSource) {
        this.jdbcTemplate = new JdbcTemplate(hikariDataSource);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", (rs, rowNum) -> {
            return new User(rs.getString("login" ), rs.getString("password"));
        }, id).stream().findAny();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> {
            return new User(rs.getString("login" ), rs.getString("password"));
        });
    }

    @Override
    public void save(User entity) {
        jdbcTemplate.update("INSERT INTO users(login, password) VALUES (?, ?)", entity.getLogin(), entity.getPasswordHash());
    }

    @Override
    public void update(User entity) {
        jdbcTemplate.update("UPDATE users SET login = ?, password = ? WHERE id = ?", entity.getLogin(), entity.getPasswordHash());
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> findByLogin(String email) {
        return jdbcTemplate.query("SELECT * FROM users WHERE login = ?", (rs, rowNum) -> {
            return new User(rs.getString("login" ), rs.getString("password"));
        }, email).stream().findAny();
    }
}
