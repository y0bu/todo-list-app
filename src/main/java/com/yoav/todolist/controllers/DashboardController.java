package com.yoav.todolist.controllers;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class DashboardController {

    private AccountService accountService;
    private TaskService taskService;

    @Autowired
    public DashboardController(AccountService accountService, TaskService taskService) {
        this.accountService = accountService;
        this.taskService = taskService;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String getDashboard(Model model, HttpSession session) {
        String usernameOfLoggedUser = (String)session.getAttribute("username");
        if (usernameOfLoggedUser == null) {
            return "redirect:/";
        }
        List<Task> tasks = accountService.findByUsername(usernameOfLoggedUser).getTasks();
        model.addAttribute("tasks", tasks);
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
        Task addedTask = new Task(addTask);
        String usernameOfLoggedUser = (String)session.getAttribute("username");
        Account thisAccount = accountService.findByUsername(usernameOfLoggedUser);
        taskService.add(addedTask, thisAccount);
        return "redirect:/dashboard";
    }
}
