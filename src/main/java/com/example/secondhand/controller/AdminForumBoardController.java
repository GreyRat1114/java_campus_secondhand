package com.example.secondhand.controller;

import com.example.secondhand.entity.ForumBoard;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.ForumBoardService;
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
@RequestMapping("/admin/forum/boards")
public class AdminForumBoardController {
    private final ForumBoardService forumBoardService;

    public AdminForumBoardController(ForumBoardService forumBoardService) {
        this.forumBoardService = forumBoardService;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        List<ForumBoard> boards = forumBoardService.findAdminBoards();
        model.addAttribute("boards", boards);

        return "admin/forum_boards";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) Integer sortOrder,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            forumBoardService.create(name, description, sortOrder);
            redirectAttributes.addFlashAttribute("success", "板块添加成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/forum/boards";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) Integer sortOrder,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            forumBoardService.update(id, name, description, sortOrder);
            redirectAttributes.addFlashAttribute("success", "板块修改成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/forum/boards";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            forumBoardService.delete(id);
            redirectAttributes.addFlashAttribute("success", "板块删除成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/forum/boards";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}
