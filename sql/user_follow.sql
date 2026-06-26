-- 已有数据库加关注/好友表时执行本文件，不会删除已有关注关系

CREATE TABLE IF NOT EXISTS user_follow (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES sys_user(id),
    CONSTRAINT fk_follow_following FOREIGN KEY (following_id) REFERENCES sys_user(id),
    CONSTRAINT uk_follow UNIQUE (follower_id, following_id),
    CONSTRAINT ck_follow_not_self CHECK (follower_id <> following_id)
);

CREATE INDEX IF NOT EXISTS idx_follow_follower ON user_follow(follower_id);
CREATE INDEX IF NOT EXISTS idx_follow_following ON user_follow(following_id);
