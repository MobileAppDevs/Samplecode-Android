package com.example.firebasechatapp.example.chatmodule.users;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String email;
    private String user_id;

    public User(String name, String email, String user_id) {
        this.name = name;
        this.email = email;
        this.user_id = user_id;
    }

    public User() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
