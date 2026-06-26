package com.example.secondhand.service;

import com.example.secondhand.entity.ForumComment;
import com.example.secondhand.entity.ForumPost;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.ForumMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ForumService {
    private final ForumMapper forumMapper;

    public ForumService(ForumMapper forumMapper) {
        this.forumMapper = forumMapper;
    }

    public List<ForumPost> findPostPage(String keyword, String postType, int page, int size) {
        return findPostPage(keyword, postType, null, page, size);
    }

    public List<ForumPost> findPostPage(String keyword, String postType, Long boardId, int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return forumMapper.findPostPage(keyword, postType, boardId, offset, size);
    }

    public int countPostPage(String keyword, String postType) {
        return countPostPage(keyword, postType, null);
    }

    public int countPostPage(String keyword, String postType, Long boardId) {
        return forumMapper.countPostPage(keyword, postType, boardId);
    }

    @Transactional
    public void createPost(User user, String title, String content, String postType) {
        createPost(user, title, content, postType, null, null);
    }

    @Transactional
    public void createPost(User user,
                           String title,
                           String content,
                           String postType,
                           Long boardId,
                           Long productId) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("标题不能超过100字");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        if (content.length() > 5000) {
            throw new IllegalArgumentException("内容不能超过5000字");
        }
        if (!"NEED".equals(postType) && !"DISCUSS".equals(postType)) {
            postType = "DISCUSS";
        }

        ForumPost post = new ForumPost();
        post.setUserId(user.getId());
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setPostType(postType);
        post.setBoardId(boardId);
        post.setProductId(productId);
        forumMapper.insertPost(post);
    }

    @Transactional
    public ForumPost getPostDetail(Long id) {
        ForumPost post = forumMapper.findPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在或已删除");
        }

        forumMapper.increaseViewCount(id);
        post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
        return post;
    }

    public List<ForumComment> findCommentsByPostId(Long postId) {
        return forumMapper.findCommentsByPostId(postId);
    }

    public List<ForumPost> findMyPosts(User user) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }
        return forumMapper.findPostsByUserId(user.getId());
    }

    public ForumPost getEditPost(Long id, User user) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }

        ForumPost post = forumMapper.findPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在或已删除");
        }
        if (!post.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("只能编辑自己发布的帖子");
        }
        return post;
    }

    @Transactional
    public void updatePost(User user, Long id, String title, String content, String postType) {
        updatePost(user, id, title, content, postType, null, null);
    }

    @Transactional
    public void updatePost(User user,
                           Long id,
                           String title,
                           String content,
                           String postType,
                           Long boardId,
                           Long productId) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }

        ForumPost oldPost = forumMapper.findPostById(id);
        if (oldPost == null) {
            throw new IllegalArgumentException("帖子不存在或已删除");
        }
        if (!oldPost.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("只能修改自己发布的帖子");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("标题不能超过100字");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        if (content.length() > 5000) {
            throw new IllegalArgumentException("内容不能超过5000字");
        }
        if (!"NEED".equals(postType) && !"DISCUSS".equals(postType)) {
            postType = "DISCUSS";
        }

        ForumPost post = new ForumPost();
        post.setId(id);
        post.setUserId(user.getId());
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setPostType(postType);
        post.setBoardId(boardId == null ? oldPost.getBoardId() : boardId);
        post.setProductId(productId == null ? oldPost.getProductId() : productId);
        forumMapper.updatePost(post);
    }

    @Transactional
    public void deletePost(User user, Long id) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }

        ForumPost post = forumMapper.findPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在或已删除");
        }
        if (!post.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("只能删除自己发布的帖子");
        }
        forumMapper.deletePostByOwner(id, user.getId());
    }

    public List<ForumPost> findAdminPostPage(String keyword,
                                             String postType,
                                             String status,
                                             int page,
                                             int size) {
        int offset = Math.max(page - 1, 0) * size;
        return forumMapper.findAdminPostPage(keyword, postType, status, offset, size);
    }

    public int countAdminPostPage(String keyword, String postType, String status) {
        return forumMapper.countAdminPostPage(keyword, postType, status);
    }

    @Transactional
    public void adminDeletePost(Long id) {
        forumMapper.adminDeletePost(id);
    }

    public List<ForumComment> findAdminCommentPage(String keyword,
                                                   String status,
                                                   int page,
                                                   int size) {
        int offset = Math.max(page - 1, 0) * size;
        return forumMapper.findAdminCommentPage(keyword, status, offset, size);
    }

    public int countAdminCommentPage(String keyword, String status) {
        return forumMapper.countAdminCommentPage(keyword, status);
    }

    @Transactional
    public void adminDeleteComment(Long id) {
        forumMapper.adminDeleteComment(id);
    }

    public List<ForumComment> findFloorComments(Long postId, User currentUser) {
        Long currentUserId = currentUser == null ? null : currentUser.getId();
        List<ForumComment> floors = forumMapper.findTopCommentsByPostId(postId, currentUserId);
        List<ForumComment> children = forumMapper.findChildCommentsByPostId(postId, currentUserId);

        for (ForumComment floor : floors) {
            List<ForumComment> list = new ArrayList<>();
            for (ForumComment child : children) {
                if (floor.getId().equals(child.getParentId())) {
                    list.add(child);
                }
            }
            floor.setChildren(list);
        }

        return floors;
    }

    @Transactional
    public void createComment(User user, Long postId, String content) {
        createComment(user, postId, null, content);
    }

    @Transactional
    public void createComment(User user, Long postId, Long parentId, String content) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }
        ForumPost post = forumMapper.findPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        if (parentId != null) {
            ForumComment parent = forumMapper.findCommentById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("回复对象不存在");
            }
            if (!postId.equals(parent.getPostId())) {
                throw new IllegalArgumentException("回复对象不属于当前帖子");
            }
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("评论不能为空");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("评论不能超过1000字");
        }

        ForumComment comment = new ForumComment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setParentId(parentId);
        comment.setContent(content.trim());
        forumMapper.insertComment(comment);
    }

    @Transactional
    public void toggleCommentLike(User user, Long commentId) {
        if (user == null) {
            throw new IllegalArgumentException("请先登录");
        }

        ForumComment comment = forumMapper.findCommentById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("回复不存在");
        }

        int exists = forumMapper.existsCommentLike(commentId, user.getId());
        if (exists > 0) {
            forumMapper.deleteCommentLike(commentId, user.getId());
        } else {
            forumMapper.insertCommentLike(commentId, user.getId());
        }
    }
}
