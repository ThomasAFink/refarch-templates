package de.muenchen.refarch.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.refarch.MicroServiceApplication;
import de.muenchen.refarch.TestConstants;
import de.muenchen.refarch.config.TestConfig;
import de.muenchen.refarch.language.Language;
import de.muenchen.refarch.language.LanguageService;
import de.muenchen.refarch.link.Link;
import de.muenchen.refarch.link.LinkScope;
import de.muenchen.refarch.link.LinkService;
import de.muenchen.refarch.post.content.PostContentRepository;
import de.muenchen.refarch.post.content.dto.PostContentRequestDTO;
import de.muenchen.refarch.post.content.dto.PostContentResponseDTO;
import de.muenchen.refarch.post.dto.PostRequestDTO;
import de.muenchen.refarch.post.dto.PostResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_NO_SECURITY_PROFILE })
@AutoConfigureMockMvc
@Import(TestConfig.class)
class PostControllerTest {
    // Constants
    private static final String API_POSTS = "/posts";
    private static final String API_POSTS_ID = "/posts/{id}";
    private static final String API_POSTS_CONTENT = "/posts/{postId}/content";
    private static final String API_POSTS_CONTENT_LANGUAGE = "/posts/{postId}/content/{languageId}";
    private static final String TEST_TITLE = "Test Post";
    private static final String TEST_DESCRIPTION = "Test post description";
    private static final String TEST_KEYWORDS = "test, post, keywords";
    private static final String TEST_CONTENT = "Test post content";
    private static final String TEST_LANGUAGE_NAME = "English";
    private static final String TEST_LANGUAGE_ABBREV = "en";
    private static final String TEST_LINK_URL = "/test-post";
    private static final String TEST_LINK_TEXT = "Test Post";

    // Test container
    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    // Spring injected dependencies
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private PostContentRepository postContentRepository;

    @MockitoBean
    private LinkService linkService;

    @MockitoBean
    private LanguageService languageService;

    // Test data
    private UUID postId;
    private UUID linkId;
    private UUID languageId;
    private PostRequestDTO postRequestDTO;
    private PostResponseDTO postResponseDTO;
    private PostContentRequestDTO contentRequestDTO;
    private PostContentResponseDTO contentResponseDTO;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        linkId = UUID.randomUUID();
        languageId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Link link = new Link();
        link.setId(linkId);
        link.setUrl(TEST_LINK_URL);
        link.setName(TEST_LINK_TEXT);
        link.setScope(LinkScope.EXTERNAL);

        final Language language = new Language();
        language.setId(languageId);
        language.setName(TEST_LANGUAGE_NAME);
        language.setAbbreviation(TEST_LANGUAGE_ABBREV);
        language.setFontAwesomeIcon("flag-usa");
        language.setMdiIcon("flag");

        postRequestDTO = new PostRequestDTO(
                linkId,
                TEST_LINK_URL,
                true,
                true);

        postResponseDTO = new PostResponseDTO(
                postId,
                linkId,
                TEST_LINK_URL,
                true,
                true,
                Set.of(),
                now,
                now);

        contentRequestDTO = new PostContentRequestDTO(
                languageId,
                TEST_TITLE,
                TEST_CONTENT,
                TEST_DESCRIPTION,
                TEST_KEYWORDS);

        contentResponseDTO = new PostContentResponseDTO(
                UUID.randomUUID(),
                postId,
                languageId,
                TEST_TITLE,
                TEST_CONTENT,
                TEST_DESCRIPTION,
                TEST_KEYWORDS,
                now,
                now);
    }

    @Test
    void whenGettingAllPosts_shouldReturnList() throws Exception {
        // Arrange
        final List<PostResponseDTO> posts = List.of(postResponseDTO);
        when(postService.findAll()).thenReturn(posts);

        // Act & Assert
        mockMvc.perform(get(API_POSTS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(postId.toString()))
                .andExpect(jsonPath("$[0].linkId").value(linkId.toString()))
                .andExpect(jsonPath("$[0].thumbnail").value(TEST_LINK_URL));

        verify(postService).findAll();
    }

    @Test
    void whenGettingPostById_shouldReturnPost() throws Exception {
        // Arrange
        when(postService.findById(postId)).thenReturn(postResponseDTO);

        // Act & Assert
        mockMvc.perform(get(API_POSTS_ID, postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.linkId").value(linkId.toString()))
                .andExpect(jsonPath("$.thumbnail").value(TEST_LINK_URL));

        verify(postService).findById(postId);
    }

    @Test
    void whenCreatingPost_shouldReturnCreated() throws Exception {
        // Arrange
        when(postService.create(any(PostRequestDTO.class))).thenReturn(postResponseDTO);

        // Act & Assert
        mockMvc.perform(post(API_POSTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.linkId").value(linkId.toString()))
                .andExpect(jsonPath("$.thumbnail").value(TEST_LINK_URL));

        verify(postService).create(any(PostRequestDTO.class));
    }

    @Test
    void whenUpdatingPost_shouldReturnUpdated() throws Exception {
        // Arrange
        when(postService.update(eq(postId), any(PostRequestDTO.class))).thenReturn(postResponseDTO);

        // Act & Assert
        mockMvc.perform(put(API_POSTS_ID, postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.linkId").value(linkId.toString()))
                .andExpect(jsonPath("$.thumbnail").value(TEST_LINK_URL));

        verify(postService).update(eq(postId), any(PostRequestDTO.class));
    }

    @Test
    void whenDeletingPost_shouldSucceed() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(API_POSTS_ID, postId))
                .andExpect(status().isNoContent());

        verify(postService).delete(postId);
    }

    @Test
    void whenGettingAllPostContent_shouldReturnList() throws Exception {
        // Arrange
        final List<PostContentResponseDTO> contents = List.of(contentResponseDTO);
        when(postService.findAllContentByPost(postId)).thenReturn(contents);

        // Act & Assert
        mockMvc.perform(get(API_POSTS_CONTENT, postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].postId").value(postId.toString()))
                .andExpect(jsonPath("$[0].title").value(TEST_TITLE))
                .andExpect(jsonPath("$[0].content").value(TEST_CONTENT));

        verify(postService).findAllContentByPost(postId);
    }

    @Test
    void whenGettingPostContent_shouldReturnContent() throws Exception {
        // Arrange
        when(postService.findContentByPostAndLanguage(postId, languageId)).thenReturn(contentResponseDTO);

        // Act & Assert
        mockMvc.perform(get(API_POSTS_CONTENT_LANGUAGE, postId, languageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT));

        verify(postService).findContentByPostAndLanguage(postId, languageId);
    }

    @Test
    void whenCreatingPostContent_shouldReturnCreated() throws Exception {
        // Arrange
        when(postService.createContent(eq(postId), any(PostContentRequestDTO.class)))
                .thenReturn(contentResponseDTO);

        // Act & Assert
        mockMvc.perform(post(API_POSTS_CONTENT, postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT));

        verify(postService).createContent(eq(postId), any(PostContentRequestDTO.class));
    }

    @Test
    void whenUpdatingPostContent_shouldReturnUpdated() throws Exception {
        // Arrange
        when(postService.updateContent(eq(postId), eq(languageId), any(PostContentRequestDTO.class)))
                .thenReturn(contentResponseDTO);

        // Act & Assert
        mockMvc.perform(put(API_POSTS_CONTENT_LANGUAGE, postId, languageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT));

        verify(postService).updateContent(eq(postId), eq(languageId), any(PostContentRequestDTO.class));
    }

    @Test
    void whenDeletingPostContent_shouldSucceed() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(API_POSTS_CONTENT_LANGUAGE, postId, languageId))
                .andExpect(status().isNoContent());

        verify(postService).deleteContent(postId, languageId);
    }
}
