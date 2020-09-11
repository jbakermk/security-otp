package org.dryfish.securityotp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * THe OTP configuration properties, currently just the service name.
 *
 * @author John Baker
 */
@ConfigurationProperties(prefix = "spring.security.otp")
@Validated
public class OTPConfiguration implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPConfiguration.class);

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @NotNull
    private String serviceName;

    @NotNull
    private String issuerDomain;

    private boolean enableUserPasswordAuthentication= true;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Configuration: serviceName={} issuerDomain={} enableUserPasswordAuthentication={}",
                serviceName, issuerDomain, enableUserPasswordAuthentication);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIssuerDomain() {
        return issuerDomain;
    }

    public void setIssuerDomain(String issuerDomain) {
        this.issuerDomain = issuerDomain;
    }

    public boolean isEnableUserPasswordAuthentication() {
        return enableUserPasswordAuthentication;
    }

    public void setEnableUserPasswordAuthentication(boolean enableUserPasswordAuthentication) {
        this.enableUserPasswordAuthentication = enableUserPasswordAuthentication;
    }


}