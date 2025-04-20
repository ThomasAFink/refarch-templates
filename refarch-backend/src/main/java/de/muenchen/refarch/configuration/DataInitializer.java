package de.muenchen.refarch.configuration;

import de.muenchen.refarch.homepage.Homepage;
import de.muenchen.refarch.homepage.content.HomepageContent;
import de.muenchen.refarch.homepage.HomepageRepository;
import de.muenchen.refarch.language.Language;
import de.muenchen.refarch.language.LanguageRepository;
import de.muenchen.refarch.link.Link;
import de.muenchen.refarch.link.LinkRepository;
import de.muenchen.refarch.link.LinkScope;
import de.muenchen.refarch.page.Page;
import de.muenchen.refarch.page.PageRepository;
import de.muenchen.refarch.page.content.PageContent;
import de.muenchen.refarch.post.Post;
import de.muenchen.refarch.post.PostRepository;
import de.muenchen.refarch.post.content.PostContent;
import de.muenchen.refarch.role.Role;
import de.muenchen.refarch.role.RoleRepository;
import de.muenchen.refarch.user.User;
import de.muenchen.refarch.user.UserRepository;
import de.muenchen.refarch.user.bio.UserBio;
import de.muenchen.refarch.user.bio.UserBioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final String[] DEFAULT_ROLES = {
            "ROLE_ADMIN",
            "ROLE_USER",
            "ROLE_EDITOR",
            "ROLE_MODERATOR"
    };

    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HomepageRepository homepageRepository;
    private final LinkRepository linkRepository;
    private final UserBioRepository userBioRepository;
    private final PageRepository pageRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void run(final String... args) {
        log.info("Checking database initialization status...");

        // Initialize languages if needed
        final Language english = getOrCreateLanguage("English", "en", "fa-flag-usa", "mdi-flag-usa");
        final Language german = getOrCreateLanguage("Deutsch", "de", "fa-flag", "mdi-flag");

        // Initialize roles if needed
        if (roleRepository.count() == 0) {
            createDefaultRoles();
        }

        // Get required roles for admin user
        final Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found after initialization"));
        final Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found after initialization"));

        // Initialize admin user if needed
        if (userRepository.count() == 0) {
            createUserBio(createAdminUser(adminRole, userRole), english);
        }

        // Initialize homepage if needed
        if (homepageRepository.count() == 0) {
            createHomepage(english, german);
        }

        // Initialize sample page if needed
        if (pageRepository.count() == 0) {
            createSamplePage(english, german);
        }

        // Initialize sample blog post if needed
        if (postRepository.count() == 0) {
            createSamplePost(english, german);
        }

        log.info("Database initialization check completed");
    }

    private void createDefaultRoles() {
        log.info("Creating default roles: {}", Arrays.toString(DEFAULT_ROLES));
        Arrays.stream(DEFAULT_ROLES).forEach(roleName -> {
            final Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        });
    }

    private Language getOrCreateLanguage(String name, String abbreviation, String fontAwesomeIcon, String mdiIcon) {
        return languageRepository.findAll().stream()
                .filter(lang -> abbreviation.equals(lang.getAbbreviation()))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Creating language: {}", name);
                    final Language language = new Language();
                    language.setName(name);
                    language.setAbbreviation(abbreviation);
                    language.setFontAwesomeIcon(fontAwesomeIcon);
                    language.setMdiIcon(mdiIcon);
                    return languageRepository.save(language);
                });
    }

    private User createAdminUser(final Role adminRole, final Role userRole) {
        log.info("Creating admin user");
        final User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("admin")); // Change in production!
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setTitle("System Administrator");
        adminUser.setAffiliation("RefArch CMS");
        adminUser.setRoles(Set.of(adminRole, userRole));
        return userRepository.save(adminUser);
    }

    private void createUserBio(final User user, final Language language) {
        log.info("Creating bio for user: {}", user.getUsername());
        final UserBio bio = new UserBio();
        bio.setUser(user);
        bio.setLanguage(language);
        bio.setBio("""
                As a system administrator for RefArch CMS, I am responsible for maintaining and improving
                the content management system. With extensive experience in web development and system
                administration, I ensure that our platform remains secure, efficient, and user-friendly.

                My focus areas include:
                - System security and performance optimization
                - User management and access control
                - Content workflow improvements
                - Technical documentation
                """);
        userBioRepository.save(bio);
    }

    private void createSamplePage(final Language english, final Language german) {
        log.info("Creating sample page");

        // Create page link
        final Link pageLink = new Link();
        pageLink.setName("About Us");
        pageLink.setUrl("/about");
        pageLink.setType("navigation");
        pageLink.setScope(LinkScope.INTERNAL);
        pageLink.setFontAwesomeIcon("fa-info-circle");
        pageLink.setMdiIcon("mdi-information");
        linkRepository.save(pageLink);

        // Create page
        final Page page = new Page();
        page.setLink(pageLink);
        page.setCommentsEnabled(true);
        page.setPublished(true);

        // Create English page content
        final PageContent englishContent = new PageContent();
        englishContent.setLanguage(english);
        englishContent.setTitle("About RefArch CMS");
        englishContent.setContent("""
                # About RefArch CMS

                RefArch CMS is a modern, flexible content management system built with Spring Boot.
                Our platform provides a robust foundation for managing digital content with ease
                and efficiency.

                ## Our Mission

                We aim to simplify content management while maintaining high standards of security,
                performance, and user experience. Our system is designed to be both powerful and
                intuitive, serving the needs of content creators and technical administrators alike.

                ## Key Features

                - Multi-language support for global reach
                - Role-based access control for security
                - Modern API design for integration
                - Flexible content management
                - Real-time collaboration tools
                """);
        englishContent.setShortDescription("Learn about RefArch CMS, our mission, and our commitment to excellence in content management.");
        englishContent.setKeywords("cms, content management, spring boot, java, web application");

        // Create German page content
        final PageContent germanContent = new PageContent();
        germanContent.setLanguage(german);
        germanContent.setTitle("Über RefArch CMS");
        germanContent.setContent("""
                # Über RefArch CMS

                RefArch CMS ist ein modernes, flexibles Content-Management-System, das mit Spring Boot entwickelt wurde.
                Unsere Plattform bietet eine robuste Grundlage für die einfache und effiziente Verwaltung digitaler Inhalte.

                ## Unsere Mission

                Wir streben danach, das Content-Management zu vereinfachen und gleichzeitig hohe Standards in Bezug auf Sicherheit,
                Leistung und Benutzerfreundlichkeit zu gewährleisten. Unser System ist sowohl leistungsstark als auch
                intuitiv und erfüllt die Bedürfnisse von Content-Erstellern und technischen Administratoren gleichermaßen.

                ## Hauptmerkmale

                - Mehrsprachige Unterstützung für globale Reichweite
                - Rollenbasierte Zugriffskontrolle für Sicherheit
                - Modernes API-Design für Integration
                - Flexible Content-Verwaltung
                - Echtzeit-Kollaborationstools
                """);
        germanContent.setShortDescription("Erfahren Sie mehr über RefArch CMS, unsere Mission und unser Engagement für Exzellenz im Content-Management.");
        germanContent.setKeywords("cms, content management, spring boot, java, webanwendung");

        page.addContent(englishContent);
        page.addContent(germanContent);
        pageRepository.save(page);
    }

    private void createSamplePost(final Language english, final Language german) {
        log.info("Creating sample blog post");

        // Create post link
        final Link postLink = new Link();
        postLink.setName("Welcome to RefArch CMS");
        postLink.setUrl("/welcome-to-refarch-cms-blog");
        postLink.setType("post");
        postLink.setScope(LinkScope.INTERNAL);
        postLink.setFontAwesomeIcon("fa-newspaper");
        postLink.setMdiIcon("mdi-post");
        linkRepository.save(postLink);

        // Create post
        final Post post = new Post();
        post.setLink(postLink);
        post.setCommentsEnabled(true);
        post.setPublished(true);

        // Create English post content
        final PostContent englishContent = new PostContent();
        englishContent.setLanguage(english);
        englishContent.setTitle("Welcome to RefArch CMS Blog");
        englishContent.setContent("""
                # Welcome to Our Blog

                We're excited to launch the RefArch CMS blog! This space will serve as a hub for
                announcements, tutorials, best practices, and insights into content management.

                ## What to Expect

                Our blog will cover various topics, including:
                - Product updates and new features
                - Technical tutorials and guides
                - Content management best practices
                - Case studies and success stories
                - Community highlights

                ## Getting Started
                """);
        englishContent.setShortDescription("Welcome to the official RefArch CMS blog! Stay tuned for updates, tutorials, and best practices.");
        englishContent.setKeywords("blog, welcome, cms, content management, announcements");

        // Create German post content
        final PostContent germanContent = new PostContent();
        germanContent.setLanguage(german);
        germanContent.setTitle("Willkommen im RefArch CMS Blog");
        germanContent.setContent("""
                # Willkommen in unserem Blog

                Wir freuen uns, den RefArch CMS Blog zu starten! Dieser Raum wird als Zentrum für
                Ankündigungen, Tutorials, Best Practices und Einblicke in das Content-Management dienen.

                ## Was Sie erwartet

                Unser Blog wird verschiedene Themen abdecken, darunter:
                - Produktaktualisierungen und neue Funktionen
                - Technische Tutorials und Anleitungen
                - Best Practices im Content-Management
                - Fallstudien und Erfolgsgeschichten
                - Community-Highlights

                ## Erste Schritte
                """);
        germanContent.setShortDescription("Willkommen im offiziellen RefArch CMS Blog! Bleiben Sie dran für Updates, Tutorials und Best Practices.");
        germanContent.setKeywords("blog, willkommen, cms, content management, ankündigungen");

        post.addContent(englishContent);
        post.addContent(germanContent);
        postRepository.save(post);
    }

    private void createHomepage(final Language english, final Language german) {
        log.info("Creating homepage");

        // Create homepage link
        final Link homepageLink = new Link();
        homepageLink.setName("Homepage");
        homepageLink.setUrl("/");
        homepageLink.setType("homepage");
        homepageLink.setScope(LinkScope.INTERNAL);
        homepageLink.setFontAwesomeIcon("fa-home");
        homepageLink.setMdiIcon("mdi-home");
        linkRepository.save(homepageLink);

        // Create homepage
        final Homepage homepage = new Homepage();
        homepage.setLink(homepageLink);
        homepage.setThumbnail("homepage-thumbnail.jpg");

        // Create English homepage content
        final HomepageContent englishContent = new HomepageContent();
        englishContent.setLanguage(english);
        englishContent.setWelcomeMessage("Welcome to RefArch CMS");
        englishContent.setWelcomeMessageExtended("Your Modern Content Management Solution");
        englishContent.setExploreOurWork("Explore Our Features");
        englishContent.setGetInvolved("Get Started Today");
        englishContent.setImportantLinks("Essential Resources");
        englishContent.setEcosystemLinks("Our Ecosystem");
        englishContent.setBlog("Latest Blog Posts");
        englishContent.setPapers("Documentation");
        englishContent.setReadMore("Read More");

        // Create German homepage content
        final HomepageContent germanContent = new HomepageContent();
        germanContent.setLanguage(german);
        germanContent.setWelcomeMessage("Willkommen bei RefArch CMS");
        germanContent.setWelcomeMessageExtended("Ihre moderne Content-Management-Lösung");
        germanContent.setExploreOurWork("Entdecken Sie unsere Funktionen");
        germanContent.setGetInvolved("Starten Sie noch heute");
        germanContent.setImportantLinks("Wichtige Ressourcen");
        germanContent.setEcosystemLinks("Unser Ökosystem");
        germanContent.setBlog("Neueste Blog-Beiträge");
        germanContent.setPapers("Dokumentation");
        germanContent.setReadMore("Weiterlesen");

        homepage.addContent(englishContent);
        homepage.addContent(germanContent);
        homepageRepository.save(homepage);
    }
}
