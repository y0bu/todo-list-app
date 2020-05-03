package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * this is normal signup controller which can do the following things:
 *     1) check if the username that user insert is already exist
 *     2) check if the password that user insert is weak password
 *     3) mechanism of checking and requesting to type the password again for validate that user remember the password he type
 *
 *     and if one of this problem are appear we alerting user that we have a problem and he need to change some values
 *     in order to sign up
 * **/
@Controller
public class SignupController {

    // for inserting account and for validate that user not using username that already exist
    private final AccountService accountService;

    @Autowired // dependency injection for singleton pattern
    public SignupController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * simple get request for the sign up page
     * **/
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String getSignup() {
        return "signup/index";
    }

    /**
     * @param username the username(that user wants to insert into the database) coming from the form post request
     * @param password the password(that user wants to insert into the database) coming from the form post request
     * @param passwordAgain the password should be identical to password again its basically validation that make sure
     *                      that user really remember the password he/she typing
     * @param model to send to the html template alerts to the user like "the username is already have been taken" etc etc...
     * @param attributes we want to set also alerts to redirecting page we want to redirect to login when the sign up
     *                   successfully done and we want to set alert that saying "you signed up successfully now you can log in"
     *                   in the login page
     * @return we have two possibilities when returning first when the login not successfully done so we returning back to
     *                   the sign up page and telling user what exactly the problem is
     * @return the second possibility is when the sign up is successfully done so we redirecting to login page with greeting
     *                   message "you signed up successfully now you can log in"
     * **/
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String postSignup(
            Model model,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String passwordAgain,
            RedirectAttributes attributes) {

        // this string is should contain a note message about the weakness of the password for example
        // "you need at least one special character" etc etc...
        // the string gonna be empty when the password is strong enough
        String passwordAlert = PasswordUtils.chooseAlertWeakPassword(password);

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

}
