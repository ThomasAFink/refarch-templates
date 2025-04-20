package de.muenchen.refarch.config;

import de.muenchen.refarch.user.User;
import java.util.UUID;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
    public User testUser() {
        final User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
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
}
