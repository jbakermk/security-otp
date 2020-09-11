package org.dryfish.securityotp.database;

import org.springframework.retry.annotation.Retryable;

import java.util.Optional;

/**
 * Provide your own implementation and place in the Spring application scope, or the default h2 database implementation
 * will be deployed.
 *
 * @author John Baker
 */
public interface UserDatabase {

    @Retryable(value = RuntimeException.class)
    public Optional<User> findLogin(String login);

    @Retryable(value = RuntimeException.class)
    public void saveUser(User user);
}