package zw.ac.uz.dpmds.zoonotic;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import zw.ac.uz.dpmds.zoonotic.security.HazardTypes;
import zw.ac.uz.dpmds.zoonotic.security.JwtService;
import zw.ac.uz.dpmds.zoonotic.security.SecurityRoles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies development JWT scope claims.
 *
 * @author DPDMS
 * @version 1.0
 */
class JwtServiceTest {

    /**
     * Ensures recorder tokens include ward and hazard scope.
     */
    @Test
    void recorderTokenContainsScopeClaims() {
        JwtEncoder encoder = mock(JwtEncoder.class);
        Jwt token = mock(Jwt.class);
        when(token.getTokenValue()).thenReturn("token");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenAnswer(
                invocation -> {
                    JwtEncoderParameters parameters = invocation.getArgument(0);
                    assertEquals(
                            List.of(SecurityRoles.WARD_RECORDER),
                            parameters.getClaims().getClaim("roles"));
                    assertEquals(
                            "recorder",
                            parameters.getClaims().getSubject());
                    org.junit.jupiter.api.Assertions.assertNotNull(
                            parameters.getClaims().getIssuedAt());
                    org.junit.jupiter.api.Assertions.assertNotNull(
                            parameters.getClaims().getExpiresAt());
                    assertEquals("Ward A", parameters.getClaims().getClaim("ward"));
                    assertEquals(
                            HazardTypes.ZOONOTIC,
                            parameters.getClaims().getClaim("hazard"));
                    return token;
                });

        JwtService service = new JwtService(encoder, 900, "Ward A");
        service.generateToken(new UsernamePasswordAuthenticationToken(
                "recorder",
                null,
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + SecurityRoles.WARD_RECORDER))));
    }

    /**
     * Ensures supervisor tokens carry hazard scope but no recorder ward.
     */
    @Test
    void supervisorTokenContainsHazardWithoutWard() {
        JwtEncoder encoder = mock(JwtEncoder.class);
        Jwt token = mock(Jwt.class);
        when(token.getTokenValue()).thenReturn("token");
        when(encoder.encode(any(JwtEncoderParameters.class))).thenAnswer(
                invocation -> {
                    JwtEncoderParameters parameters = invocation.getArgument(0);
                    assertEquals(
                            HazardTypes.ZOONOTIC,
                            parameters.getClaims().getClaim("hazard"));
                    assertEquals(
                            "supervisor",
                            parameters.getClaims().getSubject());
                    org.junit.jupiter.api.Assertions.assertNotNull(
                            parameters.getClaims().getIssuedAt());
                    org.junit.jupiter.api.Assertions.assertNotNull(
                            parameters.getClaims().getExpiresAt());
                    assertNull(parameters.getClaims().getClaim("ward"));
                    return token;
                });

        JwtService service = new JwtService(encoder, 900, "Ward A");
        service.generateToken(new UsernamePasswordAuthenticationToken(
                "supervisor",
                null,
                List.of(new SimpleGrantedAuthority(
                        "ROLE_" + SecurityRoles.PROVINCIAL_SUPERVISOR))));
    }
}
