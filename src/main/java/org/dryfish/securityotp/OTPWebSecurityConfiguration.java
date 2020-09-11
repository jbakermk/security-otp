package org.dryfish.securityotp;

import org.dryfish.securityotp.autoconfigure.EnableOTP;
import org.dryfish.securityotp.controllers.AuthenticationController;
import org.dryfish.securityotp.controllers.RegistrationController;
import org.dryfish.securityotp.database.InMemoryUserDatabase;
import org.dryfish.securityotp.database.UserDatabase;
import org.dryfish.securityotp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

/**
 * {@link WebSecurityConfigurerAdapter} to add OTP support with default configuration,
 * which allows some /otp paths and sets the login page to /otp/login.html, whilst securing every other path.
 * Specifically, the RestControllers for registration and authentication are exposed through this configuration
 * to avoid configuration through component-scan when @EnableOTP has not been specified.
 *
 * It is imported by {@link EnableOTP}.
 *
 * @author John Baker
 */
public class OTPWebSecurityConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPWebSecurityConfiguration.class);

    @Autowired private ApplicationContext sc;

    @Bean
    private UserService createUserService() {
        return new UserService();
    }

    @Bean
    private AuthenticationController createAuthenticationController() {
        return new AuthenticationController();
    }

    @Bean
    private RegistrationController createRegistrationController() {
        return new RegistrationController();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(UserDatabase.class)
    public UserDatabase authorizedClientService() {
        return new InMemoryUserDatabase();
    }

    @Bean
    public WebSecurityConfigurerAdapter createOTPWebSecurityConfigurerAdapter() {
        return new OTPWebSecurityConfigurerAdapter();
    }

    protected class OTPWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private final Logger LOGGER = LoggerFactory.getLogger(OTPWebSecurityConfigurerAdapter.class);

        @Autowired
        private OTPConfiguration configuration;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            LOGGER.info("Configuring Spring Security for OTP");
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/otp/index.html", "/otp/login.html", "/otp/register.html", "/otp/scripts.js",
                            "/otp/style.css", "/otp/authenticate/**", "/otp/registration/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/otp/login.html")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();
        }
    }
}
