package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import com.yoav.todolist.utils.CookiesUtils;
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

/**
* this is the main controller of the to-do list app is where all the task of user is displayed
* a user can do three operation on this page first user can delete a task second user can add a task and three user can logout
* logout just remove the cookie and the session
* **/
@Controller
public class DashboardController {

    private final AccountService accountService; // we use this service to just get all the task of a user that logged in
    private final TaskService taskService; // we use this service to remove and add tasks

    @Autowired // dependency injection for a singleton pattern
    public DashboardController(AccountService accountService, TaskService taskService) {
        this.accountService = accountService;
        this.taskService = taskService;
    }

    /**
     * @return the demo user feature where he have all the feature that a registered have except
     * he did not have a persistence storage on the database
     * **/
    @RequestMapping(value = "/dashboard", params = "demoUser", method = RequestMethod.POST)
    public String getDashboardUserDemo() {
        return "dashboard/demoUser";
    }

    /**
     * @param model for returning all user tasks
     * @param session for getting cookie session (to check if the user has been logged in)
     * @param username is the cookie that a user is creating when logged in and select the checkbox "Remember Me"
     * @implNote if the user did not have either session or weekly cookie user redirecting to the home page
     * @return if user is indeed have cookie or session so we get the account by name of it and returning a list of
     * tasks of that user (the method is fetching the username with the cookie or with the session cookie)
     * **/
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getDashboard(Model model,
                               HttpSession session,
                               @CookieValue(value = "username", defaultValue = "notSetCookie") String username) {

        String usernameOfLoggedUser;

        if ((usernameOfLoggedUser =
                getUsernameFromCookieOrSession((String)session.getAttribute("username"), username)) == null)
            return "redirect:/";

        Account thisAccount = accountService.findByUsername(usernameOfLoggedUser);
        model.addAttribute("tasks", thisAccount.getTasks());
        return "dashboard/index";
    }

    /**
     * the method simply delete a task by id of the task passed by
     * @param delete is a id of task in string format
     * @param session is for checking whether the user is logged in or not and if the task he want to
     * delete is belong to the user
     * @param username is for fetching cookie username account that logged in with the feature "remember me" and want to delete task
     * @return redirect to the to-do list tasks after deleting the task
     * **/
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = "delete")
    public String postDashboardRemoveTask(
            @RequestParam String delete,
            HttpSession session,
            @CookieValue(value = "username", defaultValue = "notSetCookie") String username) {

        String usernameOfAccountWantToDeleteTask;

        if ((usernameOfAccountWantToDeleteTask =
                getUsernameFromCookieOrSession((String)session.getAttribute("username"), username)) == null)
            return "redirect:/";

        // check if user that want to delete task is actually deleting one of his tasks because hacker can change the id
        // with inspect element and delete other tasks
        Account accountThatWantToDeleteTaskById = accountService.findByUsername(usernameOfAccountWantToDeleteTask);
        Task taskWantedToDelete = taskService.getById(Integer.parseInt(delete));

        if ( ! accountThatWantToDeleteTaskById.getTasks().contains(taskWantedToDelete) ) return "unauthorized";

        // delete task
        taskService.delete(
                taskWantedToDelete,
                accountThatWantToDeleteTaskById
        );

        return "redirect:/dashboard";
    }

    /**
     * @param addTask is the actual task that user want to add
     * @param session is for getting the username because to add a task we need the account that want to add task
     * @param username is for fetching cookie username account that logged in with the feature "remember me" and want to add task
     * @return redirect back to the dashboard after adding a task or if the task is not valid for insertion redirect without
     * adding
     * **/
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = "addTask")
    public String postDashboardAddTask(
            @RequestParam String addTask,
            HttpSession session,
            @CookieValue(value = "username", defaultValue = "notSetCookie") String username) {

        if ( ! isValidTask(addTask) ) return "redirect:/dashboard";

        String usernameOfAccountWantToAddTask;

        if ((usernameOfAccountWantToAddTask =
                getUsernameFromCookieOrSession((String)session.getAttribute("username"), username)) == null)
            return "redirect:/";

        // adding the task
        Task addedTask = new Task(addTask);
        Account thisAccount = accountService.findByUsername(usernameOfAccountWantToAddTask);
        taskService.add(addedTask, thisAccount);

        return "redirect:/dashboard";
    }

    /**
     * the method simply removing the session cookie and removing the cookie for logging out
     * @param session for removing the session cookie
     * @param request for getting all the cookies and removing the cookie that responsible for logging automatically
     * @param response for updating the removing cookie
     * @return redirect back to the home page after logout
     * **/
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, params = "logout")
    public String postDashboardLogout(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response) {

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("username")) {
                CookiesUtils.deleteCookie(cookie, response);
            }
        }

        session.removeAttribute("username");

        return "redirect:/";
    }

    /**
     * @return null if user is not using cookie or session else it return string with the username of the user that
     * login the string with the username is can be from the session or from the cookie so here we are checking which
     * method(session or cookie) and extracting the value of the username and then returning the value of the username
     * */
    private String getUsernameFromCookieOrSession(String session, String cookie) {
        if ( ! cookie.equals("notSetCookie") ) return cookie;

        // if the user is did not login so the session going to be null
        return session;
    }

    /**
     * valid task is task that has length less then 35 and the task need to not be empty or only spaces
     * */
    private boolean isValidTask(String task) {
        return ( ! ( task.trim().equals("") || task.length() > 35 ) );
    }

}
