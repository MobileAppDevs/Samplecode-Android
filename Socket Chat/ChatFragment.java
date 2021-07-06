package com.bawa.inr.livechat;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bawa.inr.Constants;
import com.bawa.inr.R;
import com.bawa.inr.base.BaseFragment;
import com.bawa.inr.model.BasicResponse;
import com.bawa.inr.model.MenuModel;
import com.bawa.inr.realmmodels.MyTeamRealm;
import com.bawa.inr.rest.Apis;
import com.bawa.inr.ui.MainActivity;
import com.bawa.inr.util.AppThemeResource;
import com.bawa.inr.util.ConversionUtils;
import com.bawa.inr.util.UtilMethods;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonTextView;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends BaseFragment implements ChatCallback, ChatAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView_chat)
    RecyclerView chatRecyclerView;
    @BindView(R.id.rl_root)
    View rl_root;
    @BindView(R.id.iv_attachment)
    ImageView iv_attachment;
    @BindView(R.id.rl_typingView)
    RelativeLayout rl_typingView;
    @BindView(R.id.et_message)
    EditText et_message;
    @BindView(R.id.iv_send)
    ImageView iv_send;
    @BindView(R.id.ll_attach)
    CardView ll_attchment;
    @BindView(R.id.ll_replyVew)
    View ll_replyVew;
    @BindView(R.id.replyViewBackground)
    View replyViewBackground;

    @BindView(R.id.editView)
    View editView;
    @BindView(R.id.replyView)
    View replyView;
    @BindView(R.id.edit_userImage)
    ImageView edit_userImage;
    @BindView(R.id.edit_tv_name)
    EmoticonTextView edit_tv_name;
    @BindView(R.id.edit_date)
    TextView edit_date;
    @BindView(R.id.edit_replyView)
    View edit_replyView;
    @BindView(R.id.edit_reply_name)
    EmoticonTextView edit_reply_name;
    @BindView(R.id.edit_reply_message)
    TextView edit_reply_message;
    @BindView(R.id.edit_replyCardView)
    View edit_replyCardView;
    @BindView(R.id.edit_replyImageView)
    ImageView edit_replyImageView;
    @BindView(R.id.edit_replyVideoSymbols)
    View edit_replyVideoSymbols;
    @BindView(R.id.edit_mediaImageCardView)
    View edit_mediaImageCardView;
    @BindView(R.id.edit_iv_mediaVew)
    ImageView edit_iv_mediaVew;
    @BindView(R.id.edit_videoSymbols)
    View edit_videoSymbols;
    @BindView(R.id.edit_tv_document_name)
    TextView edit_tv_document_name;
    @BindView(R.id.edit_document)
    TextView edit_document;
    @BindView(R.id.edit_iv_cancel)
    TextView edit_iv_cancel;

    @BindView(R.id.iv_mediaVew)
    ImageView iv_mediaVew;
    @BindView(R.id.mediaImageCardView)
    CardView mediaImageCardView;
    @BindView(R.id.reply_userImage)
    ImageView reply_userImage;
    @BindView(R.id.reply_date)
    TextView reply_date;
    @BindView(R.id.reply_tv_name)
    EmoticonTextView reply_tv_name;
    @BindView(R.id.tv_document_name)
    TextView tv_document_name;
    @BindView(R.id.tv_reply_message)
    EmoticonTextView tv_reply_message;
    @BindView(R.id.videoSymbols)
    TextView videoSymbols;
    @BindView(R.id.tv_message_date)
    TextView tv_message_date;

    @BindView(R.id.attachmentOption)
    LinearLayout attachmentOption;
    @BindView(R.id.optionView)
    LinearLayout optionView;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_image)
    TextView tv_image;
    @BindView(R.id.tv_video)
    TextView tv_video;
    @BindView(R.id.tv_document)
    TextView tv_document;
    @BindView(R.id.tv_camera)
    TextView tv_camera;
    @BindView(R.id.tv_gallery)
    TextView tv_gallery;
    @BindView(R.id.disableView)
    LinearLayout disableView;
    @BindView(R.id.disable_layer)
    RelativeLayout disable_layer;
    @BindView(R.id.bootomView)
    LinearLayout bootomView;

    private Handler handler;
    private Activity activity;
    private ChatAdapter chatAdapter;
    private ChatHandler chatHandler;
    private boolean isMessageEditing;
    private RealmChatModel edittingRealmChatModel;
    private String Channel_Name = "";
    public ArrayList<String> onlineUserList;
    public boolean isMessageHistoryLoading;
    private LinearLayoutManager linearLayoutManager;
    private boolean isVideoOpening;
    private MyTeamRealm selectedTeam;
    private String videoTextMessageHolder = "";


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreference.putString(Constants.LAST_VISIT_SCREEN, Constants.LAST_TALK_SCREEN);

        activity = getActivity();
        handler = new Handler();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        activity.registerReceiver(NetworkStateReceiver, intentFilter);

        if (Constants.CLEAR_ALL_FRAGMENT) {
            Constants.CLEAR_ALL_FRAGMENT = false;
            return;
        }

        selectedTeam = (MyTeamRealm) realmHandler.getKeyModel(MyTeamRealm.class, "isSeleted", true);

        if (selectedTeam != null && selectedTeam.isSync()) {

            init();

            //check for permission
            if (mediaUtil.hasStoragePermission(getActivity())) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
            chatHandler = new ChatHandler(activity, this);
            chatHandler.connectChat();
            realmHandler.resetUploadingMessage(getChannelName());
            if (UtilMethods.isInternetAvailable(activity, false) && !getChannelName().isEmpty()) {
                handleMessageHistory();
            }
            ((MainActivity) getActivity()).updatedSelectedTeamDetails(selectedTeam.getTeam_id());

        } else {
            rl_root.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedTeam != null && selectedTeam.isValid() && selectedTeam.isSync()) {
            chatHandler.switchChat(getChannelObject());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (selectedTeam != null && selectedTeam.isValid() && selectedTeam.isSync()) {
            chatHandler.leaveChat(getChannelObject());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            activity.unregisterReceiver(NetworkStateReceiver);
            if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).tv_action != null) {
                ((MainActivity) getActivity()).tv_action.setVisibility(View.GONE);
            }
            if (selectedTeam != null && selectedTeam.isValid() && selectedTeam.isSync()) {
                chatHandler.leaveChat(getChannelObject());
                chatHandler.disconnectChat(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setThemeColor() {
        iv_attachment.setColorFilter(AppThemeResource.getAppThemeResource(activity).getNonSelectedIconColor(), PorterDuff.Mode.SRC_IN);
        iv_send.setColorFilter(AppThemeResource.getAppThemeResource(activity).getNonSelectedIconColor(), PorterDuff.Mode.SRC_IN);

        attachmentOption.setBackground(AppThemeResource.getAppThemeResource(activity).getAttachmentDrawableColor());
        optionView.setBackground(AppThemeResource.getAppThemeResource(activity).getAttachmentDrawableColor());
        tv_cancel.setBackground(AppThemeResource.getAppThemeResource(activity).getAttachmentDrawableColor());
        tv_cancel.setTextColor(AppThemeResource.getAppThemeResource(activity).getUnSelectedWheelTextColor());
        tv_image.setTextColor(AppThemeResource.getAppThemeResource(activity).getUnSelectedWheelTextColor());
        tv_video.setTextColor(AppThemeResource.getAppThemeResource(activity).getUnSelectedWheelTextColor());
        tv_document.setTextColor(AppThemeResource.getAppThemeResource(activity).getUnSelectedWheelTextColor());
        tv_camera.setTextColor(AppThemeResource.getAppThemeResource(activity).getUnSelectedWheelTextColor());
        tv_gallery.setTextColor(AppThemeResource.getAppThemeResource(activity).getUnSelectedWheelTextColor());
    }

    private void handleMessageHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
                map.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
                chatHandler.reteriveChatMessageFromServer(getToken(), getUserId(), map);
            }
        }).start();
    }

    public void getReadMessageHistory(final String messageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
                map.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
                map.put("message_id", ConversionUtils.encrypt_for_body(messageId));
                chatHandler.reteriveChatMessageFromServerWithPagination(getToken(), getUserId(), map);
            }
        }).start();


    }

    private String getUserId() {
        return getUserid();
    }

    private String getChannelName() {
        return Channel_Name;
    }

    private String getParentId() {
        if (ll_replyVew.getVisibility() == View.VISIBLE && edittingRealmChatModel != null) {
            return edittingRealmChatModel.getMessageUniqueId();
        } else {
            return "0";
        }
    }

    private String getIsEdited() {
        if (isMessageEditing && edittingRealmChatModel != null) {
            return "1";
        } else {
            return "0";
        }
    }

    private String getTypingMessage() {
        return UtilMethods.getEncodedMessage(et_message.getText().toString().trim());
    }

    private String getChatType() {
        return ChatConstants.CHAT_GROUP;
    }

    private void init() {

        setThemeColor();

        if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).tv_action != null) {
            ((MainActivity) getActivity()).tv_action.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).tv_action.setText(UtilMethods.getDecodedMessage(selectedTeam.getTeam_name()));
            Channel_Name = selectedTeam.getTeam_id();
        }

        et_message.setHint(UtilMethods.getArialItalicFont(getActivity(), R.string.type_here));
        rl_typingView.setBackground(AppThemeResource.getAppThemeResource(activity).getChatInputBoxDrawable());

        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        onlineUserList = new ArrayList<>();

        RealmResults<RealmChatModel> realmResults = realmHandler.getRealm().where(RealmChatModel.class).equalTo("channelName", getChannelName()).findAllSortedAsync("timeStamp", Sort.DESCENDING);
        chatAdapter = new ChatAdapter(realmResults, activity, realmHandler, mediaUtil, this, getChannelName(), getUserId());
        chatRecyclerView.setAdapter(chatAdapter);

        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        hideheader();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        if (chatAdapter != null && chatAdapter.getItemCount() > 5)
                            showheader();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.findFirstVisibleItemPosition() != -1) {
                    tv_message_date.setText(chatAdapter.getMessgeDate(linearLayoutManager.findFirstVisibleItemPosition()));
                }
            }
        });
    }

    private void showheader() {
        if (tv_message_date.getVisibility() == View.GONE) {
            tv_message_date.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInDown).duration(600).playOn(tv_message_date);
        }
    }

    private void hideheader() {
        if (tv_message_date != null && tv_message_date.getVisibility() == View.VISIBLE) {
            try {
                YoYo.with(Techniques.SlideOutUp).duration(600).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        if (tv_message_date != null)
                            tv_message_date.setVisibility(View.GONE);
                    }
                }).playOn(tv_message_date);
            } catch (NullPointerException e) {

            }
        }
    }

    @OnClick(R.id.iv_attachment)
    public void onAttachmentClicked() {
        UtilMethods.hideKeyBoard(getActivity());
        if (ll_attchment.getVisibility() == View.GONE)
            if (!isMessageEditing)
                showchoose();
    }

    @OnClick(R.id.tv_cancel)
    public void clickAttachmentCancel() {
        if (attachmentOption.getVisibility() == View.VISIBLE) {
            showAttachmentOption();
        } else if (ll_attchment.getVisibility() == View.VISIBLE) {
            showchoose();
        }
    }

    @OnClick(R.id.tv_camera)
    public void clickAttachmentCamera() {
        showchoose();
        if (isVideoOpening)
            handleAttachmentCameraClick(false);
        else
            handleAttachmentCameraClick(true);

    }

    @OnClick(R.id.tv_gallery)
    public void clickAttachmentGallery() {
        showchoose();
        if (isVideoOpening)
            handleAttachmentClick(false);
        else
            handleAttachmentClick(true);
    }

    @OnClick(R.id.tv_image)
    public void clickAttachmentPhoto() {
        isVideoOpening = false;
        showAttachmentOption();
    }

    @OnClick(R.id.tv_video)
    public void clickAttachmentVideo() {
        isVideoOpening = true;
        showAttachmentOption();
    }

    @OnClick(R.id.tv_document)
    public void clickAttachmentDocument() {
        showchoose();
        mediaUtil.openDocumentGallery(new MediaUtil.SelectedDocumetCallback() {
            @Override
            public void response(boolean status, String fileName, String path) {
                if (status) {
                    File file = new File(path);
                    mediaUtil.scanFileForGallery(activity, file);
                    String unique = String.valueOf(System.currentTimeMillis());
                    RealmChatModel realmChatModel = sendMediaMessage(unique, ChatConstants.MESSAGE_DOCS, fileName, file, null);
                    mediaFileUpload(unique, realmChatModel, file);
                    hideReplyMessageView();
                    et_message.setText("");
                }
            }
        });
    }

    private void showchoose() {
        if (ll_attchment.getVisibility() == View.GONE) {
            ll_attchment.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp).duration(200).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    //  disable_layer.setVisibility(View.VISIBLE);
                }
            }).playOn(ll_attchment);
        } else {
            YoYo.with(Techniques.SlideOutDown).duration(300).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    //   disable_layer.setVisibility(View.GONE);
                    ll_attchment.setVisibility(View.GONE);
                    attachmentOption.setVisibility(View.GONE);
                    disableView.setVisibility(View.GONE);
                }
            }).playOn(ll_attchment);
        }
    }

    private void showAttachmentOption() {
        if (attachmentOption.getVisibility() == View.GONE) {
            attachmentOption.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp).duration(200).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    disableView.setVisibility(View.VISIBLE);
                }
            }).playOn(attachmentOption);
        } else {
            YoYo.with(Techniques.SlideOutDown).duration(300).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    disableView.setVisibility(View.GONE);
                    attachmentOption.setVisibility(View.GONE);
                }
            }).playOn(attachmentOption);
        }
    }

    @OnClick(R.id.iv_cancel)
    public void onReplyViewCancelClicked() {
        hideReplyMessageView();
        et_message.setText("");
    }

    @OnClick(R.id.edit_iv_cancel)
    public void onEditViewCancelClicked() {
        hideReplyMessageView();
        isMessageEditing = false;
        edittingRealmChatModel = null;
        et_message.setText("");
    }

    @OnClick(R.id.iv_send)
    public void onSendClicked() {
        isValidText();
        if (replyView.getVisibility() == View.VISIBLE) {
            if (chatHandler.isSocketConnected()) {
                replyTextMessageOnServer();
            } else {
                saveReplyTextMessage();
            }
        } else {
            if (isMessageEditing && editView.getVisibility() == View.VISIBLE) {
                if (!edittingRealmChatModel.getMessage().equals(getTypingMessage()))
                    if (chatHandler.isSocketConnected()) {
                        editTextMessageOnServer();
                    } else {
                        saveEditLocalMessage();
                    }
            } else {
                String time = ChatDateFormat.generateTimestampInUTC();
                if (chatHandler.isSocketConnected()) {
                    sendTextMessageOnServer(time);
                } else {
                    saveLocalMessage(time);
                }
            }
        }
    }

    private void sendTextMessageOnServer(String time) {
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
            json.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
            json.put("message", ConversionUtils.encrypt_for_body(getTypingMessage()));
            json.put("parent_id", ConversionUtils.encrypt_for_body("0")); //used only reply case
            json.put("chat_type", ConversionUtils.encrypt_for_body(getChatType()));
            json.put("message_type", ConversionUtils.encrypt_for_body(ChatConstants.MESSAGE_TEXT));
            json.put("is_edited", ConversionUtils.encrypt_for_body("0"));
            json.put("created_at", ConversionUtils.encrypt_for_body(time));
            json.put("updated_at", ConversionUtils.encrypt_for_body(time));
            chatHandler.sendMessage(json);
            et_message.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveLocalMessage(String messageId) {
        RealmChatModel realmChatModel = new RealmChatModel();
        realmChatModel.setUserId(getUserId());
        realmChatModel.setMessage(getTypingMessage());
        realmChatModel.setMessageUniqueId(messageId);
        realmChatModel.setChatType(getChatType());
        realmChatModel.setChannelName(getChannelName());
        realmChatModel.setParentId(getParentId());
        realmChatModel.setIsEdited("0");
        realmChatModel.setMessageType(ChatConstants.MESSAGE_TEXT);
        realmChatModel.setMessageStatus(ChatConstants.MESSAGE_NORMAL_STATUS);
        realmChatModel.setTimeStamp(messageId);
        realmChatModel.setUpdatedTimeStamp(messageId);
        realmChatModel.setUploading(false);
        realmChatModel.setDownloading(false);
        realmChatModel.setError(false);
        realmChatModel.setOnline(false);
        realmChatModel.setLocalMessage(true);
        realmHandler.insertMessageInRealm(realmChatModel);
        chatRecyclerView.scrollToPosition(0);
        chatAdapter.notifyDataSetChanged();
        hideReplyMessageView();
        et_message.setText("");
    }

    private void saveEditLocalMessage() {
        RealmChatModel realmChatModel = new RealmChatModel();
        realmChatModel.setUserId(edittingRealmChatModel.getUserId());
        realmChatModel.setMessage(getTypingMessage());
        realmChatModel.setMessageUniqueId(edittingRealmChatModel.getMessageUniqueId());
        realmChatModel.setChatType(edittingRealmChatModel.getChatType());
        realmChatModel.setChannelName(edittingRealmChatModel.getChannelName());
        realmChatModel.setParentId(edittingRealmChatModel.getParentId());
        if (edittingRealmChatModel.isLocalMessage()) {
            realmChatModel.setIsEdited("0");
        } else {
            realmChatModel.setIsEdited("1");
        }
        realmChatModel.setMessageType(edittingRealmChatModel.getMessageType());
        realmChatModel.setMessageStatus(edittingRealmChatModel.getMessageStatus());
        realmChatModel.setTimeStamp(edittingRealmChatModel.getTimeStamp());
        realmChatModel.setUpdatedTimeStamp(ChatDateFormat.generateTimestampInUTC());
        realmChatModel.setReplyList(edittingRealmChatModel.getReplyList());
        realmChatModel.setForwardedOriginalMessage(edittingRealmChatModel.getForwardedOriginalMessage());
        realmChatModel.setIsForwarded(edittingRealmChatModel.getIsForwarded());
        realmChatModel.setUploading(false);
        realmChatModel.setDownloading(false);
        realmChatModel.setError(false);
        realmChatModel.setOnline(false);
        realmChatModel.setLocalMessage(true);
        realmHandler.insertMessageInRealm(realmChatModel);
        chatRecyclerView.scrollToPosition(0);
        chatAdapter.notifyDataSetChanged();
        hideReplyMessageView();
        et_message.setText("");
    }


    private void editTextMessageOnServer() {
        try {
            JSONObject json = new JSONObject();
            json.put("parent_id", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getParentId()));
            json.put("user_id", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getUserId()));
            json.put("channel_name", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getChannelName()));
            json.put("chat_type", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getChatType()));
            json.put("message_type", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getMessageType()));
            json.put("message_id", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getMessageUniqueId()));
            json.put("is_edited", ConversionUtils.encrypt_for_body("1"));
            json.put("message", ConversionUtils.encrypt_for_body(getTypingMessage()));
            json.put("created_at", ConversionUtils.encrypt_for_body(edittingRealmChatModel.getTimeStamp()));
            json.put("updated_at", ConversionUtils.encrypt_for_body(ChatDateFormat.generateTimestampInUTC()));
            chatHandler.sendMessage(json);
            et_message.setText("");
            hideReplyMessageView();
            isMessageEditing = false;
            edittingRealmChatModel = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void replyTextMessageOnServer() {
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
            json.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
            json.put("message", ConversionUtils.encrypt_for_body(getTypingMessage()));
            json.put("parent_id", ConversionUtils.encrypt_for_body(getParentId())); //used only reply case
            json.put("chat_type", ConversionUtils.encrypt_for_body(getChatType()));
            json.put("is_edited", ConversionUtils.encrypt_for_body("0"));
            json.put("message_type", ConversionUtils.encrypt_for_body(ChatConstants.MESSAGE_TEXT));
            String time = ChatDateFormat.generateTimestampInUTC();
            json.put("created_at", ConversionUtils.encrypt_for_body(time));
            json.put("updated_at", ConversionUtils.encrypt_for_body(time));
            chatHandler.sendMessage(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveReplyTextMessage() {
        try {
            String messageId = ChatDateFormat.generateTimestampInUTC();

            RealmChatModel realmChatModel = new RealmChatModel();
            realmChatModel.setUserId(getUserId());
            realmChatModel.setMessage(getTypingMessage());
            realmChatModel.setMessageUniqueId(messageId);
            realmChatModel.setChatType(getChatType());
            realmChatModel.setChannelName(getChannelName());
            realmChatModel.setParentId(getParentId());
            realmChatModel.setIsEdited("0");
            realmChatModel.setMessageType(ChatConstants.MESSAGE_TEXT);
            realmChatModel.setMessageStatus(ChatConstants.MESSAGE_NORMAL_STATUS);
            realmChatModel.setTimeStamp(messageId);
            realmChatModel.setUpdatedTimeStamp(messageId);
            realmChatModel.setUploading(false);
            realmChatModel.setDownloading(false);
            realmChatModel.setError(false);
            realmChatModel.setOnline(false);
            realmChatModel.setLocalMessage(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message_id", edittingRealmChatModel.getMessageUniqueId());
            jsonObject.put("user_id", edittingRealmChatModel.getUserId());
            jsonObject.put("user_name", edittingRealmChatModel.getUserName());
            jsonObject.put("message", edittingRealmChatModel.getMessage());
            jsonObject.put("message_type", edittingRealmChatModel.getMessageType());
            jsonObject.put("filelink", edittingRealmChatModel.getFilelink());
            jsonObject.put("thumbnail", edittingRealmChatModel.getThumbnail());
            jsonObject.put("filename", edittingRealmChatModel.getFilelink());
            realmChatModel.setReplyList(jsonObject.toString());

            realmHandler.insertMessageInRealm(realmChatModel);
            chatRecyclerView.scrollToPosition(0);
            chatAdapter.notifyDataSetChanged();
            hideReplyMessageView();
            et_message.setText("");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onNewMessage(Object... args) {
        try {
            JSONObject jsonObject = new JSONObject(args[0].toString());
            JSONObject messageJson = (JSONObject) jsonObject.get("message");
            Log.e("message Received", "" + messageJson.toString());
            RealmChatModel realmChatModel = new RealmChatModel();
            realmChatModel.setUserId(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("user_id"))));
            realmChatModel.setUserName(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("user_name"))));
            realmChatModel.setUserProfile(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("profile_picture"))));
            realmChatModel.setMessage(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("message"))));
            realmChatModel.setMessageUniqueId(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("message_id"))));
            realmChatModel.setChatType(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("chat_type"))));
            realmChatModel.setChannelName(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("channel_name"))));
            realmChatModel.setParentId(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("parent_id"))));
            realmChatModel.setIsEdited(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("is_edited"))));
            realmChatModel.setIsFlagged(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("is_flagged"))));
            realmChatModel.setMessageType(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("message_type"))));
            realmChatModel.setMessageStatus(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("message_status"))));
            realmChatModel.setUpdatedTimeStamp(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("updated_at"))));
            realmChatModel.setTimeStamp(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("created_at"))));
            realmChatModel.setThumbnail(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("thumbnail"))));
            realmChatModel.setFilelink(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("filelink"))));
            if (messageJson.has("filename"))
                realmChatModel.setFileName(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("filename"))));
            realmChatModel.setReplyList(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("replyList"))));
            realmChatModel.setUploading(false);
            realmChatModel.setDownloading(false);
            realmChatModel.setError(false);
            realmChatModel.setOnline(true);
            realmChatModel.setLocalMessage(false);

            if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_TEXT)) {
                //delete offline save message and insert new callback message
                if (realmChatModel.getUserId().equals(getUserid())) {
                    realmHandler.deleteMessageInRealm(realmChatModel.getTimeStamp());
                }
            } else {
                RealmChatModel tempModel = realmHandler.getMessageInRealm(realmChatModel.getMessageUniqueId());
                if (tempModel != null) {
                    realmChatModel.setLocalMediaPath(tempModel.getLocalMediaPath());
                    realmChatModel.setLocalMediaThumbnail(tempModel.getLocalMediaThumbnail());
                }
            }
            realmHandler.insertMessageInRealm(realmChatModel);
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(0);
            ll_replyVew.setVisibility(View.GONE);
            replyViewBackground.setBackground(null);
            et_message.setText("");
            RealmChatModel mediaModel = chatAdapter.getItem(0);
            if (!mediaModel.getUserId().equals(getUserId())) {
                if (sharedPreference.getBoolean("cellular_media_download")) {
                    if (UtilMethods.isConnectedMobile(getActivity())) {
                        downloadAttachment(false, 0, mediaModel);
                    }
                } else {
                    if (UtilMethods.isConnectedWifi(getActivity())) {
                        downloadAttachment(false, 0, mediaModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteMessage(Object... args) {
        try {
            Log.e("Delete Callback", "" + args[0]);
            JSONObject jsonObject = new JSONObject(args[0].toString());
            JSONObject messageJson = (JSONObject) jsonObject.get("message");
            RealmChatModel realmChatModel = realmHandler.getMessageInRealm(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("message_id"))));

            realmHandler.getRealm().beginTransaction();
            realmChatModel.setDeleted(false);
            realmChatModel.setMessageStatus(ChatConstants.MESSAGE_DELETE_STATUS);
            realmChatModel.setUpdatedTimeStamp(ConversionUtils.decrypt_text(String.valueOf(messageJson.get("updated_at"))));
            realmHandler.getRealm().commitTransaction();

            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageHistory(Object... args) {

    }

    @Override
    public void onConnect(Object... args) {
        chatHandler.joinChat(getChannelObject());
        chatHandler.switchChat(getChannelObject());
        if (chatHandler.isSocketConnected()) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<RealmChatModel> realmChatModels = (ArrayList<RealmChatModel>) realmHandler.getRealm().copyFromRealm(realmHandler.getAllUnsyncMessages(getChannelName()));
                            if (realmChatModels != null) {
                                for (int i = 0; i < realmChatModels.size(); i++) {
                                    Log.e("unsync message", "" + i);
                                    if (realmChatModels.get(i).getMessageType().equals(ChatConstants.MESSAGE_TEXT)) {
                                        //complete text message pending
                                        sendLocalTextMessage(realmChatModels.get(i));
                                    } else {
                                        //complete media message pending
                                        mediaFileUpload(realmChatModels.get(i).getMessageUniqueId(), realmChatModels.get(i), new File(realmChatModels.get(i).getLocalMediaPath()));
                                    }
                                }
                            }

                            ArrayList<RealmChatModel> localDeleteMessages = (ArrayList<RealmChatModel>) realmHandler.getRealm().copyFromRealm(realmHandler.getAllDeletedMessages(getChannelName()));
                            if (localDeleteMessages != null) {
                                for (int i = 0; i < localDeleteMessages.size(); i++) {
                                    Log.e("unsync delete message", "" + i);
                                    sendLocalDeletedMessages(localDeleteMessages.get(i));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private void sendLocalDeletedMessages(final RealmChatModel realmChatModel) {
        try {
            JSONObject json = new JSONObject();
            json.put("updated_at", ConversionUtils.encrypt_for_body(realmChatModel.getUpdatedTimeStamp()));
            json.put("user_id", ConversionUtils.encrypt_for_body(realmChatModel.getUserId()));
            json.put("channel_name", ConversionUtils.encrypt_for_body(realmChatModel.getChannelName()));
            json.put("message_id", ConversionUtils.encrypt_for_body(realmChatModel.getMessageUniqueId()));
            chatHandler.deleteMessage(json);
            realmChatModel.setDeleted(false);
            realmHandler.insertMessageInRealm(realmChatModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendLocalTextMessage(final RealmChatModel realmChatModel) {
        try {

            Log.e("text time", ":" + realmChatModel.getTimeStamp());
            JSONObject json = new JSONObject();
            json.put("user_id", ConversionUtils.encrypt_for_body(realmChatModel.getUserId()));
            json.put("channel_name", ConversionUtils.encrypt_for_body(realmChatModel.getChannelName()));
            json.put("message", ConversionUtils.encrypt_for_body(realmChatModel.getMessage()));
            json.put("parent_id", ConversionUtils.encrypt_for_body(realmChatModel.getParentId())); //used only reply case
            json.put("chat_type", ConversionUtils.encrypt_for_body(realmChatModel.getChatType()));
            json.put("message_type", ConversionUtils.encrypt_for_body(realmChatModel.getMessageType()));
            json.put("created_at", ConversionUtils.encrypt_for_body(realmChatModel.getTimeStamp()));
            json.put("updated_at", ConversionUtils.encrypt_for_body(realmChatModel.getUpdatedTimeStamp()));
            json.put("is_edited", ConversionUtils.encrypt_for_body(realmChatModel.getIsEdited()));
            if (realmChatModel.getIsEdited() != null && realmChatModel.getIsEdited().equals(ChatConstants.MESSAGE_EDITED)) {
                json.put("message_id", ConversionUtils.encrypt_for_body(realmChatModel.getMessageUniqueId()));
            }
            if (realmChatModel.getParentId() != null && !realmChatModel.getParentId().equals("0")) {
                json.put("message_id", ConversionUtils.encrypt_for_body(realmChatModel.getMessageUniqueId()));
            }
            chatHandler.sendMessage(json);
            realmChatModel.setLocalMessage(false);
            realmHandler.insertMessageInRealm(realmChatModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDisconect(Object... args) {

    }

    @Override
    public void onlineStatus(Object... args) {
        onlineUserList.clear();
        try {
            if (args.length > 0 && new JSONObject(args[0].toString()).has("channel_users") && new JSONObject(ConversionUtils.decrypt_text(new JSONObject(args[0].toString()).get("channel_users").toString())).has("data")) {
                final JSONArray users = new JSONObject(ConversionUtils.decrypt_text(new JSONObject(args[0].toString()).get("channel_users").toString())).getJSONArray("data");
                if (users.length() > 0) {
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject jsonObject = users.getJSONObject(i);
                        if (jsonObject.get("user_channel_status").toString().equals("1")) {
                            onlineUserList.add(jsonObject.get("user_id").toString());
                        }
                    }
                    realmHandler.getRealm().beginTransaction();
                    for (RealmChatModel realmChatModel : chatAdapter.getData()) {
                        if (onlineUserList.contains(realmChatModel.getUserId())) {
                            realmChatModel.setOnline(true);
                        } else {
                            realmChatModel.setOnline(false);
                        }
                    }
                    realmHandler.getRealm().commitTransaction();
                    chatAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadChatHistory(boolean isSuccess, JSONArray read) {
        try {
            if (isSuccess) {
                for (int i = 0; read != null && i < read.length(); i++) {
                    handleServerMessage(true, (JSONObject) read.get(i));
                    chatAdapter.notifyDataSetChanged();
                }
            }
        } catch (Throwable r) {
            r.printStackTrace();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isMessageHistoryLoading = false;
            }
        }, 400);
    }

    @Override
    public void onChatHistory(boolean isSuccess, JSONArray read, JSONArray unread) {
        try {
            if (isSuccess) {
                sharedPreference.putString(Constants.FIRST_TIME_CHAT_LOADED, "Success");

                for (int i = 0; read != null && i < read.length(); i++) {
                    handleServerMessage(true, (JSONObject) read.get(i));
                }
                for (int i = 0; unread != null && i < unread.length(); i++) {
                    handleServerMessage(false, (JSONObject) unread.get(i));
                }
                chatAdapter.notifyDataSetChanged();
            }
        } catch (Throwable r) {
            r.printStackTrace();
        }
    }

    private void handleServerMessage(boolean readStatus, JSONObject messageJson) throws JSONException {
        RealmChatModel realmChatModel = new RealmChatModel();
        realmChatModel.setUserId(String.valueOf(messageJson.get("user_id")));
        realmChatModel.setUserName(String.valueOf(messageJson.get("user_name")));
        realmChatModel.setUserProfile(String.valueOf(messageJson.get("profile_picture")));
        realmChatModel.setMessage(String.valueOf(messageJson.get("message")));
        realmChatModel.setMessageUniqueId(String.valueOf(messageJson.get("message_id")));
        realmChatModel.setChatType(String.valueOf(messageJson.get("chat_type")));
        realmChatModel.setChannelName(String.valueOf(messageJson.get("channel_name")));
        realmChatModel.setParentId(String.valueOf(messageJson.get("parent_id")));
        realmChatModel.setIsEdited(String.valueOf(messageJson.get("is_edited")));
        realmChatModel.setIsFlagged(String.valueOf(messageJson.get("is_flagged")));
        realmChatModel.setMessageType(String.valueOf(messageJson.get("message_type")));
        realmChatModel.setMessageStatus(String.valueOf(messageJson.get("message_status")));
        realmChatModel.setUpdatedTimeStamp(String.valueOf(messageJson.get("updated_at")));
        realmChatModel.setTimeStamp(String.valueOf(messageJson.get("created_at")));
        realmChatModel.setThumbnail(String.valueOf(messageJson.get("thumbnail")));
        realmChatModel.setFilelink(String.valueOf(messageJson.get("filelink")));
        if (messageJson.has("filename"))
            realmChatModel.setFileName(String.valueOf(messageJson.get("filename")));
        realmChatModel.setReplyList(String.valueOf(messageJson.get("replyList")));
        if (messageJson.has("forwardedOriginalMessage"))
            realmChatModel.setForwardedOriginalMessage(String.valueOf(messageJson.get("forwardedOriginalMessage")));
        if (messageJson.has("is_forwarded"))
            realmChatModel.setIsForwarded(String.valueOf(messageJson.get("is_forwarded")));
        realmChatModel.setUploading(false);
        realmChatModel.setDownloading(false);
        realmChatModel.setError(false);
        if (onlineUserList != null && onlineUserList.contains(realmChatModel.getUserId())) {
            realmChatModel.setOnline(true);
        } else {
            realmChatModel.setOnline(false);
        }
        realmChatModel.setLocalMessage(false);
        realmChatModel.setReadMessage(readStatus);

        RealmChatModel tempModel = realmHandler.getMessageInRealm(realmChatModel.getMessageUniqueId());
        if (tempModel != null) {
            realmChatModel.setLocalMediaPath(tempModel.getLocalMediaPath());
            realmChatModel.setLocalMediaThumbnail(tempModel.getLocalMediaThumbnail());
            realmChatModel.setOnline(tempModel.isOnline());
        }
        if (realmChatModel.getUserId().equals(getUserid())) {
            realmHandler.deleteMessageInRealm(realmChatModel.getTimeStamp());
        }
        realmHandler.insertMessageInRealm(realmChatModel);
        if (sharedPreference.getBoolean("cellular_media_download")) {
            if (UtilMethods.isConnectedMobile(getActivity())) {
                BackgroundAutoDownload.autoDownloadMedia(getActivity(), realmChatModel);
            }
        } else {
            if (UtilMethods.isConnectedWifi(getActivity())) {
                BackgroundAutoDownload.autoDownloadMedia(getActivity(), realmChatModel);
            }
        }
    }

    @Override
    public void onItemClick(int position, RealmChatModel realmChatModel) {
        downloadAttachment(true, position, realmChatModel);
    }

    public void downloadAttachment(boolean AfterDownloadShow, int position, RealmChatModel realmChatModel) {
        if (!realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_TEXT) && realmChatModel.isLocalMessage() && !(realmChatModel.getLocalMediaPath() != null && !realmChatModel.getLocalMediaPath().isEmpty() && new File(realmChatModel.getLocalMediaPath()).exists())) {
            showAlertDialog(activity.getResources().getString(R.string.media_not_exists));
        } else {
            if (!realmChatModel.isUploading() && !realmChatModel.isDownloading()) {
                if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_IMAGE)) {
                    if (realmChatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, realmChatModel.getLocalMediaPath()).exists()) {
                        if (AfterDownloadShow)
                            showFullScreenImage(activity, mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, realmChatModel.getLocalMediaPath()).toString());
                    } else {
                        realmHandler.getRealm().beginTransaction();
                        realmChatModel.setDownloading(true);
                        realmHandler.getRealm().commitTransaction();
                        chatAdapter.notifyItemChanged(position);

                        String fileName = realmChatModel.getFileName();
                        if (realmChatModel.getLocalMediaPath() != null && !realmChatModel.getLocalMediaPath().isEmpty()) {
                            fileName = realmChatModel.getLocalMediaPath();
                        }
                        mediaUtil.downloadImage(realmChatModel.getMessageUniqueId(), fileName, Constants.SOCKET_CHAT_IMAGE_URL + realmChatModel.getFilelink(), new MediaUtil.DownloadVideoCallback() {
                            @Override
                            public void progress(int progress) {

                            }

                            @Override
                            public void response(boolean isSuccess, String taskId, String path) {
                                if (realmHandler != null && !realmHandler.getRealm().isClosed()) {
                                    RealmChatModel realmChatModel = realmHandler.getMessageInRealm(taskId);
                                    realmHandler.getRealm().beginTransaction();
                                    realmChatModel.setDownloading(false);
                                    if (isSuccess) {
                                        mediaUtil.scanFileForGallery(activity, new File(path));
                                        realmChatModel.setLocalMediaPath(FilenameUtils.getName(path));
                                    }
                                    realmHandler.getRealm().commitTransaction();
                                    chatAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } else if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_VIDEO)) {
                    if (realmChatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_VIDEO_PATH, realmChatModel.getLocalMediaPath()).exists()) {
                        if (AfterDownloadShow)
                            mediaUtil.openFile(mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_VIDEO_PATH, realmChatModel.getLocalMediaPath()).toString());
                    } else {
                        realmHandler.getRealm().beginTransaction();
                        realmChatModel.setDownloading(true);
                        realmHandler.getRealm().commitTransaction();
                        chatAdapter.notifyItemChanged(position);

                        String fileName = realmChatModel.getFileName();
                        if (realmChatModel.getLocalMediaPath() != null && !realmChatModel.getLocalMediaPath().isEmpty()) {
                            fileName = realmChatModel.getLocalMediaPath();
                        }
                        mediaUtil.downloadVideo(realmChatModel.getMessageUniqueId(), fileName, Constants.SOCKET_CHAT_VIDEO_URL + realmChatModel.getFilelink(), new MediaUtil.DownloadVideoCallback() {
                            @Override
                            public void progress(int progress) {

                            }

                            @Override
                            public void response(boolean isSuccess, String taskId, String path) {
                                if (realmHandler != null && !realmHandler.getRealm().isClosed()) {
                                    RealmChatModel realmChatModel = realmHandler.getMessageInRealm(taskId);
                                    realmHandler.getRealm().beginTransaction();
                                    realmChatModel.setDownloading(false);
                                    if (isSuccess) {
                                        mediaUtil.scanFileForGallery(activity, new File(path));
                                        realmChatModel.setLocalMediaPath(FilenameUtils.getName(path));
                                    }
                                    realmHandler.getRealm().commitTransaction();
                                    chatAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } else if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_DOCS)) {
                    if (realmChatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_DOCS_PATH, realmChatModel.getLocalMediaPath()).exists()) {
                        if (AfterDownloadShow)
                            mediaUtil.openFile(mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_DOCS_PATH, realmChatModel.getLocalMediaPath()).getAbsolutePath());
                    } else {
                        realmHandler.getRealm().beginTransaction();
                        realmChatModel.setDownloading(true);
                        realmHandler.getRealm().commitTransaction();
                        chatAdapter.notifyItemChanged(position);

                        String fileName = realmChatModel.getFileName();
                        if (realmChatModel.getLocalMediaPath() != null && !realmChatModel.getLocalMediaPath().isEmpty()) {
                            fileName = realmChatModel.getLocalMediaPath();
                        }
                        mediaUtil.downloadDocument(realmChatModel.getMessageUniqueId(), fileName, Constants.SOCKET_CHAT_DOCUMENT_URL + realmChatModel.getFilelink(), new MediaUtil.DownloadVideoCallback() {
                            @Override
                            public void progress(int progress) {

                            }

                            @Override
                            public void response(boolean isSuccess, String taskId, String path) {
                                if (realmHandler != null && !realmHandler.getRealm().isClosed()) {
                                    RealmChatModel realmChatModel = realmHandler.getMessageInRealm(taskId);
                                    realmHandler.getRealm().beginTransaction();
                                    realmChatModel.setDownloading(false);
                                    if (isSuccess) {
                                        mediaUtil.scanFileForGallery(activity, new File(path));
                                        realmChatModel.setLocalMediaPath(FilenameUtils.getName(path));
                                    }
                                    realmHandler.getRealm().commitTransaction();
                                    chatAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public static void showFullScreenImage(Context context, String path) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_fullscreen);
        ImageView fullImageView = (ImageView) dialog.findViewById(R.id.fullImageView);
        final View progress_bar = dialog.findViewById(R.id.progress_bar);
        Glide.with(context)
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progress_bar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progress_bar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .fitCenter().into(fullImageView);
        dialog.show();
    }

    @Override
    public void onItemLongClick(int position, RealmChatModel realmChatModel) {
        if (realmChatModel.isError() && realmChatModel.getUserId().equals(getUserId())) {
            showDeleteDialog(realmChatModel);
        }
    }

    @Override
    public void onDeleteClick(int position, RealmChatModel realmChatModel) {
        showDeleteDialog(realmChatModel);
    }

    @Override
    public void onReplyMessageClicked(String messageId) {
        int pos = getMessageAdapterPosition(messageId);
        if (pos != 0)
            chatRecyclerView.scrollToPosition(pos);
        downloadAttachment(true, pos, chatAdapter.getItem(pos));
    }

    private int getMessageAdapterPosition(String messageId) {
        if (chatAdapter != null) {
            for (int i = 0; i < chatAdapter.getData().size(); i++) {
                if (chatAdapter.getData().get(i).getMessageUniqueId().equals(messageId)) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public void onEditClick(int position, RealmChatModel realmChatModel) {
        if (realmChatModel.getMessage() != null && !realmChatModel.getMessage().isEmpty()) {
            isMessageEditing = true;
            edittingRealmChatModel = realmChatModel;
            et_message.setText(realmChatModel.getMessage());
            et_message.setSelection(et_message.getText().length());
            UtilMethods.showKeyBoard(activity);
            et_message.requestFocus();
            showEditView(realmChatModel);
        }
    }

    @Override
    public void onReplyClick(int position, RealmChatModel realmChatModel) {
        showReplyMessageView(realmChatModel);
    }

    @Override
    public void onForwardClick(int position, RealmChatModel realmChatModel) {
        if (UtilMethods.isInternetAvailable(activity, true)) {
            ForwardUserListFragment forwardUserListFragment = new ForwardUserListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("message_id", realmChatModel.getMessageUniqueId());
            forwardUserListFragment.setArguments(bundle);
            UtilMethods.setFragment(R.id.main_container, getActivity().getSupportFragmentManager(), forwardUserListFragment, "ForwardFragment", true, true);
        }
    }

    private void isValidText() {
        if (et_message.getText().toString().trim().length() == 0)
            return;
    }

    private JsonObject getChannelObject() {
        JsonObject json = new JsonObject();
        try {
            json.addProperty("user_id", ConversionUtils.encrypt_for_body(getUserId()));
            json.addProperty("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getUserName(String userId) {
        if (selectedTeam != null && selectedTeam.isSync()) {
            for (int i = 0; i < selectedTeam.getMembersRealmList().size(); i++) {
                if (selectedTeam.getMembersRealmList().get(i).getUser_id().equals(userId)) {
                    return selectedTeam.getMembersRealmList().get(i).getUser_name();
                }
            }
        }
        return "";
    }

    public String getProfilePicture(String userId) {
        if (selectedTeam != null && selectedTeam.isSync()) {
            for (int i = 0; i < selectedTeam.getMembersRealmList().size(); i++) {
                if (selectedTeam.getMembersRealmList().get(i).getUser_id().equals(userId)) {
                    return selectedTeam.getMembersRealmList().get(i).getProfile_pic();
                }
            }
        }
        return "";
    }

    private void showEditView(RealmChatModel realmChatModel) {
        replyViewBackground.setBackground(AppThemeResource.getAppThemeResource(activity).getChatReplyDrawableColor());
        ll_replyVew.setVisibility(View.VISIBLE);
        editView.setVisibility(View.VISIBLE);
        replyView.setVisibility(View.GONE);
        edit_videoSymbols.setVisibility(View.GONE);
        edit_document.setVisibility(View.GONE);

        showImage(edit_userImage, getProfilePicture(realmChatModel.getUserId()));
        if (getUserName(realmChatModel.getUserId()).isEmpty()) {
            edit_tv_name.setText(UtilMethods.getDecodedMessage(realmChatModel.getUserName()));
        } else {
            edit_tv_name.setText(UtilMethods.getDecodedMessage(getUserName(realmChatModel.getUserId())));
        }
        if (realmChatModel.getIsEdited().equals("1")) {
            edit_date.setText(getActivity().getResources().getString(R.string.message_edit_alert) + " " + ChatDateFormat.getRequiredFormat(realmChatModel.getUpdatedTimeStamp(), "dd MMM HH:mm") + " - " + ChatDateFormat.getRequiredFormat(realmChatModel.getTimeStamp(), "HH:mm"));
        } else {
            edit_date.setText(ChatDateFormat.getRequiredFormat(realmChatModel.getTimeStamp(), "HH:mm"));
        }

        if (realmChatModel.getParentId().equals("0")) {
            edit_replyView.setVisibility(View.GONE);
        } else {
            edit_replyView.setVisibility(View.VISIBLE);
            edit_tv_document_name.setVisibility(View.GONE);
            edit_replyVideoSymbols.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(realmChatModel.getReplyList());
                if (getUserName(String.valueOf(jsonObject.get("user_id"))).isEmpty()) {
                    edit_reply_name.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("user_name"))));
                } else {
                    edit_reply_name.setText(UtilMethods.getDecodedMessage(getUserName(String.valueOf(jsonObject.get("user_id")))));
                }
                edit_reply_message.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("message"))));
                if (edit_reply_message.getText().toString().trim().length() == 0) {
                    edit_reply_message.setVisibility(View.GONE);
                } else {
                    edit_reply_message.setVisibility(View.VISIBLE);
                }
                if (ChatConstants.MESSAGE_TEXT.equals(String.valueOf(jsonObject.get("message_type")))) {
                    edit_replyCardView.setVisibility(View.GONE);
                } else if (ChatConstants.MESSAGE_IMAGE.equals(String.valueOf(jsonObject.get("message_type")))) {
                    edit_replyCardView.setVisibility(View.VISIBLE);
                    showImageUsingGlide(edit_replyImageView, Constants.SOCKET_CHAT_IMAGE_URL + String.valueOf(jsonObject.get("filelink")));
                } else if (ChatConstants.MESSAGE_VIDEO.equals(String.valueOf(jsonObject.get("message_type")))) {
                    edit_replyCardView.setVisibility(View.VISIBLE);
                    edit_replyVideoSymbols.setVisibility(View.VISIBLE);
                    showImageUsingGlide(edit_replyImageView, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + String.valueOf(jsonObject.get("filelink")));
                } else if (ChatConstants.MESSAGE_DOCS.equals(String.valueOf(jsonObject.get("message_type")))) {
                    edit_replyCardView.setVisibility(View.GONE);
                    edit_tv_document_name.setVisibility(View.VISIBLE);
                    edit_tv_document_name.setText(String.valueOf(jsonObject.get("filelink")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ChatConstants.MESSAGE_TEXT.equals(realmChatModel.getMessageType())) {
            edit_mediaImageCardView.setVisibility(View.GONE);
        } else if (ChatConstants.MESSAGE_DOCS.equals(realmChatModel.getMessageType())) {
            edit_mediaImageCardView.setVisibility(View.GONE);
            edit_document.setVisibility(View.VISIBLE);
            edit_document.setText(realmChatModel.getFilelink());
        } else if (ChatConstants.MESSAGE_IMAGE.equals(realmChatModel.getMessageType())) {
            edit_mediaImageCardView.setVisibility(View.VISIBLE);
            showImageUsingGlide(edit_iv_mediaVew, Constants.SOCKET_CHAT_IMAGE_URL + realmChatModel.getFilelink());
        } else if (ChatConstants.MESSAGE_VIDEO.equals(realmChatModel.getMessageType())) {
            showImageUsingGlide(edit_iv_mediaVew, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + realmChatModel.getThumbnail());
            edit_mediaImageCardView.setVisibility(View.VISIBLE);
            edit_videoSymbols.setVisibility(View.VISIBLE);
        }
    }

    private void showReplyMessageView(RealmChatModel realmChatModel) {
        replyViewBackground.setBackground(AppThemeResource.getAppThemeResource(activity).getChatReplyDrawableColor());
        ll_replyVew.setVisibility(View.VISIBLE);
        replyView.setVisibility(View.VISIBLE);
        editView.setVisibility(View.GONE);
        mediaImageCardView.setVisibility(View.GONE);
        videoSymbols.setVisibility(View.GONE);
        tv_document_name.setVisibility(View.GONE);

        showImage(reply_userImage, getProfilePicture(realmChatModel.getUserId()));
        reply_date.setText(ChatDateFormat.getRequiredFormat(realmChatModel.getTimeStamp(), "HH:mm"));

        if (getUserName(realmChatModel.getUserId()).isEmpty()) {
            reply_tv_name.setText(UtilMethods.getDecodedMessage(realmChatModel.getUserName()));
        } else {
            reply_tv_name.setText(UtilMethods.getDecodedMessage(getUserName(realmChatModel.getUserId())));
        }
        if (realmChatModel.getMessage() != null && !realmChatModel.getMessage().isEmpty()) {
            tv_reply_message.setVisibility(View.VISIBLE);
            tv_reply_message.setText(UtilMethods.getDecodedMessage(realmChatModel.getMessage()));
        } else {
            tv_reply_message.setVisibility(View.GONE);
        }
        if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_IMAGE)) {
            mediaImageCardView.setVisibility(View.VISIBLE);
            showImageUsingGlide(iv_mediaVew, ((mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, realmChatModel.getFilelink()).exists()) ? mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, realmChatModel.getFilelink()).getAbsolutePath() : Constants.SOCKET_CHAT_IMAGE_URL + realmChatModel.getFilelink()));
        } else if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_VIDEO)) {
            videoSymbols.setVisibility(View.VISIBLE);
            mediaImageCardView.setVisibility(View.VISIBLE);
            showImageUsingGlide(iv_mediaVew, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + realmChatModel.getThumbnail());
        } else if (realmChatModel.getMessageType().equals(ChatConstants.MESSAGE_DOCS)) {
            tv_document_name.setVisibility(View.VISIBLE);
            tv_document_name.setText(realmChatModel.getFilelink());
        }
        UtilMethods.showKeyBoard(activity);
        et_message.requestFocus();
        edittingRealmChatModel = realmChatModel;
    }

    private void hideReplyMessageView() {
        ll_replyVew.setVisibility(View.GONE);
        replyViewBackground.setBackground(null);
        UtilMethods.hideKeyBoard(activity);
    }

    private void showImageUsingGlide(ImageView imageView, String path) {
        Glide.with(activity)
                .load(path)
                .centerCrop()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(imageView);
    }

    public void showDeleteDialog(final RealmChatModel realmChatModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.delete_confirm))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (realmChatModel.isLocalMessage()) {
                            realmHandler.deleteMessageInRealm(realmChatModel.getMessageUniqueId());
                            chatAdapter.notifyDataSetChanged();
                        } else {
                            if (chatHandler.isSocketConnected()) {
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("updated_at", ConversionUtils.encrypt_for_body(ChatDateFormat.generateTimestampInUTC()));
                                    json.put("user_id", ConversionUtils.encrypt_for_body(realmChatModel.getUserId()));
                                    json.put("channel_name", ConversionUtils.encrypt_for_body(realmChatModel.getChannelName()));
                                    json.put("message_id", ConversionUtils.encrypt_for_body(realmChatModel.getMessageUniqueId()));
                                    edittingRealmChatModel = realmChatModel;
                                    chatHandler.deleteMessage(json);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                realmHandler.getRealm().beginTransaction();
                                realmChatModel.setMessageStatus(ChatConstants.MESSAGE_DELETE_STATUS);
                                realmChatModel.setDeleted(true);
                                realmChatModel.setUpdatedTimeStamp(ChatDateFormat.generateTimestampInUTC());
                                realmHandler.getRealm().commitTransaction();
                                chatAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
        alert.show();
        TextView messageText = (TextView) alert.findViewById(android.R.id.message);
        if (messageText != null)
            messageText.setGravity(Gravity.CENTER | Gravity.LEFT);

        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.black));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.black));
    }

    private RealmChatModel sendMediaMessage(String uuniqueTimeStamp, String messageType, String filename, File flle, String thumbnail) {
        RealmChatModel realmChatModel = new RealmChatModel();
        realmChatModel.setUserId(getUserId());
        realmChatModel.setLocalMessage(true);
        realmChatModel.setMessage(getTypingMessage());
        realmChatModel.setMessageUniqueId(uuniqueTimeStamp);
        realmChatModel.setChatType(getChatType());
        realmChatModel.setChannelName(getChannelName());
        realmChatModel.setParentId(getParentId());
        realmChatModel.setMessageType(messageType);
        realmChatModel.setMessageStatus("1");
        realmChatModel.setIsEdited(getIsEdited());
        realmChatModel.setUpdatedTimeStamp(ChatDateFormat.generateTimestampInUTC(uuniqueTimeStamp));
        realmChatModel.setTimeStamp(ChatDateFormat.generateTimestampInUTC(uuniqueTimeStamp));
        realmChatModel.setUploading(true);
        realmChatModel.setFileName(filename);
        realmChatModel.setLocalMediaPath(FilenameUtils.getName(String.valueOf(flle)));
        realmChatModel.setLocalMediaThumbnail(FilenameUtils.getName(thumbnail));
        if (chatHandler.isSocketConnected()) {
            realmChatModel.setOnline(true);
        } else {
            realmChatModel.setOnline(false);
        }
        if (realmChatModel.getParentId() != null && !realmChatModel.getParentId().equals("0")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message_id", edittingRealmChatModel.getMessageUniqueId());
                jsonObject.put("user_id", edittingRealmChatModel.getUserId());
                jsonObject.put("user_name", edittingRealmChatModel.getUserName());
                jsonObject.put("message", edittingRealmChatModel.getMessage());
                jsonObject.put("message_type", edittingRealmChatModel.getMessageType());
                jsonObject.put("filelink", edittingRealmChatModel.getFilelink());
                jsonObject.put("thumbnail", edittingRealmChatModel.getThumbnail());
                jsonObject.put("filename", edittingRealmChatModel.getFilelink());
                realmChatModel.setReplyList(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        realmHandler.insertMessageInRealm(realmChatModel);
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.scrollToPosition(0);
        return realmChatModel;
    }

    private void handleAttachmentClick(final boolean isImageClick) {
        if (isImageClick) {
            mediaUtil.openMultipleImageGallery(new MediaUtil.SelectedImageCallback() {
                @Override
                public void response(boolean isCamera, boolean isSingleImage, Object imageResult) {
                    mediaUtil.openImageEditIntent(MediaUtil.CUSTOM_CROP, (ArrayList<Image>) imageResult, new MediaUtil.SelectedImageCallback() {
                        @Override
                        public void response(boolean isCamera, boolean isSingleImage, Object imageResult) {
                            if (imageResult != null) {
                                mediaUtil.compressMultipleImages((ArrayList<Image>) imageResult, 100, new MediaUtil.CompressImageCallback() {
                                    @Override
                                    public void response(Bitmap bitmap, final String fileName, final String path) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                File file = new File(path);
                                                mediaUtil.scanFileForGallery(activity, file);
                                                String unique = String.valueOf(System.currentTimeMillis());
                                                RealmChatModel realmChatModel = sendMediaMessage(unique, ChatConstants.MESSAGE_IMAGE, fileName, file, null);
                                                mediaFileUpload(unique, realmChatModel, file);
                                                hideReplyMessageView();
                                                et_message.setText("");
                                            }
                                        });
                                    }
                                });
                            } else {
                                handleAttachmentClick(isImageClick);
                            }
                        }
                    });
                }
            });
        } else {
            mediaUtil.openSingleVideoGallery(new MediaUtil.SelectedVideoCallback() {
                @Override
                public void response(Bitmap bitmap, final String path, String thumbnail) {
                    if (path != null) {

                        final String unique = String.valueOf(System.currentTimeMillis());
                        final String fileName = FilenameUtils.getName(path);

                        mediaUtil.compressVideo(true, false, path, new MediaUtil.CompressVideoCallback() {

                            @Override
                            public void startCompressing(String compressFilePath, String thumbnail) {
                                if (thumbnail != null) {
                                    sendMediaMessage(unique, ChatConstants.MESSAGE_VIDEO, fileName, new File(compressFilePath), thumbnail);
                                    hideReplyMessageView();
                                    videoTextMessageHolder = String.valueOf(et_message.getText());
                                    et_message.setText("");
                                }
                            }

                            @Override
                            public void response(Bitmap bitmap, String path, String thumbnail) {
                                if (path != null) {
                                    File file = new File(path);
                                    mediaUtil.scanFileForGallery(activity, file);
                                    et_message.setText(videoTextMessageHolder);
                                    RealmChatModel realmChatModel = realmHandler.getMessageInRealm(unique);
                                    mediaFileUpload(unique, realmChatModel, file);
                                    et_message.setText("");
                                }
                            }

                            @Override
                            public void withoutCompressresponse(Bitmap bitmap, String path, String thumbnailPath) {
                                if (path != null) {
                                    File file = new File(path);
                                    mediaUtil.scanFileForGallery(activity, file);
                                    final String unique = String.valueOf(System.currentTimeMillis());
                                    RealmChatModel realmChatModel = sendMediaMessage(unique, ChatConstants.MESSAGE_VIDEO, fileName, file, thumbnailPath);
                                    mediaFileUpload(unique, realmChatModel, file);
                                    hideReplyMessageView();
                                    et_message.setText("");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void handleAttachmentCameraClick(boolean isImageClick) {
        if (isImageClick) {
            mediaUtil.openCameraForImage(true, new MediaUtil.SelectedImageCallback() {
                @Override
                public void response(boolean isCamera, boolean isSingleImage, Object imageResult) {
                    if (imageResult != null) {
                        ArrayList<Image> imageArrayList = new ArrayList<Image>();
                        Image image = new Image(System.currentTimeMillis(), "Camera" + System.currentTimeMillis(), mediaUtil.getRealPathFromURI((Uri) imageResult));
                        imageArrayList.add(image);
                        mediaUtil.openImageEditIntent(MediaUtil.CUSTOM_CROP, imageArrayList, new MediaUtil.SelectedImageCallback() {
                            @Override
                            public void response(boolean isCamera, boolean isSingleImage, Object imageResult) {
                                mediaUtil.compressMultipleImages((ArrayList<Image>) imageResult, 80, new MediaUtil.CompressImageCallback() {
                                    @Override
                                    public void response(Bitmap bitmap, final String fileName, final String path) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                File file = new File(path);
                                                mediaUtil.scanFileForGallery(activity, file);
                                                String unique = String.valueOf(System.currentTimeMillis());
                                                RealmChatModel realmChatModel = sendMediaMessage(unique, ChatConstants.MESSAGE_IMAGE, fileName, file, null);
                                                mediaFileUpload(unique, realmChatModel, file);
                                                hideReplyMessageView();
                                                et_message.setText("");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else {
            mediaUtil.openCameraForVideo(new MediaUtil.SelectedVideoCallback() {
                @Override
                public void response(Bitmap bitmap, String path, String thumbnail) {
                    if (path != null) {
                        final String unique = String.valueOf(System.currentTimeMillis());
                        mediaUtil.compressVideo(true, true, path, new MediaUtil.CompressVideoCallback() {
                            @Override
                            public void startCompressing(String compressFilePath, String thumbnail) {
                                if (thumbnail != null) {
                                    sendMediaMessage(unique, ChatConstants.MESSAGE_VIDEO, FilenameUtils.getName(compressFilePath), new File(compressFilePath), thumbnail);
                                    hideReplyMessageView();
                                    videoTextMessageHolder = String.valueOf(et_message.getText());
                                    et_message.setText("");
                                }
                            }

                            @Override
                            public void response(Bitmap bitmap, String path, String thumbnail) {
                                if (path != null) {
                                    File file = new File(path);
                                    mediaUtil.scanFileForGallery(activity, file);
                                    et_message.setText(videoTextMessageHolder);
                                    RealmChatModel realmChatModel = realmHandler.getMessageInRealm(unique);
                                    mediaFileUpload(unique, realmChatModel, file);
                                    et_message.setText("");
                                }
                            }

                            @Override
                            public void withoutCompressresponse(Bitmap bitmap, String path, String thumbnailPath) {
                                if (path != null) {
                                    File file = new File(path);
                                    mediaUtil.scanFileForGallery(activity, file);
                                    final String unique = String.valueOf(System.currentTimeMillis());
                                    RealmChatModel realmChatModel = sendMediaMessage(unique, ChatConstants.MESSAGE_VIDEO, FilenameUtils.getName(path), file, thumbnailPath);
                                    mediaFileUpload(unique, realmChatModel, file);
                                    hideReplyMessageView();
                                    et_message.setText("");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void mediaFileUpload(String taskid, RealmChatModel realmChatModel, File file) {
        Log.e("media time", ":" + ChatDateFormat.generateTimestampInUTC(taskid));
        new UploadService(taskid, realmChatModel, file, new UploadListener() {
            @Override
            public void upload(boolean status, String taskId, JSONObject jsonObject) {
                try {
                    if (status) {
                        handleMediaUploadedSuccessfully(taskId, jsonObject);
                    } else {
                        handleMediaNotUploadedSuccessfully(taskId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleMediaNotUploadedSuccessfully(String uniqueTimeStamp) {
        RealmChatModel realmChatModel = realmHandler.getMessageInRealm(uniqueTimeStamp);
        realmHandler.getRealm().beginTransaction();
        realmChatModel.setError(true);
        realmChatModel.setUploading(false);
        realmHandler.getRealm().commitTransaction();
        chatAdapter.notifyDataSetChanged();
    }

    private void handleMediaUploadedSuccessfully(String taskid, JSONObject jsonObject) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
        json.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
        json.put("parent_id", ConversionUtils.encrypt_for_body(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("parent_id")))));
        json.put("chat_type", ConversionUtils.encrypt_for_body(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("chat_type")))));
        json.put("message_type", ConversionUtils.encrypt_for_body(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("message_type")))));
        json.put("message_id", ConversionUtils.encrypt_for_body(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("message_id")))));
        json.put("created_at", ConversionUtils.encrypt_for_body(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("created_at")))));
        json.put("updated_at", ConversionUtils.encrypt_for_body(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("updated_at")))));

        RealmChatModel realmChatModel = realmHandler.getMessageInRealm(taskid);
        RealmChatModel newRealmChatModel = realmHandler.getRealm().copyFromRealm(realmChatModel);
        newRealmChatModel.setMessageUniqueId(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("message_id"))));
        newRealmChatModel.setThumbnail(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("thumbnail"))));
        newRealmChatModel.setFilelink(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("filelink"))));
        newRealmChatModel.setTimeStamp(ConversionUtils.decrypt_text(String.valueOf(jsonObject.get("created_at"))));
        newRealmChatModel.setLocalMessage(false);
        newRealmChatModel.setUpdatedTimeStamp(newRealmChatModel.getTimeStamp());
        realmHandler.insertMessageInRealm(newRealmChatModel);
        realmHandler.deleteMessageInRealm(taskid);
        chatHandler.sendMessage(json);
    }

    class UploadService {
        String taskId;
        UploadListener uploadListener;

        public UploadService(String taskId, RealmChatModel realmChatModel, File file, UploadListener uploadListener) {
            this.taskId = taskId;
            this.uploadListener = uploadListener;
            mediaFileUpload(realmChatModel, file);
        }

        private void mediaFileUpload(RealmChatModel realmChatModel, File file) {
            Log.e("Uploading", "File");
            final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part mediaFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            Call<ResponseBody> call = UploadMediaRetrofit.createService(Apis.class).uploadChatMedia(Constants.AUTH_KEY, getToken(), ConversionUtils.encrypt_for_body(realmChatModel.getUserId()), ConversionUtils.encrypt_for_body(realmChatModel.getFileName()), null, ConversionUtils.encrypt_for_EmptyText(realmChatModel.getMessage()), ConversionUtils.encrypt_for_body(realmChatModel.getUserId()), ConversionUtils.encrypt_for_body(realmChatModel.getChannelName()), ConversionUtils.encrypt_for_body(realmChatModel.getParentId()), ConversionUtils.encrypt_for_body(realmChatModel.getChatType()), ConversionUtils.encrypt_for_body(realmChatModel.getMessageType()), ConversionUtils.encrypt_for_body(realmChatModel.getTimeStamp()), mediaFile);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null && response.body() != null) {
                            if (response.code() == Constants.SUCCESS) {
                                JSONObject jsonResponse = new JSONObject(response.body().string());
                                if (jsonResponse.has("data")) {
                                    JSONObject jsonObject = (JSONObject) jsonResponse.get("data");
                                    uploadListener.upload(true, taskId, jsonObject);
                                } else {
                                    uploadListener.upload(false, taskId, null);
                                }
                            } else {
                                uploadListener.upload(false, taskId, null);
                            }
                        } else {
                            uploadListener.upload(false, taskId, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        uploadListener.upload(false, taskId, null);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("onFailure", "" + t);
                    uploadListener.upload(false, taskId, null);
                }
            });
        }
    }

    interface UploadListener {
        void upload(boolean status, String taskid, JSONObject jsonObject);

    }

    private void joinChannel() {
        HashMap<String, String> map = new HashMap<>();
        map.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
        map.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
        Call<ResponseBody> call = UploadMediaRetrofit.createService(Apis.class).joinChannel(Constants.AUTH_KEY, getToken(), ConversionUtils.encrypt_for_body(getUserId()), map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("onResponse", "" + response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "" + t);
            }
        });
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
        alert.show();
        TextView messageText = (TextView) alert.findViewById(android.R.id.message);
        if (messageText != null)
            messageText.setGravity(Gravity.CENTER | Gravity.LEFT);

        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.black));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.black));
    }

    private final BroadcastReceiver NetworkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (!UtilMethods.isInternetAvailable(context, false)) {
                    if (chatAdapter != null) {
                        realmHandler.getRealm().beginTransaction();
                        for (RealmChatModel realmChatModel : chatAdapter.getData()) {
                            realmChatModel.setOnline(false);
                        }
                        realmHandler.getRealm().commitTransaction();
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void printMinutes(String start_date, String end_date) {

        showBaseProgressDialog(getResourceString(R.string.please_wait));

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", ConversionUtils.encrypt_for_body(getUserId()));
        map.put("channel_name", ConversionUtils.encrypt_for_body(getChannelName()));
        if (start_date != null && !start_date.isEmpty())
            map.put("start_date", ConversionUtils.encrypt_for_body(start_date));
        if (end_date != null && !end_date.isEmpty())
            map.put("end_date", ConversionUtils.encrypt_for_body(end_date));
        map.put("print_option", ConversionUtils.encrypt_for_body("0"));
        map.put("timezone", ConversionUtils.encrypt_for_body(TimeZone.getDefault().getID()));
        Call<BasicResponse> call = UploadMediaRetrofit.createService(Apis.class).printMinutes(Constants.AUTH_KEY, getToken(), ConversionUtils.encrypt_for_headers(getUserId()), map);
        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                try {
                    hideBaseProgressDialog();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            UtilMethods.showAlertPopup(getActivity(), getResourceString(R.string.print_success_alert), null);
                        }
                    } else {
                        handleErrorResponseForMyCard(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable c) {
                hideBaseProgressDialog();
                showFailureToast(getActivity(), c);
            }
        });
    }

    private void showDateSelectionDialog() {
        final Dialog createChannelDialog = new Dialog(getActivity());
        createChannelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        createChannelDialog.setCancelable(true);
        createChannelDialog.setContentView(R.layout.dialog_select_date_for_chat);
        createChannelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText etStartDate = createChannelDialog.findViewById(R.id.et_start_date);
        final EditText etEndDate = createChannelDialog.findViewById(R.id.et_end_date);
        TextView tvSkip = createChannelDialog.findViewById(R.id.tv_skip);
        TextView tvOk = createChannelDialog.findViewById(R.id.tv_ok);
        tvSkip.setTextColor(AppThemeResource.getAppThemeResource(getActivity()).getNormalButtonBackgroundColor());
        tvOk.setTextColor(AppThemeResource.getAppThemeResource(getActivity()).getNormalButtonBackgroundColor());
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChannelDialog.dismiss();
                printMinutes(null, null);
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!etStartDate.getText().toString().trim().isEmpty() && !etEndDate.getText().toString().trim().isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        if (sdf.parse(etStartDate.getText().toString()).before(sdf.parse(etEndDate.getText().toString()))) {
                            createChannelDialog.dismiss();
                            printMinutes(etStartDate.getText().toString(), etEndDate.getText().toString());
                        } else {
                            if (sdf.parse(etStartDate.getText().toString()).equals(sdf.parse(etEndDate.getText().toString()))) {
                                createChannelDialog.dismiss();
                                printMinutes(etStartDate.getText().toString(), etEndDate.getText().toString());
                            } else {
                                Toast.makeText(getActivity(), getResourceString(R.string.date_select_alert), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), getResourceString(R.string.date_select_alert), Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException p) {
                    p.printStackTrace();
                }
            }
        });

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(0);
                        cal.set(year, month, day);
                        Date date = cal.getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = sdf.format(date);
                        etStartDate.setText(strDate);
                    }
                }, mYear, mMonth, mDay);
                pickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                pickerDialog.show();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(0);
                        cal.set(year, month, day);
                        Date date = cal.getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = sdf.format(date);
                        etEndDate.setText(strDate);
                    }
                }, mYear, mMonth, mDay);
                pickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                pickerDialog.show();
            }
        });
        createChannelDialog.show();
    }

    @Override
    public void INRMenuClick(View view) {
        showScreenMenu(view);
    }

    @Override
    public void INRMenuOptionClick(View view, String clickMenu) {
        if (clickMenu.equals(getResourceString(R.string.print_minutes))) {
            if (selectedTeam != null && selectedTeam.isSync() && chatAdapter != null && chatAdapter.getItemCount() > 0) {
                showDateSelectionDialog();
            }
        }
    }

    private void showScreenMenu(View view) {
        ArrayList<MenuModel> menuModelArrayList = new ArrayList<>();
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.options_chat));
        for (int i = 0; i < list.size(); i++) {
            MenuModel menuModel = new MenuModel();
            menuModel.setName(list.get(i));
            menuModel.setArrowShowing(true);
            menuModel.setColorType(Constants.SELECTED_COLOR_TYPE);
            if (list.get(i).equals(getResourceString(R.string.ttalk))) {
                menuModel.setHeader(true);
                menuModel.setArrowShowing(false);
            } else if (list.get(i).equals(getResourceString(R.string.print_minutes))) {
                if (!(selectedTeam != null && selectedTeam.isSync())) {
                    menuModel.setColorType(Constants.DISABLE_COLOR_TYPE);
                }
            }
            menuModelArrayList.add(menuModel);
        }
        ((MainActivity) getActivity()).showINRMenuDialog(view, menuModelArrayList);
    }
}

