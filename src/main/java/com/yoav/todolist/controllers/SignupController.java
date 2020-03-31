package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupController {

    private AccountService accountService;

    @Autowired
    public SignupController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String getSignup() {
        return "signup/index";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String postSignup(Model model,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String passwordAgain,
                             RedirectAttributes attributes) {

        String passwordAlert = chooseAlertWeakPassword(password);
        if (!password.equals(passwordAgain)) {
            model.addAttribute("alert", "password you typed is not the same as you typed above");
            return "signup/index";
        }
        else if (passwordAlert.length() != 0) {
            model.addAttribute("alert", passwordAlert);
            return "signup/index";
        }
        else if (accountService.isExistByUsername(username)) {
            model.addAttribute("alert", "the username is already have been taken");
            return "signup/index";
        }
        else {
            // we do not have any problems
            accountService.add(new Account(username, password));
            attributes.addFlashAttribute("alert", "you signed up successfully now you can log in");
            return "redirect:/login";
        }
    }

    public String chooseAlertWeakPassword(String password) {
        /*   choose which alert to show to the user if the password is weak like
             "you need at least one special character" or "your password is too long" or
             "you need at least 8 characters in the password" if the password is strong so return "" empty string
        */
        if (password.length() < 8) {
            return "you need at least 8 character in the password";
        } else if (password.length() > 200) {
            return "the password is to long maximum length of the password is 200";
        }

        boolean hasSpecialChar = false;
        int uppers = 0;
        int digits = 0;
        int lowers = 0;
        for (int i = 0; i < password.length(); ++i) {
            char current = password.charAt(i);
            if (current >= 'a' && current <= 'z') lowers++;
            else if (current >= 'A' && current <= 'Z') uppers++;
            else if (current >= '0' && current <= '9') digits++;
            else hasSpecialChar = true;
        }
        if (!hasSpecialChar) return "the password must contains at least one special character";
        else if (lowers == 0) return "the password also need to contain at least one lower character";
        else if (uppers < 2) return "the password need to have at least two uppers character";
        else if (digits < 2) return "the password need to have at least two digits";
        else return "";
    }
}
