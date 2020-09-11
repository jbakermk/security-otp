package org.dryfish.securityotp.database;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique=true)
    private String login;
    private String passwordHash;
    private String secret;

    protected User() {
    }

    public User(String login, String passwordHash, String secret) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.secret = secret;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, login='%s']", id, login);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSecret() {
        return secret;
    }
}
