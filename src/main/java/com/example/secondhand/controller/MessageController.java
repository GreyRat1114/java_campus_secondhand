package com.example.secondhand.controller;

import com.example.secondhand.entity.PrivateMessage;
import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.ProductMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    private final MessageService messageService;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    public MessageController(MessageService messageService,
                             UserMapper userMapper,
                             ProductMapper productMapper) {
        this.messageService = messageService;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
    }

    @GetMapping("/messages")
    public String messageList(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<PrivateMessage> chats = messageService.findChatList(user.getId());
        int unreadCount = messageService.countUnread(user.getId());
        model.addAttribute("chats", chats);
        model.addAttribute("unreadCount", unreadCount);
        return "messages";
    }

    @GetMapping("/messages/chat/{targetId}")
    public String chat(@PathVariable Long targetId,
                       @RequestParam(required = false) Long productId,
                       HttpSession session,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        User targetUser = userMapper.findById(targetId);
        if (targetUser == null) {
            redirectAttributes.addFlashAttribute("error", "聊天对象不存在");
            return "redirect:/messages";
        }

        List<PrivateMessage> messages = messageService.findConversation(user.getId(), targetId);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("messages", messages);
        model.addAttribute("productId", productId);
        if (productId != null) {
            model.addAttribute("product", productMapper.findById(productId));
        }
        return "chat";
    }

    @PostMapping("/messages/send")
    public String send(@RequestParam Long receiverId,
                       @RequestParam(required = false) Long productId,
                       @RequestParam String content,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            messageService.sendMessage(user, receiverId, productId, content);
            redirectAttributes.addFlashAttribute("success", "发送成功");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        String redirectUrl = "redirect:/messages/chat/" + receiverId;
        if (productId != null) {
            redirectUrl += "?productId=" + productId;
        }
        return redirectUrl;
    }

    @GetMapping("/messages/api/conversation/{targetId}")
    @ResponseBody
    public List<PrivateMessage> apiConversation(@PathVariable Long targetId,
                                                HttpSession session) {
        User user = currentUser(session);
        if (user == null) {
            return Collections.emptyList();
        }

        User targetUser = userMapper.findById(targetId);
        if (targetUser == null) {
            return Collections.emptyList();
        }

        return messageService.findConversation(user.getId(), targetId);
    }

    @PostMapping("/messages/api/send")
    @ResponseBody
    public Map<String, Object> apiSend(@RequestParam Long receiverId,
                                       @RequestParam(required = false) Long productId,
                                       @RequestParam String content,
                                       HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = currentUser(session);
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        try {
            messageService.sendMessage(user, receiverId, productId, content);
            result.put("success", true);
            result.put("message", "发送成功");
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    private User currentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
