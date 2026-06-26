package com.example.secondhand.service;

import com.example.secondhand.entity.PrivateMessage;
import com.example.secondhand.entity.Product;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.MessageMapper;
import com.example.secondhand.mapper.ProductMapper;
import com.example.secondhand.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    public MessageService(MessageMapper messageMapper,
                          UserMapper userMapper,
                          ProductMapper productMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
    }

    public List<PrivateMessage> findChatList(Long userId) {
        return messageMapper.findChatList(userId);
    }

    @Transactional
    public List<PrivateMessage> findConversation(Long userId, Long targetId) {
        messageMapper.markRead(userId, targetId);
        return messageMapper.findConversation(userId, targetId);
    }

    @Transactional
    public void sendMessage(User sender, Long receiverId, Long productId, String content) {
        if (sender == null) {
            throw new IllegalArgumentException("请先登录");
        }
        if (receiverId == null) {
            throw new IllegalArgumentException("接收人不能为空");
        }
        if (sender.getId().equals(receiverId)) {
            throw new IllegalArgumentException("不能给自己发送私信");
        }

        User receiver = userMapper.findById(receiverId);
        if (receiver == null) {
            throw new IllegalArgumentException("接收用户不存在");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("消息内容不能超过500字");
        }
        if (productId != null) {
            Product product = productMapper.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("关联商品不存在");
            }
        }

        PrivateMessage message = new PrivateMessage();
        message.setSenderId(sender.getId());
        message.setReceiverId(receiverId);
        message.setProductId(productId);
        message.setContent(content.trim());
        messageMapper.insert(message);
    }

    public int countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }
}
