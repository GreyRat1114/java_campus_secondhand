package com.example.secondhand.controller;

import com.example.secondhand.entity.ForumComment;
import com.example.secondhand.entity.ForumPost;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.ForumBoardService;
import com.example.secondhand.service.ForumService;
import com.example.secondhand.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ForumController {
    private final ForumService forumService;
    private final ForumBoardService forumBoardService;
    private final ProductService productService;

    public ForumController(ForumService forumService,
                           ForumBoardService forumBoardService,
                           ProductService productService) {
        this.forumService = forumService;
        this.forumBoardService = forumBoardService;
        this.productService = productService;
    }

    @GetMapping("/forum")
    public String forum(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String postType,
                        @RequestParam(required = false) Long boardId,
                        @RequestParam(defaultValue = "1") Integer page,
                        Model model) {
        int size = 10;
        List<ForumPost> posts = forumService.findPostPage(keyword, postType, boardId, page, size);
        int total = forumService.countPostPage(keyword, postType, boardId);
        int totalPage = (int) Math.ceil(total * 1.0 / size);

        model.addAttribute("posts", posts);
        model.addAttribute("boards", forumBoardService.findNormalBoards());
        model.addAttribute("keyword", keyword);
        model.addAttribute("postType", postType);
        model.addAttribute("boardId", boardId);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", Math.max(totalPage, 1));
        return "forum";
    }

    @GetMapping("/forum/new")
    public String newPost(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("boards", forumBoardService.findNormalBoards());
        model.addAttribute("myProducts", productService.findBySellerId(user.getId()));
        return "forum_form";
    }

    @PostMapping("/forum/create")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam String postType,
                             @RequestParam(required = false) Long boardId,
                             @RequestParam(required = false) Long productId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        try {
            forumService.createPost(user, title, content, postType, boardId, productId);
            redirectAttributes.addFlashAttribute("success", "发帖成功");
            return "redirect:/forum";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forum/new";
        }
    }

    @GetMapping("/forum/{id}")
    public String detail(@PathVariable Long id,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            User user = currentUser(session);
            ForumPost post = forumService.getPostDetail(id);
            List<ForumComment> comments = forumService.findFloorComments(id, user);
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            return "forum_detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forum";
        }
    }

    @PostMapping("/forum/{id}/comments")
    public String createComment(@PathVariable Long id,
                                @RequestParam(required = false) Long parentId,
                                @RequestParam String content,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        try {
            forumService.createComment(user, id, parentId, content);
            redirectAttributes.addFlashAttribute("success", "回复成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/forum/" + id;
    }

    @PostMapping("/forum/comments/{commentId}/like")
    public String likeComment(@PathVariable Long commentId,
                              @RequestParam Long postId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = currentUser(session);

        try {
            forumService.toggleCommentLike(user, commentId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/forum/" + postId;
    }

    @GetMapping("/my/posts")
    public String myPosts(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<ForumPost> myPosts = forumService.findMyPosts(user);
        model.addAttribute("myPosts", myPosts);
        return "my_posts";
    }

    @GetMapping("/my/posts/{id}/edit")
    public String editPost(@PathVariable Long id,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        try {
            ForumPost post = forumService.getEditPost(id, user);
            model.addAttribute("post", post);
            return "forum_edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/my/posts";
        }
    }

    @PostMapping("/my/posts/{id}/update")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam String postType,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        try {
            forumService.updatePost(user, id, title, content, postType);
            redirectAttributes.addFlashAttribute("success", "帖子修改成功");
            return "redirect:/my/posts";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/my/posts/" + id + "/edit";
        }
    }

    @PostMapping("/my/posts/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        try {
            forumService.deletePost(user, id);
            redirectAttributes.addFlashAttribute("success", "帖子已删除");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my/posts";
    }

    private User currentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
