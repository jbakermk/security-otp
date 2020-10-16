package org.dryfish.securityotp.service;

import org.dryfish.securityotp.database.User;
import org.dryfish.securityotp.database.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

/**
 * In order to test the Retry annotation on UserDatabase, mock the UserRepository to throw an exception when save
 * is called and attempt to create a user through the UserService.
 */
@SpringBootTest(
        properties = {
            "spring.security.otp.retry.maxAttempts=5",
            "spring.security.otp.retry.delayExpression=1"
        }
)
public class TestUserServiceRetry {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository mockUserRepository;

    @Test
    public void verifyMultipleCallsToSaveUser() {
        doThrow(new RuntimeException()).when(mockUserRepository).save(any(User.class));
        try {
            User user = userService.register("test", "password", "secret");
        } catch (RuntimeException e) {
            // Expected
        }
        verify(mockUserRepository, times(5)).save(any(User.class));
    }
}
