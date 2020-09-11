package org.dryfish.securityotp;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class OTPAuthenticatedToken extends UsernamePasswordAuthenticationToken {

    public OTPAuthenticatedToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}