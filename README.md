# Spring Security TOTP implementation

The aim of this small project is to supply a flexible time based one time password (TOTP) implementation for Spring Security. There are a number of TOTP clients for your phone, one of the most popular being [Google Authenticator](https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator) whilst another is [FreeOTP](https://freeotp.github.io/).

A number of implementations exist in the open source world, but I couldn't find anything that easily integrated with Spring Security and the ones I did find included security concerns, such as passing passwords in URLs. That said, I am grateful to author of the google-auth-sample project, that I've used as a basis for some functionality within this implementation (primarily the UI code - which I'd like to re-write when I get the chance).

I'd welcome your feedback!

## The user store

A user store is required for the purposes of persisting and retrieving a username and password. The `UserDatabase` interface provides the relevant methods and out of the box, if no implementation is found within the application scope, an in memory (ie h2 database) implementation is deployed. This implementation persists the database to disc via the `totp.database` files, but you can configure this via the h2.properties file. That said, the h2 project does not recommend this approach for production systems.

Hence anyone can provide an implementation of `UserDatabase` that plugs into their own user repository.

## Enabling OTP

The `@EnableOTP` annotation will configure a TOTP implementation via the `OTPWebSecurityConfiguration` class. The `SecurityOTPApplication` class runs the example application, described later

# Configuration

The following properties must be defined:

* *spring.security.otp.serviceName*: The name of the service that will be encoded into the QR image scanned by you phone, hence will be displayed in the TOTP client.

# URLs in use through this implementation

The `OTPWebSecurityConfiguration` class defines an implementation of `WebSecurityConfigurerAdapter`, which protects `/*` but allows access to the following URLs:

1. /otp/index.html
2. /otp/login.html
3. /otp/register.html
4. /otp/scripts.js
5. /otp/style.css
6. /otp/authenticate/** (the `AuthenticationController` class)
7. /otp/registration/** (the `RegistrationController` class)

You can define your own user interface by implementing an implementation of `WebSecurityConfiguration` and exposing the relevant URLs.

## Running standalone example

The `SecurityOTPApplication` class will run a Spring Boot application to demonstrate how the TOTP implementation functions with the h2 database:

1. Run `SecurityOTPApplication`
2. Attempt to access http://localhost:8080/otp/secured.html
3. You will be presented with a login page. Follow the [register](http://localhost:8080/otp/register.html) link and register your client.
4. Attempt to access http://localhost:8080/otp/secured.html and login.
