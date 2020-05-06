package com.yoav.todolist.exceptions;

public class UsernameNotFoundException extends RuntimeException {

    public UsernameNotFoundException() {
        super("username not found or id of account not found exception");
    }

}
