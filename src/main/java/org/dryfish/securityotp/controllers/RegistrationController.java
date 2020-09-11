package org.dryfish.securityotp.controllers;

import net.glxn.qrgen.core.exception.QRGenerationException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.codec.binary.Base32;
import org.dryfish.securityotp.OTPConfiguration;
import org.dryfish.securityotp.database.User;
import org.dryfish.securityotp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * This controller deals with registering clients against the @{link UserDatabase} implementation via the
 * {}link UserService}.
 *
 * @author John Baker
 */
@ResponseBody
@RequestMapping(value = "/otp/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class RegistrationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private OTPConfiguration configuration;
    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        LOGGER.debug("Created RegistrationController exposing /otp/registration");
    }

    private String generateRandomString() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @PostMapping(value = "/{login}")
    public ResponseEntity<?> register(@PathVariable @NotBlank String login, @RequestBody Map<String, Object> data) {
        String password= data.getOrDefault("password", "").toString();

        if (password.length()==0) {
            LOGGER.debug("No password supplied for user {}", login);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // TODO: Some kind of rate limiting to prevent enumeration?
        if (userService.doesUserExist(login)) {
            LOGGER.debug("User {} already exists", login);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            String secret = generateRandomString();
            // QR code generator does not recognise base32 padding (=) so remove.
            String encodedSecret = new Base32().encodeAsString(
                    secret.getBytes("utf-8")).replace("=", "");
            String text = String.format("otpauth://totp/%s?issuer=%s&secret=%s",
                    URLEncoder.encode(String.format("%s@%s", login, configuration.getIssuerDomain()), "UTF-8"),
                    configuration.getServiceName(),
                    encodedSecret);
            LOGGER.debug("Encoding string to QR code: {}", text);
            byte[] image = QRCode.from(text).to(ImageType.PNG).withSize(250, 250).stream().toByteArray();

            // QR code generator can generate exceptions so do this as late as possible
            User user = userService.register(login, password, secret);
            LOGGER.debug("Registered user {}", login);

            return new ResponseEntity<>("data:image/png;base64," + Base64.getEncoder().encodeToString(image),
                    HttpStatus.OK);
        } catch (QRGenerationException | UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}