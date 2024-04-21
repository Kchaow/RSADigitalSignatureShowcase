package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
public class PageController {
    final UserSessionRepository userSessionRepository;
    public PageController(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }
    @RequestMapping("/")
    public String testing(Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        Optional<UserSession> userSession = userSessionRepository.findById(httpSession.getId());
        model.addAttribute("p", userSession.isPresent() ? userSession.get().getRsaCryptoSystem().getP().toString() : "");
        model.addAttribute("q", userSession.isPresent() ? userSession.get().getRsaCryptoSystem().getQ().toString() : "");
        model.addAttribute("sessionId", httpSession.getId());
        return "index";
    }
}
