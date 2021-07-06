package com.bawa.inr.livechat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ongraphh on 14-Jun-18.
 */

public interface ChatCallback {

    void onNewMessage(Object... args);

    void onDeleteMessage(Object... args);

    void onMessageHistory(Object... args);

    void onConnect(Object... args);

    void onDisconect(Object... args);

    void onlineStatus(Object... args);

    void onChatHistory(boolean isSuccess, JSONArray read, JSONArray unread);
    void onReadChatHistory(boolean isSuccess, JSONArray read);
}
