package zw.ac.uz.dpmds.zoonotic.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import zw.ac.uz.dpmds.zoonotic.security.HazardTypes;

/**
 * Creates locally signed development JWT access tokens.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;
    private final String recorderWard;

    /**
     * Creates a JWT service with the configured encoder and lifetime.
     *
     * @param jwtEncoder the JWT encoder
     * @param expirationSeconds the access-token lifetime
     */
    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds,
            @Value("${security.dev.recorder-ward}") String recorderWard) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
        this.recorderWard = recorderWard;
    }

    /**
     * Creates a signed access token for an authenticated development user.
     *
     * @param authentication the successful authentication
     * @return the signed JWT
     */
    public String generateToken(Authentication authentication) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .toList();
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .claim("hazard", HazardTypes.ZOONOTIC);
        if (roles.contains(SecurityRoles.WARD_RECORDER)) {
            claimsBuilder.claim("ward", recorderWard);
        }
        JwtClaimsSet claims = claimsBuilder.build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    /**
     * Returns the configured access-token lifetime.
     *
     * @return the lifetime in seconds
     */
    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
