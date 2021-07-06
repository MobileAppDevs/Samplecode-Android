package com.bawa.inr.livechat;

import android.app.Activity;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.text.format.DateFormat;
import android.util.Log;

import com.bawa.inr.Constants;
import com.bawa.inr.InrApplication;
import com.bawa.inr.R;
import com.bawa.inr.notiffication.PushRetrofit;
import com.bawa.inr.rest.Apis;
import com.bawa.inr.util.ConversionUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHandler {

    private void showLog(String key, String value) {
        Log.e(key, value);
    }

    private String TAG = "ChatLog";

    //sending packet emit points
    private final String SWITCH_WHISPER_CHANNEL = "switch wishper channel";
    private final String LEAVE_WHISPER_CHANNEL = "leave wishper channel";
    private final String SWITCH_CHANNEL = "switch channel";
    private final String JOIN_CHANNEL = "join channel";
    private final String LEAVE_CHANNEL = "leave channel";
    private final String SEND_MESSAGE = "send message";
    private final String DELETE_MESSAGE = "delete message";

    //listener emit points

    private final String ONLINE_WHISPER_CHANNEL_STATUS = "channel wishper status";
    private final String ONLINE_CHANNEL_STATUS = "channel status";
    private final String SEND_MESSAGE_CALLBACK = "send";
    private final String DELETE_MESSAGE_CALLBACK = "delete";
    private final String NEW_USER_JOIN_CALLBACK = "join";
    private final String MESSAGE = "message";

    private Activity activity;
    private Socket mSocket;
    private ChatCallback chatCallback;
    private boolean isForceFullyDisconnect;


    public ChatHandler(Activity activity, ChatCallback chatCallback) {
        this.activity = activity;
        this.chatCallback = chatCallback;
    }


    public void connectChat() {
        try {
            mSocket = IO.socket(Constants.SOCKET_CHAT_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //standard event callback
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeOut);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        mSocket.connect();
    }

    public boolean isSocketConnected() {
        if (mSocket != null && mSocket.connected()) {
            return true;
        }
        return false;
    }

    private void addListenerAfterConnect() {
        //app required event callback
        if (mSocket != null) {
            mSocket.on(MESSAGE, onConnectedEvent);
            mSocket.on(NEW_USER_JOIN_CALLBACK, onNewUserJoinEvent);
            mSocket.on(SEND_MESSAGE_CALLBACK, onSendMessage);
            mSocket.on(DELETE_MESSAGE_CALLBACK, onDeleteMessage);
            mSocket.on(ONLINE_CHANNEL_STATUS, onUserOnlineStatus);
            mSocket.on(ONLINE_WHISPER_CHANNEL_STATUS, onUserOnlineStatus);
        }
    }

    public void disconnectChat(boolean isForceFullyDisconnect) {
        if (mSocket != null) {
            this.isForceFullyDisconnect = isForceFullyDisconnect;
            mSocket.disconnect();
        }
    }

    public void sendMessage(Object object) {
        if (mSocket != null) {
            mSocket.emit(SEND_MESSAGE, object.toString());
        }
    }

    public void deleteMessage(Object object) {
        if (mSocket != null) {
            mSocket.emit(DELETE_MESSAGE, object.toString());
        }
    }

    public void switchChat(Object object) {
        //call to connet new channel and removed from other all connected channel
        if (mSocket != null) {
            mSocket.emit(SWITCH_CHANNEL, object.toString());
        }
    }

    public void switchWhisper(Object object) {
        //call to connet new channel
        if (mSocket != null) {
            mSocket.emit(SWITCH_WHISPER_CHANNEL, object.toString());
        }
    }

    public void leaveWhisper(Object object) {
        if (mSocket != null) {
            mSocket.emit(LEAVE_WHISPER_CHANNEL, object.toString());
        }
    }

    public void leaveChat(Object object) {
        if (mSocket != null) {
            mSocket.emit(LEAVE_CHANNEL, object.toString());
        }
    }

    public void joinChat(Object object) {
        //call first time to know user have connected with group
        if (mSocket != null) {
            mSocket.emit(JOIN_CHANNEL, object.toString());
        }
    }

    private Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            showLog(TAG, "onSendMessage called");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatCallback.onNewMessage(args);
                }
            });
        }
    };
    private Emitter.Listener onDeleteMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            showLog(TAG, "onDeleteMessage called");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatCallback.onDeleteMessage(args);
                }
            });
        }
    };

    private Emitter.Listener onUserOnlineStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            showLog(TAG, "onUserOnlineStatus called");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatCallback.onlineStatus(args);
                }
            });
        }
    };

    private Emitter.Listener onConnectedEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onConnectedEvent" + args.toString());
        }
    };

    private Emitter.Listener onNewUserJoinEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onNewUserJoinEvent" + args.toString());
        }
    };
    private Emitter.Listener onLeaveChanel = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onLeaveChannel called");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onDisconnect called");
            if (!isForceFullyDisconnect && mSocket != null && !mSocket.connected()) {
                mSocket.connect();
            }
            chatCallback.onDisconect(args);
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onConnected called");
            addListenerAfterConnect();
            chatCallback.onConnect(args);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onConnectError called");
            if (!isForceFullyDisconnect && mSocket != null && !mSocket.connected()) {
                mSocket.connect();
            }
        }
    };

    private Emitter.Listener onTimeOut = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showLog(TAG, "onTimeOut called");
            if (!isForceFullyDisconnect && mSocket != null && !mSocket.connected()) {
                mSocket.connect();
            }
        }
    };


    public void reteriveChatMessageFromServer(String token, String userId, HashMap<String, String> map) {
        Call<ResponseBody> call = UploadMediaRetrofit.createService(Apis.class).getChatMessageHistory(Constants.AUTH_KEY, token, ConversionUtils.encrypt_for_body(userId), map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        if (response.code() == Constants.SUCCESS) {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            if (jsonResponse.has("data")) {
                                JSONObject jsonObject = new JSONObject(ConversionUtils.decrypt_text(jsonResponse.get("data").toString()));
                                if (chatCallback != null) {
                                    chatCallback.onChatHistory(true, jsonObject.has("readMessages") ? jsonObject.getJSONArray("readMessages") : null, jsonObject.has("unreadMessages") ? jsonObject.getJSONArray("unreadMessages") : null);
                                }
                            }
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "" + t);
            }
        });
    }

    public void reteriveChatMessageFromServerWithPagination(String token, String userId, HashMap<String, String> map) {
        Call<ResponseBody> call = UploadMediaRetrofit.createService(Apis.class).getChatMessageHistoryWithPagination(Constants.AUTH_KEY, token, ConversionUtils.encrypt_for_body(userId), map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response != null && response.body() != null) {
                        if (response.code() == Constants.SUCCESS) {
                            String s = response.body().string();
                            JSONObject jsonObject = new JSONObject(ConversionUtils.decrypt_text(new JSONObject(s).get("data").toString()));
                            if (chatCallback != null) {
                                chatCallback.onReadChatHistory(true, jsonObject.has("readMessages") ? jsonObject.getJSONArray("readMessages") : null);
                            }
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (chatCallback != null) {
                    chatCallback.onReadChatHistory(false, null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "" + t);
                if (chatCallback != null) {
                    chatCallback.onReadChatHistory(false, null);
                }
            }
        });
    }
}
