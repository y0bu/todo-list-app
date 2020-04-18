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

/**
 * this is a rest api controller
 * that offer operations(CRUD operations) to do on the task object(entity)
 * **/
@RestController
public class TasksController {

    private final TaskService taskService;
    private final AccountService accountService;

    @Autowired // dependency injection of singleton pattern
    public TasksController(TaskService taskService, AccountService accountService) {
        this.taskService = taskService;
        this.accountService = accountService;
    }

    /**
     * @return all the tasks from all accounts
     * **/
    @GetMapping("/api/task")
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    /**
     * @param username with this we get the account belong to that username and then
     * @return the tasks belong to the account that we just find
     * **/
    @GetMapping("/api/task/{username}")
    public List<Task> getAllTasksBelongToSpecifiedUsername(@PathVariable String username) {
        try {
            Account accountMadeRequestToGetAllHisTasks = accountService.findByUsername(username);
            return accountMadeRequestToGetAllHisTasks.getTasks();
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * in here we adding task to a specified username
     * @param username with this we getting account and with this account we setting
     *                the bidirectional many to one relationship with the new task that we pass as request body
     * @param task the new task that we going to attach to {"username"}
     * @return "task added successfully" when everything is fine and if record with username = {"username"} is not
     * existing we return "username do not exist" without adding task
     * **/
    @PostMapping("/api/task/{username}")
    public ResponseEntity addTask(@Valid @RequestBody Task task, @PathVariable String username) {
        try {
            Account accountWantToAddTask = accountService.findByUsername(username);
            taskService.add(task, accountWantToAddTask);
            return new ResponseEntity("task added successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            return new ResponseEntity("username do not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param taskNumberOrder
     * when you get the all the tasks of a certain user via /api/task/{username}/ you get them in certain order right?
     * so that order is the order number
     * so with given order number of a task and with given username of the account
     * we simply remove one of {"username"} task
     * @param username is the username that want to delete one of its own task
     * @return as usual if the username do exist it will add the task successfully and display to the body of the
     * response ("task deleted successfully") else it will display ("username do not exist")
     * **/
    @DeleteMapping("/api/task/{username}/{taskNumberOrder}")
    public ResponseEntity deleteTask(@PathVariable String username, @PathVariable int taskNumberOrder) {
        try {
            Account accountWantToDeleteTask = accountService.findByUsername(username);
            List<Task> tasks = accountWantToDeleteTask.getTasks();
            Task deleteTask = tasks.get(taskNumberOrder-1); // -1 for getting actual index because its not passed as array index
            taskService.delete(deleteTask.getId());
            return new ResponseEntity("task deleted successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            return new ResponseEntity("username do not exist or task not exist or serial number of task not correct", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param tasks is to just replace the old list of tasks belong to {"username"} with the given list of tasks
     * @param username is the username that want to replace his own list of tasks
     * @return as usual if the username do exist it will add the task successfully and display to the body of the
     * response ("tasks updated successfully") else it will display ("username do not exist")
     * **/
    @PutMapping("/api/task/{username}")
    public ResponseEntity updateTasks(@Valid @RequestBody List<Task> tasks, @PathVariable String username) {
        try {
            Account accountToBeUpdated = accountService.findByUsername(username);
            accountToBeUpdated.setTasks(tasks);
            accountService.update(accountToBeUpdated);
            return new ResponseEntity("tasks updated successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("problem");
            return new ResponseEntity("username do not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

}
