package zw.ac.uz.dpmds.zoonotic.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Carries development login credentials to the local authentication endpoint.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class LoginRequest {

    @Schema(description = "Development username", example = "recorder")
    @NotBlank
    private String username;

    @Schema(description = "Development password", example = "not-a-real-password")
    @NotBlank
    private String password;

    /**
     * Returns the submitted username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the submitted username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the submitted password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the submitted password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
