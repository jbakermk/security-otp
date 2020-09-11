package org.dryfish.securityotp.controllers;

import org.dryfish.securityotp.OTPAuthenticatedToken;
import org.dryfish.securityotp.OTPConfiguration;
import org.dryfish.securityotp.database.User;
import org.dryfish.securityotp.service.TOTPService;
import org.dryfish.securityotp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * This controller deals with authenticating clients against the @{link UserDatabase} implementation through
 * verifying the user, password and token.
 *
 * @author John Baker
 */
@ResponseBody
@RequestMapping(value = "/otp/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private TOTPService totpService;
    @Autowired
    private OTPConfiguration otpConfiguration;

    @PostConstruct
    public void init() {
        LOGGER.debug("Created AuthenticationController exposing /otp/authenticate");
    }

    @PostMapping("/{login}")
    public AuthenticationStatus authenticate(@PathVariable @NotBlank String login, @RequestBody Map<String, Object> data) {
        SecurityContextHolder.getContext().setAuthentication(null);

        AuthenticationStatus status = AuthenticationStatus.FAILED;

        if (otpConfiguration.isEnableUserPasswordAuthentication()) {
            LOGGER.debug("Authenticating user {}", login);
            // Was a password passed and can it be used to lookup an account?
            String password = data.getOrDefault("password", "").toString();
            if (password.length() > 0 && userService.findUser(login, password).isPresent()) {
                status = AuthenticationStatus.REQUIRE_TOKEN_CHECK;
            }
            LOGGER.debug("Returning status {}", status);
        }

        return status;
    }

    @PostMapping("/token/{login}")
    public AuthenticationStatus token(@PathVariable @NotBlank String login, @RequestBody Map<String, Object> data) {
        SecurityContextHolder.getContext().setAuthentication(null);

        AuthenticationStatus status = AuthenticationStatus.FAILED;

        LOGGER.debug("Verifying token for user {}", login);
        Optional<User> user = userService.findUser(login);
        if (user.isPresent()) {
            LOGGER.debug("Located user {}", user.get());

            // Was a token passed and if so is it valid?
            String token = data.getOrDefault("token", "").toString();
            if (token.length() > 0 && totpService.verifyCode(token, user.get().getSecret())) {
                LOGGER.debug("User {} token valid, setting OTPAuthenticatedToken on SecurityContextHolder", user.get().getLogin());

                UsernamePasswordAuthenticationToken authentication =
                        new OTPAuthenticatedToken(user.get().getLogin(), "",
                                Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                status = AuthenticationStatus.AUTHENTICATED;
            } else {
                status = AuthenticationStatus.REQUIRE_TOKEN_CHECK;
            }
        }

        LOGGER.debug("Returning status {}", status);

        return status;
    }

    @DeleteMapping(value = "/logout")
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public static enum AuthenticationStatus {
        AUTHENTICATED, REQUIRE_TOKEN_CHECK, FAILED
    }
}
