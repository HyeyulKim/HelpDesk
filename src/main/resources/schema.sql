CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    requester_id BIGINT NOT NULL,
    assignee_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_requests_requester FOREIGN KEY (requester_id) REFERENCES users (user_id),
    CONSTRAINT fk_requests_assignee FOREIGN KEY (assignee_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_request FOREIGN KEY (request_id) REFERENCES requests (request_id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS attachments (
    attachment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachments_request FOREIGN KEY (request_id) REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS request_status_histories (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    from_status VARCHAR(20) NULL,
    to_status VARCHAR(20) NOT NULL,
    changed_by BIGINT NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_history_request FOREIGN KEY (request_id) REFERENCES requests (request_id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_user FOREIGN KEY (changed_by) REFERENCES users (user_id)
);