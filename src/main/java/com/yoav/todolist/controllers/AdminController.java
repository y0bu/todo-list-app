package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Admin;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.AdminService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/*
* admin feature have the following operation:
*     1) delete account
*     2) delete task with specifying username
*     3) see all the accounts and their password
*     4) see all tasks of a specifying account
*     5) add a new admin
* */
@Controller
public class AdminController {

    private final TaskService taskService; // for deleting task operation
    private final AccountService accountService; // for seeing all the task of account and deleting account
    private final AdminService adminService; // for creating account and logging validation login

    @Autowired // dependency injection on the services for singleton purposes
    public AdminController(TaskService taskService, AccountService accountService, AdminService adminService) {
        this.taskService = taskService;
        this.accountService = accountService;
        this.adminService = adminService;
    }

    /*
    * get a simple admin login page
    * */
    @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
    public String getLoginAdmin() {
        return "admin/login";
    }

    /*
    * check the credential of the admin login
    * */
    @RequestMapping(value = "/admin/login", method = RequestMethod.POST, params = {"adminName", "password"})
    public String postValidateLoginAdmin(
            @RequestParam String adminName,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        /*
        * if the record with the specifying credential adminName and password are exist in the table "admins" grant
        * permission (with HTTP session cookie) to the admin panel
        * else display on the login page that the password or the adminName are incorrect
        * */
        if (adminService.isExistByAdminNameAndPassword(adminName, password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/admin";
        } else {
            model.addAttribute("alert", "the password and/or username are incorrect");
            return "admin/login";
        }
    }

    /*
    * if the session not set(means that permission not granted to user to use admin panel) then redirect to unauthorized error
    * else display list of all users and their info like passwords usernames and options like delete account
    * and getting all the tasks of the user
    * */
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAllAccountsMainAdminPanel(HttpSession session, Model model) {
        if (session.getAttribute("isAdmin") != null) {
            model.addAttribute("accounts", accountService.getAll());

            // this line is for specifying the request path(to the account tasks management("/admin/{username}"))
            // with the username of account
            model.addAttribute("urlToRedirect", "/admin/");

            return "admin/displayUsers";
        } else {
            return "unauthorized";
        }
    }

    /*
    * on the very first line of code of the method we check if
    * the user have permission to access that feature of deleting account
    * if user have permission so user should pass parameter to that http post request with the variable name "deleteAccount"
    * and that deleteAccount variable should contains the id of the account that we want to delete as string and then method
    * delete the account and then we redirecting to the main page of the admin panel
    * */
    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "deleteAccount")
    public String postDeleteAccount(@RequestParam String deleteAccount, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        int idOfDeletingAccount = Integer.parseInt(deleteAccount);
        accountService.deleteById(idOfDeletingAccount);
        return "redirect:/admin";
    }

    /*
    * this mapping is simply but requesting a full list of tasks belong to the specifying username there is also a
    * option to delete task with post request
    * on the very first line of code of the method we check if
    * the user have permission to access that feature of deleting account
    * */
    @RequestMapping(value = "/admin/{usernameOfAccount}", method = RequestMethod.GET)
    public String getTasksOfNameOfAccounts(@PathVariable String usernameOfAccount, HttpSession session, Model model) {
        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        Account requestedAccount = accountService.findByUsername(usernameOfAccount);
        model.addAttribute("tasks", requestedAccount.getTasks());
        return "admin/displayTasksOfUsers";
    }

     /*
     * on the very first line of code of the method we check if
     * the user have permission to access that feature of deleting account
     * this is post request to delete task by id. the id of the task that we want to delete we pass that as a string
     * */
    @RequestMapping(value = "/admin/{usernameOfAccount}", method = RequestMethod.POST, params = "deleteTask")
    public String postDeleteTasksOfUsername(
            @RequestParam String deleteTask,
            @PathVariable String usernameOfAccount,
            HttpSession session) {

        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        int idOfDeletingTask = Integer.parseInt(deleteTask);
        taskService.delete(idOfDeletingTask);
        return "redirect:/admin/" + usernameOfAccount; // redirecting back to the list of tasks of the specifying username
    }

    /*
    * a simple get request to the page(simple form page) where we can add admins
    * to add a admin you need to be admin so in the first line of code in the method we checking if the user that requesting
    * the request is have permission for the admin feature
    * */
    @RequestMapping(value = "/admin/add", method = RequestMethod.GET)
    public String getCreateNewAdmin(HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        else return "admin/createAdmin";
    }

    /*
    * this is post request of the form of the create admin page
    * here we checking if the admin name is already exist because we cant allow two identical adminName is the admins table
    * and then if there is no "adminName" in admins table we cant insert admin to the table admins
    * else if there is "adminName" in table admins we returning back to the add page and then telling admin that you need to
    * use other adminName because its already exist
    * */
    @RequestMapping(value = "/admin/add", method = RequestMethod.POST, params = {"adminName", "password"})
    public String postAddAdmin(
            @RequestParam String adminName,
            @RequestParam String password,
            HttpSession session,
            Model model,
            RedirectAttributes attributes) {

        if (session.getAttribute("isAdmin") == null) return "unauthorized"; // checking if user have permission to the admin feature

        if (adminService.isExistByAdminName(adminName)) {
            model.addAttribute("alert", "admin name is already have been taken");
            return "admin/createAdmin";
        } else {
            Admin newAdmin = new Admin(adminName, password);
            adminService.add(newAdmin);
            attributes.addFlashAttribute("alert", "the admin added successfully you can log in now");
            return "redirect:/admin/login"; // redirecting to login page because login was successfully done.
        }
    }

}
