package com.example.secondhand.controller;

import com.example.secondhand.entity.FollowInfo;
import com.example.secondhand.entity.User;
import com.example.secondhand.service.FollowService;
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
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/friends")
    public String friends(@RequestParam(required = false) String keyword,
                          HttpSession session,
                          Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<FollowInfo> friends = followService.findFriends(user.getId());
        List<FollowInfo> followingList = followService.findFollowing(user.getId());
        List<FollowInfo> followerList = followService.findFollowers(user.getId());
        List<FollowInfo> searchResults = followService.searchUsers(user.getId(), keyword);

        model.addAttribute("friends", friends);
        model.addAttribute("followingList", followingList);
        model.addAttribute("followerList", followerList);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", keyword);
        return "friends";
    }

    @PostMapping("/friends/follow/{targetId}")
    public String follow(@PathVariable Long targetId,
                         @RequestParam(defaultValue = "/friends") String redirect,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        String message = followService.follow(user, targetId);
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:" + safeRedirect(redirect);
    }

    @PostMapping("/friends/unfollow/{targetId}")
    public String unfollow(@PathVariable Long targetId,
                           @RequestParam(defaultValue = "/friends") String redirect,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        String message = followService.unfollow(user, targetId);
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:" + safeRedirect(redirect);
    }

    private User currentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    private String safeRedirect(String redirect) {
        if (redirect == null || redirect.trim().isEmpty()) {
            return "/friends";
        }
        if (!redirect.startsWith("/")) {
            return "/friends";
        }
        if (redirect.startsWith("//")) {
            return "/friends";
        }
        return redirect;
    }
}
