-- WordPress Database Schema for Spring Boot (PostgreSQL compatible)
-- This migration creates all core WordPress tables with wp_ prefix

-- =====================================================
-- CORE CONTENT TABLES
-- =====================================================

-- Posts table (wp_posts)
CREATE TABLE wp_posts (
    ID BIGSERIAL PRIMARY KEY,
    post_author BIGINT NOT NULL DEFAULT 0,
    post_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_date_gmt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_content TEXT NOT NULL,
    post_title TEXT NOT NULL,
    post_excerpt TEXT NOT NULL,
    post_status VARCHAR(20) NOT NULL DEFAULT 'publish',
    comment_status VARCHAR(20) NOT NULL DEFAULT 'open',
    ping_status VARCHAR(20) NOT NULL DEFAULT 'open',
    post_password VARCHAR(255) NOT NULL DEFAULT '',
    post_name VARCHAR(200) NOT NULL DEFAULT '',
    to_ping TEXT NOT NULL,
    pinged TEXT NOT NULL,
    post_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_modified_gmt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_content_filtered TEXT NOT NULL,
    post_parent BIGINT NOT NULL DEFAULT 0,
    guid VARCHAR(255) NOT NULL DEFAULT '',
    menu_order INTEGER NOT NULL DEFAULT 0,
    post_type VARCHAR(20) NOT NULL DEFAULT 'post',
    post_mime_type VARCHAR(100) NOT NULL DEFAULT '',
    comment_count BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for wp_posts
CREATE INDEX wp_posts_post_name_idx ON wp_posts (post_name);
CREATE INDEX wp_posts_type_status_date_idx ON wp_posts (post_type, post_status, post_date, ID);
CREATE INDEX wp_posts_post_parent_idx ON wp_posts (post_parent);
CREATE INDEX wp_posts_post_author_idx ON wp_posts (post_author);

-- Post metadata table (wp_postmeta)
CREATE TABLE wp_postmeta (
    meta_id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL DEFAULT 0,
    meta_key VARCHAR(255) DEFAULT NULL,
    meta_value TEXT
);

-- Create indexes for wp_postmeta
CREATE INDEX wp_postmeta_post_id_idx ON wp_postmeta (post_id);
CREATE INDEX wp_postmeta_meta_key_idx ON wp_postmeta (meta_key);

-- Comments table (wp_comments)
CREATE TABLE wp_comments (
    comment_ID BIGSERIAL PRIMARY KEY,
    comment_post_ID BIGINT NOT NULL DEFAULT 0,
    comment_author TEXT NOT NULL,
    comment_author_email VARCHAR(100) NOT NULL DEFAULT '',
    comment_author_url VARCHAR(200) NOT NULL DEFAULT '',
    comment_author_IP VARCHAR(100) NOT NULL DEFAULT '',
    comment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment_date_gmt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment_content TEXT NOT NULL,
    comment_karma INTEGER NOT NULL DEFAULT 0,
    comment_approved VARCHAR(20) NOT NULL DEFAULT '1',
    comment_agent VARCHAR(255) NOT NULL DEFAULT '',
    comment_type VARCHAR(20) NOT NULL DEFAULT 'comment',
    comment_parent BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for wp_comments
CREATE INDEX wp_comments_comment_post_id_idx ON wp_comments (comment_post_ID);
CREATE INDEX wp_comments_comment_approved_date_gmt_idx ON wp_comments (comment_approved, comment_date_gmt);
CREATE INDEX wp_comments_comment_date_gmt_idx ON wp_comments (comment_date_gmt);
CREATE INDEX wp_comments_comment_parent_idx ON wp_comments (comment_parent);
CREATE INDEX wp_comments_comment_author_email_idx ON wp_comments (comment_author_email);

-- Comment metadata table (wp_commentmeta)
CREATE TABLE wp_commentmeta (
    meta_id BIGSERIAL PRIMARY KEY,
    comment_id BIGINT NOT NULL DEFAULT 0,
    meta_key VARCHAR(255) DEFAULT NULL,
    meta_value TEXT
);

-- Create indexes for wp_commentmeta
CREATE INDEX wp_commentmeta_comment_id_idx ON wp_commentmeta (comment_id);
CREATE INDEX wp_commentmeta_meta_key_idx ON wp_commentmeta (meta_key);

-- =====================================================
-- TAXONOMY SYSTEM
-- =====================================================

-- Terms table (wp_terms)
CREATE TABLE wp_terms (
    term_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL DEFAULT '',
    slug VARCHAR(200) NOT NULL DEFAULT '',
    term_group BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for wp_terms
CREATE INDEX wp_terms_slug_idx ON wp_terms (slug);
CREATE INDEX wp_terms_name_idx ON wp_terms (name);

-- Term taxonomy table (wp_term_taxonomy)
CREATE TABLE wp_term_taxonomy (
    term_taxonomy_id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL DEFAULT 0,
    taxonomy VARCHAR(32) NOT NULL DEFAULT '',
    description TEXT NOT NULL,
    parent BIGINT NOT NULL DEFAULT 0,
    count BIGINT NOT NULL DEFAULT 0,
    UNIQUE(term_id, taxonomy)
);

-- Create indexes for wp_term_taxonomy
CREATE INDEX wp_term_taxonomy_taxonomy_idx ON wp_term_taxonomy (taxonomy);

-- Term relationships table (wp_term_relationships)
CREATE TABLE wp_term_relationships (
    object_id BIGINT NOT NULL DEFAULT 0,
    term_taxonomy_id BIGINT NOT NULL DEFAULT 0,
    term_order INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (object_id, term_taxonomy_id)
);

-- Create indexes for wp_term_relationships
CREATE INDEX wp_term_relationships_term_taxonomy_id_idx ON wp_term_relationships (term_taxonomy_id);

-- Term metadata table (wp_termmeta)
CREATE TABLE wp_termmeta (
    meta_id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL DEFAULT 0,
    meta_key VARCHAR(255) DEFAULT NULL,
    meta_value TEXT
);

-- Create indexes for wp_termmeta
CREATE INDEX wp_termmeta_term_id_idx ON wp_termmeta (term_id);
CREATE INDEX wp_termmeta_meta_key_idx ON wp_termmeta (meta_key);

-- =====================================================
-- USER MANAGEMENT
-- =====================================================

-- Users table (wp_users)
CREATE TABLE wp_users (
    ID BIGSERIAL PRIMARY KEY,
    user_login VARCHAR(60) NOT NULL DEFAULT '',
    user_pass VARCHAR(255) NOT NULL DEFAULT '',
    user_nicename VARCHAR(50) NOT NULL DEFAULT '',
    user_email VARCHAR(100) NOT NULL DEFAULT '',
    user_url VARCHAR(100) NOT NULL DEFAULT '',
    user_registered TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_activation_key VARCHAR(255) NOT NULL DEFAULT '',
    user_status INTEGER NOT NULL DEFAULT 0,
    display_name VARCHAR(250) NOT NULL DEFAULT ''
);

-- Create indexes for wp_users
CREATE INDEX wp_users_user_login_key_idx ON wp_users (user_login);
CREATE INDEX wp_users_user_nicename_idx ON wp_users (user_nicename);
CREATE INDEX wp_users_user_email_idx ON wp_users (user_email);

-- User metadata table (wp_usermeta)
CREATE TABLE wp_usermeta (
    umeta_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL DEFAULT 0,
    meta_key VARCHAR(255) DEFAULT NULL,
    meta_value TEXT
);

-- Create indexes for wp_usermeta
CREATE INDEX wp_usermeta_user_id_idx ON wp_usermeta (user_id);
CREATE INDEX wp_usermeta_meta_key_idx ON wp_usermeta (meta_key);

-- =====================================================
-- SYSTEM TABLES
-- =====================================================

-- Options table (wp_options)
CREATE TABLE wp_options (
    option_id BIGSERIAL PRIMARY KEY,
    option_name VARCHAR(191) NOT NULL DEFAULT '',
    option_value TEXT NOT NULL,
    autoload VARCHAR(20) NOT NULL DEFAULT 'yes',
    UNIQUE(option_name)
);

-- Create indexes for wp_options
CREATE INDEX wp_options_autoload_idx ON wp_options (autoload);

-- Links table (wp_links)
CREATE TABLE wp_links (
    link_id BIGSERIAL PRIMARY KEY,
    link_url VARCHAR(255) NOT NULL DEFAULT '',
    link_name VARCHAR(255) NOT NULL DEFAULT '',
    link_image VARCHAR(255) NOT NULL DEFAULT '',
    link_target VARCHAR(25) NOT NULL DEFAULT '',
    link_description VARCHAR(255) NOT NULL DEFAULT '',
    link_visible VARCHAR(20) NOT NULL DEFAULT 'Y',
    link_owner BIGINT NOT NULL DEFAULT 1,
    link_rating INTEGER NOT NULL DEFAULT 0,
    link_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    link_rel VARCHAR(255) NOT NULL DEFAULT '',
    link_notes TEXT NOT NULL,
    link_rss VARCHAR(255) NOT NULL DEFAULT ''
);

-- Create indexes for wp_links
CREATE INDEX wp_links_link_visible_idx ON wp_links (link_visible); 