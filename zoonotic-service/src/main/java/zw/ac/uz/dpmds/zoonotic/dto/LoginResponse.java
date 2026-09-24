package zw.ac.uz.dpmds.zoonotic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Carries a development login access token.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class LoginResponse {

    @Schema(description = "Signed JWT access token")
    private String accessToken;
    @Schema(description = "Authentication scheme", example = "Bearer")
    private String tokenType;
    @Schema(description = "Token lifetime in seconds", example = "900")
    private long expiresIn;

    /**
     * Creates a login response.
     *
     * @param accessToken the signed access token
     * @param tokenType the token type
     * @param expiresIn the token lifetime in seconds
     */
    public LoginResponse(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    /**
     * Returns the access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Returns the token type.
     *
     * @return the token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Returns the token lifetime.
     *
     * @return the lifetime in seconds
     */
    public long getExpiresIn() {
        return expiresIn;
    }
}
