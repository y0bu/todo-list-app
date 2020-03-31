package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.AdminService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdminLogin() {
        return "admin/login";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = {"adminName", "password"})
    public String postAdminLogin(@RequestParam String adminName, @RequestParam String password, Model model) {
        if (adminService.isExistByAdminNameAndPassword(adminName, password)) {
            model.addAttribute("accounts", accountService.getAll());
            return "admin/displayUsers";
        } else {
            model.addAttribute("alert", "password and/or admin name are incorrect");
            return "admin/login";
        }
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "goBack")
    public String postAdminGoBack(Model model) {
        model.addAttribute("accounts", accountService.getAll());
        return "admin/displayUsers";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "manageAccount")
    public String postAdminManageAndDisplayAccountTasks(@RequestParam String manageAccount, Model model) {
        int accountId = Integer.parseInt(manageAccount);
        Account account = accountService.findById(accountId);
        model.addAttribute("tasks", account.getTasks());
        model.addAttribute("accountId", account.getId());
        return "admin/displayTasksOfUsers";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "deleteAccount")
    public String postAdminDeleteAccount(@RequestParam String deleteAccount, Model model) {
        accountService.deleteById(Integer.parseInt(deleteAccount));
        model.addAttribute("accounts", accountService.getAll());
        return "admin/displayUsers";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "deleteTask")
    public String postAdminDeleteTask(@RequestParam String deleteTask, @RequestParam String accountId, Model model) {
        int idOfDeletingTask = 0;
        try {
            idOfDeletingTask = Integer.parseInt(deleteTask);
        } catch (NumberFormatException e) {
            model.addAttribute("accounts", accountService.getAll());
            return "admin/displayUsers";
        }
        taskService.delete(idOfDeletingTask);
        int accountIdInt = 0;
        try {
            accountIdInt = Integer.parseInt(accountId);
        } catch (NumberFormatException e) {
            model.addAttribute("accounts", accountService.getAll());
            return "admin/displayUsers";
        }
        Account account = accountService.findById(accountIdInt);
        model.addAttribute("tasks", account.getTasks());
        return "admin/displayTasksOfUsers";
    }
}
