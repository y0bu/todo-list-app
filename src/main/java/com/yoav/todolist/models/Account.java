package com.yoav.todolist.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @OneToMany(
            mappedBy = "account",
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    private List<Task> tasks = new ArrayList<>();

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account() {
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setAccount(null);
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setAccount(this);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks.forEach(i -> i.setAccount(null));
        this.tasks.clear();

        tasks.forEach(i -> {
            i.setAccount(this);
            addTask(i);
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object account) {
        return ((Account)account).getUsername().equals(this.username);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
