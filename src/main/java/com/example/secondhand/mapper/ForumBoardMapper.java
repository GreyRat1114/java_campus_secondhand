package com.example.secondhand.mapper;

import com.example.secondhand.entity.ForumBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForumBoardMapper {
    List<ForumBoard> findNormalBoards();

    List<ForumBoard> findAdminBoards();

    ForumBoard findById(@Param("id") Long id);

    int insert(ForumBoard board);

    int update(ForumBoard board);

    int deleteBoard(@Param("id") Long id);

    int countPosts(@Param("boardId") Long boardId);
}
