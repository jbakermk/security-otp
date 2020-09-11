package org.dryfish.securityotp;

import org.dryfish.securityotp.autoconfigure.EnableOTP;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableOTP
/* When running standalone, use a persistable h2 database */
@PropertySource("classpath:h2.properties")
public class SecurityOTPApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityOTPApplication.class, args);
	}

}
