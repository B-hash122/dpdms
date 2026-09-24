package zw.ac.uz.dpmds.zoonotic.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Converts JWT role claims into Spring Security role authorities.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class JwtRoleAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthenticationConverter delegate;

    /**
     * Creates a converter for the roles claim.
     */
    public JwtRoleAuthenticationConverter() {
        delegate = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        delegate.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
    }

    /**
     * Converts a validated JWT into an authenticated token.
     *
     * @param jwt the validated JWT
     * @return the authenticated token
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return delegate.convert(jwt);
    }
}
