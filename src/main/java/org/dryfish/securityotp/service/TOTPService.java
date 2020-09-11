package org.dryfish.securityotp.service;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TOTPService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TOTPService.class);

    public boolean verifyCode(String totpCode, String secret) {
        try {
            return generateTotpBySecret(secret).equals(totpCode);
        } catch (EncoderException e) {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
    }

    protected String generateTotpBySecret(String secret) throws EncoderException {
        // Getting current timestamp representing 30 seconds time frame
        Instant instant= LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        long timeFrame = instant.toEpochMilli() / 1000L / 30;
        LOGGER.debug("Instant {} generates time frame {}", instant, timeFrame);

        // Encoding time frame value to HEX string
        String timeEncoded = Long.toHexString(timeFrame);

        // Encoding given secret string to HEX string
        char[] secretEncoded = (char[]) new Hex().encode(secret);

        // Generating TOTP by given time and secret - using TOTP algorithm implementation provided by IETF.
        return TOTP.generateTOTP(String.copyValueOf(secretEncoded), timeEncoded, "6");
    }
}
