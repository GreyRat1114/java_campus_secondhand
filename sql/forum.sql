-- 已有数据库加校园论坛表时执行本文件，不会删除已有帖子和评论

CREATE TABLE IF NOT EXISTS forum_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    post_type VARCHAR(30) NOT NULL DEFAULT 'DISCUSS',
    status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    view_count INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT now(),
    update_time TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_forum_post_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS forum_comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    create_time TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_forum_comment_post FOREIGN KEY (post_id) REFERENCES forum_post(id),
    CONSTRAINT fk_forum_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE INDEX IF NOT EXISTS idx_forum_post_user ON forum_post(user_id);
CREATE INDEX IF NOT EXISTS idx_forum_post_type ON forum_post(post_type);
CREATE INDEX IF NOT EXISTS idx_forum_post_time ON forum_post(create_time);
CREATE INDEX IF NOT EXISTS idx_forum_comment_post ON forum_comment(post_id);
