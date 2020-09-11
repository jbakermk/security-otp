package org.dryfish.securityotp.service;

import org.apache.commons.codec.EncoderException;
import org.dryfish.securityotp.Tokens;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Test the TOTP generator to ensure it generates two of the same tokens within a 30 second period,
 * and different tokens from two different periods.
 */
@SpringBootTest(properties = { "spring.jpa.hibernate.ddl-auto=create" })
public class TestTOTPService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestTOTPService.class);

    @Autowired
    private TOTPService totpService;

    // Two different tokens from times outside of a 30 second period.
    @Test
    public void twoTimePeriods() throws EncoderException {
        LocalDateTime localDate = LocalDateTime.now();
        String current= Tokens.generateTotpBySecret(localDate.withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
        String expired= Tokens.generateTotpBySecret(localDate.withSecond(30).atZone(ZoneId.systemDefault()).toInstant());
        Assertions.assertNotEquals(expired, current);
    }

    // Two tokens within the bounds of a 30 second period
    @Test
    public void oneTimePeriod() throws EncoderException {
        LocalDateTime localDate = LocalDateTime.now();
        String current= Tokens.generateTotpBySecret(localDate.withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
        String later= Tokens.generateTotpBySecret(localDate.withSecond(29).atZone(ZoneId.systemDefault()).toInstant());
        Assertions.assertEquals(later, current);
    }
}