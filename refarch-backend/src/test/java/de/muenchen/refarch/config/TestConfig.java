package de.muenchen.refarch.config;

import de.muenchen.refarch.user.User;
import java.lang.reflect.Field;
import java.util.UUID;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/* default */ @TestConfiguration
@EnableWebMvc
@SuppressWarnings(
    {
            "PMD.UnitTestShouldUseTestAnnotation",
            "PMD.TestClassWithoutTestCases",
            "PMD.CommentDefaultAccessModifier"
    }
)
public class TestConfig {

    private static final UUID TEST_USER_ID = UUID.randomUUID();

    @Bean
    @Primary
    @Profile("!no-security")
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll());
        return http.build();
    }

    @Bean
    @Primary
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    public User testUser() {
        final User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setTitle("Test Title");
        user.setAffiliation("Test Affiliation");

        // We need to use reflection to set the password field as it's not accessible
        // This is necessary for testing purposes only
        try {
            final Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true); // Required for testing, but should be avoided in production code
            passwordField.set(user, "testpassword");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set password field", e);
        }

        return user;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        final UserDetails user = org.springframework.security.core.userdetails.User.builder()
                .username("test")
                .password("{noop}test")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
