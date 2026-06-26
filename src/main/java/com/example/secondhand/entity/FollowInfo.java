package com.example.secondhand.entity;

import java.time.LocalDateTime;

public class FollowInfo {
    private Long id;
    private Long followerId;
    private Long followingId;
    private LocalDateTime createTime;

    private Long targetId;
    private String targetName;
    private Integer targetCreditScore;
    private String targetStatus;

    private Boolean followedByMe;
    private Boolean followingMe;
    private Boolean friend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Integer getTargetCreditScore() {
        return targetCreditScore;
    }

    public void setTargetCreditScore(Integer targetCreditScore) {
        this.targetCreditScore = targetCreditScore;
    }

    public String getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }

    public Boolean getFollowedByMe() {
        return followedByMe;
    }

    public void setFollowedByMe(Boolean followedByMe) {
        this.followedByMe = followedByMe;
    }

    public Boolean getFollowingMe() {
        return followingMe;
    }

    public void setFollowingMe(Boolean followingMe) {
        this.followingMe = followingMe;
    }

    public Boolean getFriend() {
        return friend;
    }

    public void setFriend(Boolean friend) {
        this.friend = friend;
    }
}
