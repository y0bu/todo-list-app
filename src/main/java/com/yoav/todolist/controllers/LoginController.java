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

/**
 * login controller that provide normal functionality of
 * login page like alerting when username or/and password are incorrect
 * and remember me feature
 * **/
@Controller
public class LoginController {

    private final AccountService accountService; // for login validation

    @Autowired // dependency injection for singleton pattern
    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * @return simple login page
     * @param username to check if user is having cookie
     * **/
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin(@CookieValue(value = "username", defaultValue = "notSetCookie") String username) {

        // if the user have cookie
        // (when last time user logged in click on the checkbox remember me and cookie has been created)
        if (!(username.equals("notSetCookie"))) return "redirect:/dashboard";

        return "login/index";
    }

    /**
     * a simple post form request for checking if the given parameters are valid exist(exist record) in the database
     * @param username
     * @param password
     * @param session for setting the session that saying what username is logged in
     * @param model for sending alert to the login page like "not the correct username or/and password"
     * @return redirecting to the dashboard if logging has successfully done
     * @return displaying the login page again with the message "not the correct username or/and password" if login is not
     * successfully done
     * **/
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

    /**
     * the exact same login method "postLogin()" but one difference is that it add a cookie because user
     * clicked on "remember me" checkbox
     * @param response for adding the cookie
     * @param session for adding session for preventing errors in the dashboard operation like delete and add task
     * @param model for alerting user if login was unsuccessful message like: "not the correct username or/and password"
     * @param password
     * @param username
     * @return redirect to the dashboard if login was successful else return back to the login and say "not the correct username or/and password"
     * **/
    @RequestMapping(value = "/login", method = RequestMethod.POST, params = {"username", "password", "rememberMe"})
    public String postLoginWithRememberMe(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            HttpSession session,
            Model model) {

        if (accountService.isExistByUsernameAndPassword(username, password)) {
            Cookie cookie = new Cookie("username", username);
            // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
            cookie.setHttpOnly(true);
            cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
            cookie.setPath("/"); // global cookie accessible every where
            response.addCookie(cookie);
            session.setAttribute("username", username);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("alert", "not the correct username or/and password");
            return "login/index";
        }
    }
}
