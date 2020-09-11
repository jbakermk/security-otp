package org.dryfish.securityotp.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * If no other instance of {@link UserDatabase} is present in the application, this will be created by OTPWebSecurityConfiguration.
 * The h2 database is for development purposes and will persist to totp.database in the working directory.
 *
 * @author John Baker
 */
public class InMemoryUserDatabase implements UserDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryUserDatabase.class);

    @Autowired
    private UserRepository repository;

    @PostConstruct
    public void init() {
        LOGGER.info("Loaded OTP in-memory database with {} users", repository.count());
    }

    @Override
    public Optional<User> findLogin(String login) {
        return repository.findByLogin(login);
    }

    @Override
    public void saveUser(User user) {
        repository.save(user);
    }
}
