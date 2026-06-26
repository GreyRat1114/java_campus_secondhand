package com.example.secondhand.mapper;

import com.example.secondhand.entity.ForumComment;
import com.example.secondhand.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForumMapper {
    int insertPost(ForumPost post);

    List<ForumPost> findPostPage(@Param("keyword") String keyword,
                                  @Param("postType") String postType,
                                  @Param("boardId") Long boardId,
                                  @Param("offset") Integer offset,
                                  @Param("size") Integer size);

    int countPostPage(@Param("keyword") String keyword,
                      @Param("postType") String postType,
                      @Param("boardId") Long boardId);

    ForumPost findPostById(@Param("id") Long id);

    int increaseViewCount(@Param("id") Long id);

    int insertComment(ForumComment comment);

    List<ForumComment> findCommentsByPostId(@Param("postId") Long postId);

    List<ForumComment> findTopCommentsByPostId(@Param("postId") Long postId,
                                                @Param("currentUserId") Long currentUserId);

    List<ForumComment> findChildCommentsByPostId(@Param("postId") Long postId,
                                                  @Param("currentUserId") Long currentUserId);

    ForumComment findCommentById(@Param("id") Long id);

    int insertCommentLike(@Param("commentId") Long commentId,
                          @Param("userId") Long userId);

    int deleteCommentLike(@Param("commentId") Long commentId,
                          @Param("userId") Long userId);

    int existsCommentLike(@Param("commentId") Long commentId,
                          @Param("userId") Long userId);

    List<ForumPost> findPostsByUserId(@Param("userId") Long userId);

    int updatePost(ForumPost post);

    int deletePostByOwner(@Param("id") Long id, @Param("userId") Long userId);

    List<ForumPost> findAdminPostPage(@Param("keyword") String keyword,
                                       @Param("postType") String postType,
                                       @Param("status") String status,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

    int countAdminPostPage(@Param("keyword") String keyword,
                           @Param("postType") String postType,
                           @Param("status") String status);

    int adminDeletePost(@Param("id") Long id);

    List<ForumComment> findAdminCommentPage(@Param("keyword") String keyword,
                                             @Param("status") String status,
                                             @Param("offset") Integer offset,
                                             @Param("size") Integer size);

    int countAdminCommentPage(@Param("keyword") String keyword,
                              @Param("status") String status);

    int adminDeleteComment(@Param("id") Long id);
}
