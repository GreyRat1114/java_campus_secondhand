package com.example.secondhand.mapper;

import com.example.secondhand.entity.FollowInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {
    int insertFollow(@Param("followerId") Long followerId,
                     @Param("followingId") Long followingId);

    int deleteFollow(@Param("followerId") Long followerId,
                     @Param("followingId") Long followingId);

    int existsFollow(@Param("followerId") Long followerId,
                     @Param("followingId") Long followingId);

    List<FollowInfo> findFollowing(@Param("userId") Long userId);

    List<FollowInfo> findFollowers(@Param("userId") Long userId);

    List<FollowInfo> findFriends(@Param("userId") Long userId);

    List<FollowInfo> searchUsers(@Param("userId") Long userId,
                                 @Param("keyword") String keyword);
}
