package com.example.secondhand.service;

import com.example.secondhand.entity.FollowInfo;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.FollowMapper;
import com.example.secondhand.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    public FollowService(FollowMapper followMapper, UserMapper userMapper) {
        this.followMapper = followMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public String follow(User currentUser, Long targetId) {
        if (currentUser == null) {
            return "请先登录";
        }
        if (targetId == null) {
            return "关注对象不能为空";
        }
        if (currentUser.getId().equals(targetId)) {
            return "不能关注自己";
        }

        User target = userMapper.findById(targetId);
        if (target == null) {
            return "用户不存在";
        }

        followMapper.insertFollow(currentUser.getId(), targetId);
        if (isFriend(currentUser.getId(), targetId)) {
            return "关注成功，你们已经成为好友";
        }
        return "关注成功，等待对方回关后成为好友";
    }

    @Transactional
    public String unfollow(User currentUser, Long targetId) {
        if (currentUser == null) {
            return "请先登录";
        }
        followMapper.deleteFollow(currentUser.getId(), targetId);
        return "已取消关注";
    }

    public boolean isFollowing(Long userId, Long targetId) {
        return followMapper.existsFollow(userId, targetId) > 0;
    }

    public boolean isFriend(Long userId, Long targetId) {
        return followMapper.existsFollow(userId, targetId) > 0
                && followMapper.existsFollow(targetId, userId) > 0;
    }

    public List<FollowInfo> findFollowing(Long userId) {
        return followMapper.findFollowing(userId);
    }

    public List<FollowInfo> findFollowers(Long userId) {
        return followMapper.findFollowers(userId);
    }

    public List<FollowInfo> findFriends(Long userId) {
        return followMapper.findFriends(userId);
    }

    public List<FollowInfo> searchUsers(Long userId, String keyword) {
        return followMapper.searchUsers(userId, keyword);
    }
}
