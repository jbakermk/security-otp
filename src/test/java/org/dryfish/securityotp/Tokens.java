package org.dryfish.securityotp;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Hex;
import org.dryfish.securityotp.service.TOTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class Tokens {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tokens.class);

    public static final String SECRET = "password123";

    public static String generateTotpBySecret(Instant instant) throws EncoderException {
        // Getting current timestamp representing 30 seconds time frame
        long timeFrame = (instant.toEpochMilli() / 1000L) / 30;
        LOGGER.info("Instant {} generates time frame {}", instant, timeFrame);

        // Encoding time frame value to HEX string - requred by TOTP generator which is used here.
        String timeEncoded = Long.toHexString(timeFrame);

        // Encoding given secret string to HEX string - requred by TOTP generator which is used here.
        char[] secretEncoded = (char[]) new Hex().encode(SECRET);

        // Generating TOTP by given time and secret - using TOTP algorithm implementation provided by IETF.
        return TOTP.generateTOTP(String.copyValueOf(secretEncoded), timeEncoded, "6");
    }
}