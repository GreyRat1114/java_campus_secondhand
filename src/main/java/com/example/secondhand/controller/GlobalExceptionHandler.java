package com.example.secondhand.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "图片上传失败，请确认图片大小不超过 20MB");
        return "redirect:/products/new";
    }
}
