CREATE TABLE users (
    id         BIGSERIAL    PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    nickname   VARCHAR(50)  NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE tags (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE references (
    id           BIGSERIAL    PRIMARY KEY,
    title        VARCHAR(500) NOT NULL,
    summary      TEXT,
    url          VARCHAR(1000) NOT NULL,
    source       VARCHAR(20)  NOT NULL,
    author       VARCHAR(200),
    source_id    VARCHAR(100),
    published_at TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reference_source_source_id UNIQUE (source, source_id)
);

CREATE INDEX idx_reference_source     ON references (source);
CREATE INDEX idx_reference_created_at ON references (created_at);

CREATE TABLE reference_tags (
    reference_id BIGINT NOT NULL REFERENCES references (id) ON DELETE CASCADE,
    tag_id       BIGINT NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    PRIMARY KEY (reference_id, tag_id)
);

CREATE TABLE collections (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_collection_user_name UNIQUE (user_id, name)
);

CREATE TABLE collection_references (
    collection_id BIGINT NOT NULL REFERENCES collections (id) ON DELETE CASCADE,
    reference_id  BIGINT NOT NULL REFERENCES references (id) ON DELETE CASCADE,
    PRIMARY KEY (collection_id, reference_id)
);
