package org.dryfish.securityotp.service;

import org.dryfish.securityotp.database.User;
import org.dryfish.securityotp.database.UserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

public class UserService {

    @Autowired
    private UserDatabase users;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String login, String password, String secret) {
        User user = new User(login, passwordEncoder.encode(password), secret);
        users.saveUser(user);
        return user;
    }

    public Optional<User> findUser(String login) {
        return users.findLogin(login);
    }

    public Optional<User> findUser(String login, String password) {
        return users.findLogin(login).filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    public boolean doesUserExist(String login) {
        return users.findLogin(login).isPresent();
    }
}