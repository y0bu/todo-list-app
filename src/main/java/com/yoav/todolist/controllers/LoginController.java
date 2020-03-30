package com.yoav.todolist.controllers;

import com.yoav.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private AccountService accountService;

    @Autowired
    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin() {
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
}
