-- 已有数据库加私聊表时执行本文件，不会删除已有消息

CREATE TABLE IF NOT EXISTS private_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES sys_user(id),
    receiver_id BIGINT NOT NULL REFERENCES sys_user(id),
    product_id BIGINT REFERENCES product(id),
    content VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_private_message_sender ON private_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_private_message_receiver ON private_message(receiver_id);
CREATE INDEX IF NOT EXISTS idx_private_message_product ON private_message(product_id);
CREATE INDEX IF NOT EXISTS idx_private_message_time ON private_message(create_time);
