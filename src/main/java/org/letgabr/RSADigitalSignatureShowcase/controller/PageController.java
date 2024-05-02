package org.letgabr.RSADigitalSignatureShowcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.letgabr.RSADigitalSignatureShowcase.dao.UserSessionRepository;
import org.letgabr.RSADigitalSignatureShowcase.dto.UserSession;
import org.letgabr.RSADigitalSignatureShowcase.util.PrimeTester;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.util.Optional;

@Controller
public class PageController {
    final PrimeTester primeTester;
    final UserSessionRepository userSessionRepository;
    public PageController(UserSessionRepository userSessionRepository, PrimeTester primeTester) {
        this.userSessionRepository = userSessionRepository;
        this.primeTester = primeTester;
    }
    @RequestMapping("/")
    public String testing(Model model, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        Optional<UserSession> userSession = userSessionRepository.findById(httpSession.getId());
//        userSession.ifPresent(x -> {
//            if (x.getConnectionStatus().getStatus().equals("connected")) {
//                x.getConnectionStatus().setStatus("free");
//                x.getConnectionStatus().setUserId("");
//                userSessionRepository.save(x);
//            }
//        });
        model.addAttribute("p", userSession.map(x -> x.getRsaCryptoSystem().getP().toString())
                .orElseGet(() -> ""));
        model.addAttribute("q", userSession.map(x -> x.getRsaCryptoSystem().getQ().toString())
                .orElseGet(() -> ""));
        model.addAttribute("pIsPrime", userSession.map(x -> primeTester.isPrime(x.getRsaCryptoSystem().getP()) ? "true" : "false")
                .orElseGet(() -> ""));
        model.addAttribute("qIsPrime", userSession.map(x -> primeTester.isPrime(x.getRsaCryptoSystem().getQ()) ? "true" : "false")
                .orElseGet(() -> ""));
        model.addAttribute("sessionId", httpSession.getId());
        model.addAttribute("e", userSession.map(x -> x.getRsaCryptoSystem().getPublicKey().toString())
                .orElseGet(() -> ""));
        model.addAttribute("d", userSession.map(x -> x.getRsaCryptoSystem().getPrivateKey().toString())
                .orElseGet(() -> ""));
        model.addAttribute("n", userSession.map(x -> x.getRsaCryptoSystem().getN().toString())
                .orElseGet(() -> ""));

        if (userSession.isPresent() && userSession.get().getConnectionStatus().getStatus().equals("connected")) {
            Optional<UserSession> connectedUserSession = userSessionRepository.findById(userSession.get().getConnectionStatus().getUserId());
            model.addAttribute("connectedUser", connectedUserSession.map(UserSession::getJsessionId)
                    .orElseGet(() -> ""));
            model.addAttribute("connectedPublicKey", connectedUserSession.map(x -> x.getRsaCryptoSystem().getPublicKey().toString())
                    .orElseGet(() -> ""));
            model.addAttribute("connectedN", connectedUserSession.map(x -> x.getRsaCryptoSystem().getN().toString())
                    .orElseGet(() -> ""));
        }

        return "index";
    }
}
