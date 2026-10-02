package com.bloodbank.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        if (referer == null) {
            referer = "/dashboard";
        }
        
        // Log the error for debugging
        System.err.println("Exception caught globally: " + e.getMessage());
        
        // Redirect back to the page they came from with a generic error flag
        return "redirect:" + referer + (referer.contains("?") ? "&" : "?") + "error=true";
    }
}
