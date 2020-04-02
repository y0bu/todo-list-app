package com.yoav.todolist.controllers;

import com.yoav.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private AccountService accountService;

    @Autowired
    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin(@CookieValue(value = "username", defaultValue = "notSetCookie") String username) {
        if (!(username.equals("notSetCookie"))) {
            return "redirect:/dashboard";
        }
        return "login/index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String postLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (accountService.isExistByUsernameAndPassword(username, password)) {
            session.setAttribute("username", username);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("alert", "not the correct username or/and password");
            return "login/index";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, params = {"username", "password", "rememberMe"})
    public String postLoginWithRememberMe(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            Model model) {

        if (accountService.isExistByUsernameAndPassword(username, password)) {
            Cookie cookie = new Cookie("username", username);
            // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
            cookie.setHttpOnly(true);
            cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
            cookie.setPath("/"); // global cookie accessible every where
            response.addCookie(cookie);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("alert", "not the correct username or/and password");
            return "login/index";
        }
    }
}
