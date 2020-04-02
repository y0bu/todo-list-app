package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private AccountService accountService;
    private TaskService taskService;

    @Autowired
    public DashboardController(AccountService accountService, TaskService taskService) {
        this.accountService = accountService;
        this.taskService = taskService;
    }

    @RequestMapping(value = "/dashboard", params = "demoUser", method = RequestMethod.POST)
    public String getDashboardUserDemo() {
        return "dashboard/demoUser";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getDashboard(Model model, HttpSession session, @CookieValue(value = "username", defaultValue = "notSetCookie") String username) {
        if (!(username.equals("notSetCookie"))) {
            Account thisAccount = accountService.findByUsername(username);
            model.addAttribute("tasks", thisAccount.getTasks());
            return "dashboard/index";
        }

        String usernameOfLoggedUser = (String)session.getAttribute("username");
        if (usernameOfLoggedUser == null) {
            return "redirect:/";
        }

        Account thisAccount = accountService.findByUsername(usernameOfLoggedUser);
        model.addAttribute("tasks", thisAccount.getTasks());
        return "dashboard/index";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = "delete")
    public String postDashboardRemoveTask(@RequestParam String delete, HttpSession session) {
        String usernameOfLoggedUser = (String)session.getAttribute("username");
        Account thisAccount = accountService.findByUsername(usernameOfLoggedUser);
        int idOfDeletingTask = Integer.parseInt(delete);
        taskService.delete(idOfDeletingTask);
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = "addTask")
    public String postDashboardAddTask(@RequestParam String addTask, HttpSession session) {
        if (addTask.trim().equals("")) {
            return "redirect:/dashboard";
        }
        Task addedTask = new Task(addTask);
        String usernameOfLoggedUser = (String)session.getAttribute("username");
        Account thisAccount = accountService.findByUsername(usernameOfLoggedUser);
        taskService.add(addedTask, thisAccount);
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = "logout")
    public String postDashboardLogout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("username")) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        session.removeAttribute("username");
        return "redirect:/";
    }
}
