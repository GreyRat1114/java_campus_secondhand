-- 校园二手交易平台 PostgreSQL 建库建表脚本
-- 第一步：CREATE DATABASE secondhand_db;
-- 第二步：连接 secondhand_db 后执行本文件

DROP TABLE IF EXISTS forum_comment;
DROP TABLE IF EXISTS forum_post;
DROP TABLE IF EXISTS private_message;
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS order_info;
DROP TABLE IF EXISTS product_image;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    phone VARCHAR(30),
    credit_score INT NOT NULL DEFAULT 100,
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_follow (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL REFERENCES sys_user(id),
    following_id BIGINT NOT NULL REFERENCES sys_user(id),
    create_time TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_follow UNIQUE (follower_id, following_id),
    CONSTRAINT ck_follow_not_self CHECK (follower_id <> following_id)
);

CREATE INDEX idx_follow_follower ON user_follow(follower_id);
CREATE INDEX idx_follow_following ON user_follow(following_id);

CREATE TABLE forum_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    post_type VARCHAR(30) NOT NULL DEFAULT 'DISCUSS',
    status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    view_count INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT now(),
    update_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE forum_comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES forum_post(id),
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    content TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_forum_post_user ON forum_post(user_id);
CREATE INDEX idx_forum_post_type ON forum_post(post_type);
CREATE INDEX idx_forum_post_time ON forum_post(create_time);
CREATE INDEX idx_forum_comment_post ON forum_comment(post_id);

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES category(id),
    seller_id BIGINT NOT NULL REFERENCES sys_user(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_seller ON product(seller_id);

CREATE TABLE product_image (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE order_info (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    buyer_id BIGINT NOT NULL REFERENCES sys_user(id),
    seller_id BIGINT NOT NULL REFERENCES sys_user(id),
    order_status VARCHAR(20) NOT NULL DEFAULT 'WAIT_CONFIRM',
    create_time TIMESTAMP NOT NULL DEFAULT now(),
    finish_time TIMESTAMP
);

CREATE INDEX idx_order_buyer ON order_info(buyer_id);
CREATE INDEX idx_order_seller ON order_info(seller_id);
CREATE INDEX idx_order_status ON order_info(order_status);

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES order_info(id),
    reviewer_id BIGINT NOT NULL REFERENCES sys_user(id),
    target_user_id BIGINT NOT NULL REFERENCES sys_user(id),
    score INT NOT NULL CHECK(score BETWEEN 1 AND 5),
    content VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uk_review_order ON review(order_id);

CREATE TABLE private_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES sys_user(id),
    receiver_id BIGINT NOT NULL REFERENCES sys_user(id),
    product_id BIGINT REFERENCES product(id),
    content VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_private_message_sender ON private_message(sender_id);
CREATE INDEX idx_private_message_receiver ON private_message(receiver_id);
CREATE INDEX idx_private_message_product ON private_message(product_id);
CREATE INDEX idx_private_message_time ON private_message(create_time);

INSERT INTO sys_user(username, password, role, phone, credit_score, status) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', '10000000000', 100, 'NORMAL'),
('zhangsan', 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER', '18800000001', 100, 'NORMAL'),
('lisi', 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER', '18800000002', 98, 'NORMAL');

INSERT INTO category(name, description) VALUES
('教材书籍', '教材、参考书、考试资料'),
('电子产品', '耳机、键盘、显示器等'),
('生活用品', '台灯、收纳、宿舍用品'),
('服装鞋包', '衣服、鞋子、包'),
('运动器材', '球拍、哑铃、自行车等');

INSERT INTO product(title, description, price, category_id, seller_id, status, create_time) VALUES
('Java Web 程序设计教材', '课程教材，九成新，可校内面交。', 25.00, 1, 2, 'SOLD', now() - interval '3 day'),
('罗技无线鼠标', '使用正常，适合宿舍学习使用。', 45.00, 2, 2, 'SOLD', now() - interval '2 day'),
('宿舍台灯', '亮度可调，毕业出闲置。', 30.00, 3, 3, 'SOLD', now() - interval '1 day'),
('篮球', '室外篮球，磨损较少。', 35.00, 5, 3, 'PENDING', now()),
('日语能力考 N2 资料', '含练习题和笔记。', 20.00, 1, 2, 'PENDING', now()),
('蓝牙耳机', '续航正常，适合通勤和自习室使用。', 60.00, 2, 3, 'ON_SALE', now() - interval '6 hour');

INSERT INTO order_info(product_id, buyer_id, seller_id, order_status, create_time, finish_time) VALUES
(1, 3, 2, 'FINISHED', now() - interval '5 day', now() - interval '4 day'),
(2, 3, 2, 'FINISHED', now() - interval '2 day', now() - interval '1 day'),
(3, 2, 3, 'WAIT_CONFIRM', now() - interval '1 hour', NULL);

INSERT INTO review(order_id, reviewer_id, target_user_id, score, content) VALUES
(1, 3, 2, 5, '教材保存很好，交易很顺利。'),
(2, 3, 2, 4, '鼠标正常，卖家回复快。');
