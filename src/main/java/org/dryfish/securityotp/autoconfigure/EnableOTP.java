package org.dryfish.securityotp.autoconfigure;

import org.dryfish.securityotp.OTPConfiguration;
import org.dryfish.securityotp.OTPWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link EnableOTP Auto-configuration} for TOTP support.
 *
 * @author John Baker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configuration
@AutoConfigureBefore(SecurityAutoConfiguration.class)
@ConditionalOnClass({ EnableWebSecurity.class })
@EnableConfigurationProperties(OTPConfiguration.class)
@Import({ OTPWebSecurityConfiguration.class })
@EnableRetry
public @interface EnableOTP {}