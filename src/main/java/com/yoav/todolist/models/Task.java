package com.yoav.todolist.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name = "task")
    private String task;

    @ManyToOne
    @JoinColumn(name="account_id", nullable=false)
    private Account account;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_creation_task")
    private Date date;

    public Task(String task) {
        this.date = new Date();
        this.task = task;
    }

    public Task() {
        this.date = new Date();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        Task theEquator = (Task) o;
        if (theEquator.getId() == this.id) return true;
        else return false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", task='" + task + '\'' +
                '}';
    }
}
