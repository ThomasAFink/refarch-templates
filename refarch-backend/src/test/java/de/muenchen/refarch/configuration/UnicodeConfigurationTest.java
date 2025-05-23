package de.muenchen.refarch.configuration;

import static de.muenchen.refarch.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.refarch.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.refarch.MicroServiceApplication;
import de.muenchen.refarch.TestConstants;
import de.muenchen.refarch.config.TestConfig;
import de.muenchen.refarch.link.Link;
import de.muenchen.refarch.link.LinkRepository;
import de.muenchen.refarch.link.LinkScope;
import de.muenchen.refarch.link.dto.LinkRequestDTO;
import de.muenchen.refarch.link.dto.LinkResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@AutoConfigureMockMvc
@Import(TestConfig.class)
class UnicodeConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String LINK_ENDPOINT_URL = "/links";

    /**
     * Decomposed string:
     * String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String NAME_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";
    private static final String NAME_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LinkRepository linkRepository;

    @Test
    void testForNfcNormalization() {
        // Create a link with decomposed unicode
        final LinkRequestDTO request = new LinkRequestDTO(
                "http://example.com",
                NAME_ATTRIBUTE_DECOMPOSED,
                null,
                null,
                "test",
                LinkScope.INTERNAL);

        // Send request and get response
        final LinkResponseDTO response = testRestTemplate.postForObject(
                LINK_ENDPOINT_URL,
                request,
                LinkResponseDTO.class);

        assertNotNull(response);
        assertNotNull(response.id());

        // Verify that the name was normalized to composed form
        final Link savedLink = linkRepository.findById(response.id()).orElseThrow();
        assertEquals(NAME_ATTRIBUTE_COMPOSED, savedLink.getName());
    }
}
