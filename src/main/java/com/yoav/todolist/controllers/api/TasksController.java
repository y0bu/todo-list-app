package com.yoav.todolist.controllers.api;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TasksController {

    private TaskService taskService;
    private AccountService accountService;

    @Autowired
    public TasksController(TaskService taskService, AccountService accountService) {
        this.taskService = taskService;
        this.accountService = accountService;
    }

    @GetMapping("/api/task")
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    @GetMapping("/api/task/{username}")
    public List<Task> getAllTasksBelongToSpecifiedUsername(@PathVariable String username) {
        try {
            Account accountMadeRequestToGetAllHisTasks = accountService.findByUsername(username);
            return accountMadeRequestToGetAllHisTasks.getTasks();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/api/task/{username}")
    public ResponseEntity addTask(@Valid @RequestBody Task task, @PathVariable String username) {
        try {
            Account accountWantToAddTask = accountService.findByUsername(username);
            taskService.add(task, accountWantToAddTask);
            return new ResponseEntity("task added successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new ResponseEntity("username do not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/api/task/{username}/{taskNumberOrder}")
    public ResponseEntity deleteTask(@PathVariable String username, @PathVariable int taskNumberOrder) {
        try {
            Account accountWantToDeleteTask = accountService.findByUsername(username);
            List<Task> tasks = accountWantToDeleteTask.getTasks();
            Task deleteTask = tasks.get(taskNumberOrder-1); // -1 for getting actual index
            taskService.delete(deleteTask.getId());
            return new ResponseEntity("task deleted successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new ResponseEntity("username do not exist or task not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/task/{username}")
    public ResponseEntity updateTasks(@Valid @RequestBody List<Task> tasks, @PathVariable String username) {
        try {
            Account accountToBeUpdated = accountService.findByUsername(username);
            accountToBeUpdated.setTasks(tasks);
            taskService.deleteAllByAccountId(accountToBeUpdated.getId());
            accountService.update(accountToBeUpdated);
            return new ResponseEntity("tasks updated successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("problem");
            return new ResponseEntity("username do not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

}
