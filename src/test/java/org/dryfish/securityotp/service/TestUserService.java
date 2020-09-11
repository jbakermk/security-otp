package org.dryfish.securityotp.service;

import org.dryfish.securityotp.database.User;
import org.dryfish.securityotp.database.UserRepository;
import org.dryfish.securityotp.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootTest(
	properties = { "spring.jpa.hibernate.ddl-auto=create" }
)
public class TestUserService {

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void userPersistedAndMatched() {
		User user= userService.register("test", "password", "secret");
		Optional<User> u= userRepository.findByLogin("test");
		Assertions.assertTrue(u.isPresent());
		Assertions.assertEquals("test", u.get().getLogin());
		Assertions.assertTrue(passwordEncoder.matches("password", u.get().getPasswordHash()));

		u= userService.findUser("test", "password");
		Assertions.assertTrue(u.isPresent());
	}

}
