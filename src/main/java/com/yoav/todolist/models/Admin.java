package com.yoav.todolist.models;

import javax.persistence.*;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "password")
    private String password;

    public Admin(String adminName, String password) {
        this.adminName = adminName;
        this.password = password;
    }

    public Admin() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" +  + '\'' +
                ", password='" + adminName + '\'' +
                '}';
    }
}
