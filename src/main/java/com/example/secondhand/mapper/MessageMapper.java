package com.example.secondhand.mapper;

import com.example.secondhand.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(PrivateMessage message);

    List<PrivateMessage> findChatList(@Param("userId") Long userId);

    List<PrivateMessage> findConversation(@Param("userId") Long userId,
                                           @Param("targetId") Long targetId);

    int markRead(@Param("userId") Long userId,
                 @Param("targetId") Long targetId);

    int countUnread(@Param("userId") Long userId);
}
