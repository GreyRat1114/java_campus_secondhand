package com.example.secondhand.controller;

import com.example.secondhand.entity.ForumComment;
import com.example.secondhand.entity.ForumPost;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.ForumService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/forum")
public class AdminForumController {
    private final ForumService forumService;

    public AdminForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/posts")
    public String posts(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String postType,
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "1") Integer page,
                        HttpSession session,
                        Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        int currentPage = Math.max(page, 1);
        int size = 10;
        List<ForumPost> posts = forumService.findAdminPostPage(keyword, postType, status, currentPage, size);
        int total = forumService.countAdminPostPage(keyword, postType, status);
        int totalPage = (int) Math.ceil(total * 1.0 / size);

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("postType", postType);
        model.addAttribute("status", status);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPage", Math.max(totalPage, 1));

        return "admin/forum_posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String postType,
                             @RequestParam(required = false) String status,
                             @RequestParam(defaultValue = "1") Integer page,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";

        forumService.adminDeletePost(id);
        redirectAttributes.addFlashAttribute("success", "帖子已删除");
        redirectAttributes.addAttribute("page", Math.max(page, 1));
        redirectAttributes.addAttribute("keyword", keyword == null ? "" : keyword);
        redirectAttributes.addAttribute("postType", postType == null ? "" : postType);
        redirectAttributes.addAttribute("status", status == null ? "" : status);

        return "redirect:/admin/forum/posts";
    }

    @GetMapping("/comments")
    public String comments(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String status,
                           @RequestParam(defaultValue = "1") Integer page,
                           HttpSession session,
                           Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        int currentPage = Math.max(page, 1);
        int size = 10;
        List<ForumComment> comments = forumService.findAdminCommentPage(keyword, status, currentPage, size);
        int total = forumService.countAdminCommentPage(keyword, status);
        int totalPage = (int) Math.ceil(total * 1.0 / size);

        model.addAttribute("comments", comments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("page", currentPage);
        model.addAttribute("totalPage", Math.max(totalPage, 1));

        return "admin/forum_comments";
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "1") Integer page,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";

        forumService.adminDeleteComment(id);
        redirectAttributes.addFlashAttribute("success", "评论已删除");
        redirectAttributes.addAttribute("page", Math.max(page, 1));
        redirectAttributes.addAttribute("keyword", keyword == null ? "" : keyword);
        redirectAttributes.addAttribute("status", status == null ? "" : status);

        return "redirect:/admin/forum/comments";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
