package de.muenchen.refarch.configuration;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * The central class for configuration of all security aspects.
 * Automatically used when not running with profile `no-security`.
 * Configures all endpoints to require authentication via access token.
 * (except the Spring Boot Actuator endpoints)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!no-security")
public class SecurityConfiguration {

    private final SecurityProperties securityProperties;

    @Autowired
    public SecurityConfiguration(final SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/**")).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            final String jwkSetUri = securityProperties.getUserInfoUri().replace("/userinfo", "/certs");

            final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

            // Configure token validation
            final OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefault();
            final Set<String> requiredClaims = new HashSet<>(Set.of("sub", "iss", "exp", "iat", "aud"));
            final OAuth2TokenValidator<Jwt> claimsValidator = token -> {
                if (token.getClaims().keySet().containsAll(requiredClaims)) {
                    return OAuth2TokenValidatorResult.success();
                }
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Missing required claims", null));
            };

            final OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(defaultValidators, claimsValidator);
            jwtDecoder.setJwtValidator(validator);

            return jwtDecoder;
        } catch (Exception e) {
            throw new SecurityConfigurationException("Failed to configure JWT decoder", e);
        }
    }
}

class SecurityConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SecurityConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
