package com.example.firebasechatapp.example.chatmodule.recentchat;

public class Member {

    private String email;
    private String name;
    private String user_id;

    public Member() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public Member(String email, String name, String user_id) {
        this.email = email;
        this.name = name;
        this.user_id = user_id;
    }
}
