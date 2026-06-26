package com.example.secondhand.service;

import com.example.secondhand.entity.ForumBoard;
import com.example.secondhand.mapper.ForumBoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumBoardService {
    private final ForumBoardMapper forumBoardMapper;

    public ForumBoardService(ForumBoardMapper forumBoardMapper) {
        this.forumBoardMapper = forumBoardMapper;
    }

    public List<ForumBoard> findNormalBoards() {
        return forumBoardMapper.findNormalBoards();
    }

    public List<ForumBoard> findAdminBoards() {
        return forumBoardMapper.findAdminBoards();
    }

    @Transactional
    public void create(String name, String description, Integer sortOrder) {
        validate(name);

        ForumBoard board = new ForumBoard();
        board.setName(name.trim());
        board.setDescription(description);
        board.setSortOrder(sortOrder == null ? 0 : sortOrder);

        forumBoardMapper.insert(board);
    }

    @Transactional
    public void update(Long id, String name, String description, Integer sortOrder) {
        validate(name);

        ForumBoard board = forumBoardMapper.findById(id);
        if (board == null) {
            throw new IllegalArgumentException("板块不存在");
        }

        board.setName(name.trim());
        board.setDescription(description);
        board.setSortOrder(sortOrder == null ? 0 : sortOrder);

        forumBoardMapper.update(board);
    }

    @Transactional
    public void delete(Long id) {
        int count = forumBoardMapper.countPosts(id);
        if (count > 0) {
            throw new IllegalArgumentException("该板块下已有帖子，不能删除");
        }
        forumBoardMapper.deleteBoard(id);
    }

    private void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("板块名称不能为空");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("板块名称不能超过50字");
        }
    }
}
