package zw.ac.uz.dpmds.zoonotic.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import zw.ac.uz.dpmds.zoonotic.dto.LoginRequest;
import zw.ac.uz.dpmds.zoonotic.dto.LoginResponse;
import zw.ac.uz.dpmds.zoonotic.security.JwtService;

/**
 * Provides the temporary local development login endpoint.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "JWT authentication operations")
public class AuthenticationController {

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Creates the local authentication controller.
     *
     * @param authenticationManager the Spring authentication manager
     * @param jwtService the JWT service
     */
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a development user and issues a signed JWT.
     *
     * @param request the validated login request
     * @return the access-token response
     */
    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a development user",
            description = "Returns a signed JWT containing the user's role and "
                    + "authorized zoonotic scope.")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername(),
                        request.getPassword()));
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponse(
                token,
                BEARER_TOKEN_TYPE,
                jwtService.getExpirationSeconds()));
    }
}
