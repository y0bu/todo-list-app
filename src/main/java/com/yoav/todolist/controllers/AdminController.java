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

@Controller
public class AdminController {

    private TaskService taskService;
    private AccountService accountService;
    private AdminService adminService;

    @Autowired
    public AdminController(TaskService taskService, AccountService accountService, AdminService adminService) {
        this.taskService = taskService;
        this.accountService = accountService;
        this.adminService = adminService;
        adminService.checkIfBaseAdminAccountExist();
    }

    @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
    public String getLoginAdmin() {
        return "admin/login";
    }

    @RequestMapping(value = "/admin/login", method = RequestMethod.POST, params = {"adminName", "password"})
    public String postValidateLoginAdmin(
            @RequestParam String adminName,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        if (adminService.isExistByAdminNameAndPassword(adminName, password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/admin";
        } else {
            model.addAttribute("alert", "the password and/or username are incorrect");
            return "admin/login";
        }
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAllAccountsMainAdminPanel(HttpSession session, Model model) {
        if (session.getAttribute("isAdmin") != null) {
            model.addAttribute("accounts", accountService.getAll());
            model.addAttribute("urlToRedirect", "/admin/");
            return "admin/displayUsers";
        } else {
            // TODO rise unauthorized error
            return "unauthorized";
        }
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "deleteAccount")
    public String postDeleteAccount(@RequestParam String deleteAccount, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        int idOfDeletingAccount = Integer.parseInt(deleteAccount);
        accountService.deleteById(idOfDeletingAccount);
        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/{usernameOfAccount}", method = RequestMethod.GET)
    public String getTasksOfNameOfAccounts(@PathVariable String usernameOfAccount, HttpSession session, Model model) {
        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        Account requestedAccount = accountService.findByUsername(usernameOfAccount);
        model.addAttribute("tasks", requestedAccount.getTasks());
        return "admin/displayTasksOfUsers";
    }

    @RequestMapping(value = "/admin/{usernameOfAccount}", method = RequestMethod.POST, params = "deleteTask")
    public String postDeleteTasksOfUsername(
            @RequestParam String deleteTask,
            @PathVariable String usernameOfAccount,
            HttpSession session) {

        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        int idOfDeletingTask = Integer.parseInt(deleteTask);
        taskService.delete(idOfDeletingTask);
        return "redirect:/admin/" + usernameOfAccount;
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.GET)
    public String getCreateNewAdmin(HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "unauthorized";
        else return "admin/createAdmin";
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST, params = {"adminName", "password"})
    public String postAddAdmin(
            @RequestParam String adminName,
            @RequestParam String password,
            HttpSession session,
            Model model,
            RedirectAttributes attributes) {

        if (session.getAttribute("isAdmin") == null) return "unauthorized";

        if (adminService.isExistByAdminName(adminName)) {
            model.addAttribute("alert", "admin name is already have been taken");
            return "admin/login";
        } else {
            Admin newAdmin = new Admin(adminName, password);
            adminService.add(newAdmin);
            attributes.addFlashAttribute("alert", "the admin added successfully you can log in now");
            return "redirect:/admin/login";
        }
    }

}


























