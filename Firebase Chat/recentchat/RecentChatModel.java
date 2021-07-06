package com.example.firebasechatapp.example.chatmodule.recentchat;

import com.example.firebasechatapp.example.chatmodule.chat.Message;

public class RecentChatModel {

    private String name;
    private long timestamp;
    private String id;
    private String createrid;
    private boolean isGroup;
    private Message last_message;
    private Member members;

    public RecentChatModel() {
    }

    public RecentChatModel(String name, long timestamp, String id, String createrid, boolean isGroup, Message last_message, Member members) {
        this.name = name;
        this.timestamp = timestamp;
        this.id = id;
        this.createrid = createrid;
        this.isGroup = isGroup;
        this.last_message = last_message;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreaterid() {
        return createrid;
    }

    public void setCreaterid(String createrid) {
        this.createrid = createrid;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }

    public Member getMember() {
        return members;
    }

    public void setMember(Member member) {
        this.members = member;
    }
}
