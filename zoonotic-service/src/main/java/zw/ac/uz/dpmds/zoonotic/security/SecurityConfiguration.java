package zw.ac.uz.dpmds.zoonotic.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Configures temporary local JWT authentication and endpoint RBAC.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String RECORDER_USERNAME = "recorder";
    private static final String SUPERVISOR_USERNAME = "supervisor";

    private final String jwtSecret;
    private final long jwtExpirationSeconds;
    private final String recorderDevelopmentPassword;
    private final String supervisorDevelopmentPassword;
    private final String recorderWard;

    /**
     * Creates the local security configuration from external configuration.
     *
     * @param jwtSecret the externally supplied JWT signing secret
     * @param jwtExpirationSeconds the access-token lifetime in seconds
     * @param recorderDevelopmentPassword the local recorder password
     * @param supervisorDevelopmentPassword the local supervisor password
     * @param recorderWard the configured recorder ward
     */
    public SecurityConfiguration(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.expiration-seconds}") long jwtExpirationSeconds,
            @Value("${security.dev.recorder-password}") String recorderDevelopmentPassword,
            @Value("${security.dev.supervisor-password}") String supervisorDevelopmentPassword,
            @Value("${security.dev.recorder-ward}") String recorderWard) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationSeconds = jwtExpirationSeconds;
        this.recorderDevelopmentPassword = recorderDevelopmentPassword;
        this.supervisorDevelopmentPassword = supervisorDevelopmentPassword;
        this.recorderWard = recorderWard;
    }

    /**
     * Creates the password encoder used by the development users.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates temporary in-memory users for local development only.
     *
     * @param passwordEncoder the password encoder
     * @return the in-memory user service
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails recorder = User.withUsername(RECORDER_USERNAME)
                .password(passwordEncoder.encode(recorderDevelopmentPassword))
                .roles(SecurityRoles.WARD_RECORDER)
                .build();
        UserDetails supervisor = User.withUsername(SUPERVISOR_USERNAME)
                .password(passwordEncoder.encode(supervisorDevelopmentPassword))
                .roles(SecurityRoles.PROVINCIAL_SUPERVISOR)
                .build();
        return new InMemoryUserDetailsManager(recorder, supervisor);
    }

    /**
     * Exposes the authentication manager used by the development login endpoint.
     *
     * @param configuration the Spring Security authentication configuration
     * @return the authentication manager
     * @throws Exception when the manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Creates the local JWT encoder.
     *
     * @return the JWT encoder
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
    }

    /**
     * Creates the local JWT decoder used for bearer-token validation.
     *
     * @return the JWT decoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Provides the signing key derived from the externally supplied secret.
     *
     * @return the HMAC signing key
     */
    private SecretKey secretKey() {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    /**
     * Provides explicit CORS support for the React development frontend.
     *
     * @return the configured CORS source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures stateless JWT bearer-token endpoint authorization.
     *
     * @param http the HTTP security builder
     * @return the configured security filter chain
     * @throws Exception when security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                new JwtRoleAuthenticationConverter())))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/auth/login")
                        .permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/zoonotic-incidents/*/audit")
                        .hasRole(SecurityRoles.PROVINCIAL_SUPERVISOR)
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/zoonotic-incidents/*/approve",
                                "/api/zoonotic-incidents/*/reject",
                                "/api/zoonotic-incidents/*/request-correction")
                        .hasRole(SecurityRoles.PROVINCIAL_SUPERVISOR)
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/zoonotic-incidents/*/resubmit")
                        .hasRole(SecurityRoles.WARD_RECORDER)
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/zoonotic-incidents")
                        .hasRole(SecurityRoles.WARD_RECORDER)
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/zoonotic-incidents/*")
                        .hasRole(SecurityRoles.WARD_RECORDER)
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/zoonotic-incidents",
                                "/api/zoonotic-incidents/*")
                        .hasAnyRole(
                                SecurityRoles.WARD_RECORDER,
                                SecurityRoles.PROVINCIAL_SUPERVISOR)
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/zoonotic-incidents/*")
                        .hasRole(SecurityRoles.WARD_RECORDER)
                        .anyRequest().authenticated());
        return http.build();
    }
}
