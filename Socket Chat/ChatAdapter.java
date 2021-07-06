package com.bawa.inr.livechat;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bawa.inr.Constants;
import com.bawa.inr.R;
import com.bawa.inr.ui.whisperers.WhispererFragment;
import com.bawa.inr.util.AppThemeResource;
import com.bawa.inr.util.UtilMethods;
import com.bawa.inr.view.RoundedCornersTransformation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonTextView;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ChatAdapter extends RealmRecyclerViewAdapter<RealmChatModel, ChatAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener, SwipeRevealLayout.SwipeListener {
    private Context context;
    private String userId;
    private OnItemClickListener onItemClickListener;
    private int swipeItemPosition;
    private MediaUtil mediaUtil;
    private ViewBinderHelper binderHelper;

    private float width;

    public interface OnItemClickListener {
        void onItemClick(int position, RealmChatModel realmChatModel);

        void onDeleteClick(int position, RealmChatModel realmChatModel);

        void onEditClick(int position, RealmChatModel realmChatModel);

        void onReplyClick(int position, RealmChatModel realmChatModel);

        void onForwardClick(int position, RealmChatModel realmChatModel);

        void onItemLongClick(int position, RealmChatModel realmChatModel);

        void onReplyMessageClicked(String MessageId);
    }


    public ChatAdapter(RealmResults<RealmChatModel> realmChatModels, Context context, RealmHandler realmHandler, MediaUtil mediaUtil, OnItemClickListener onItemClickListener, String channelId, String userId) {
        super(realmChatModels, true);
        //super(realmHandler.getRealm().where(RealmChatModel.class).equalTo("channelName", channelId).findAllSortedAsync("timeStamp", Sort.DESCENDING), true);
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.userId = userId;
        this.mediaUtil = mediaUtil;
        binderHelper = new ViewBinderHelper();
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int index) {
        return super.getItemId(index);
    }

    @Override
    public void onClosed(SwipeRevealLayout swipeRevealLayout) {
        swipeRevealLayout.findViewById(R.id.left_color_dummy).setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        swipeRevealLayout.findViewById(R.id.right_color_dummy).setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
    }

    @Override
    public void onOpened(SwipeRevealLayout view) {
        swipeItemPosition = Integer.parseInt(view.getTag().toString());
    }

    @Override
    public void onSlide(SwipeRevealLayout swipeRevealLayout, float v) {
        if (v > 0.0) {
            swipeRevealLayout.findViewById(R.id.right_color_dummy).setBackgroundColor(ContextCompat.getColor(context, R.color.grey_default_color));
            if (onItemClickListener instanceof WhispererFragment) {
                swipeRevealLayout.findViewById(R.id.left_color_dummy).setBackgroundColor(AppThemeResource.getAppThemeResource(context).getNormalButtonBackgroundColor());
            } else {
                swipeRevealLayout.findViewById(R.id.left_color_dummy).setBackgroundColor(ContextCompat.getColor(context, R.color.grey_default_color));
            }
        }
    }

    public void hideDeleteOptionView() {
        if (binderHelper != null)
            binderHelper.closeLayout(String.valueOf(swipeItemPosition));
    }

    public String getMessgeDate(int p) {
        return ChatDateFormat.getRequiredFormat(getItem(p).getTimeStamp(), "EEEE, dd MMM");
    }

    public int getNumberOfLine(float textsize, int width, String text) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        int numLines = (int) Math.ceil((float) bounds.width() / width);
        return numLines;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        binderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));

        final RealmChatModel chatModel = getItem(position);

//        //hide all log messages in case of whisper if backend will not do this
//        if (onItemClickListener instanceof WhispererFragment) {
//            if (ChatConstants.MESSAGE_CREATE_GROUP.equals(chatModel.getMessageType()) || ChatConstants.MESSAGE_JOIN_GROUP.equals(chatModel.getMessageType()) || ChatConstants.MESSAGE_LEFT_GROUP.equals(chatModel.getMessageType())) {
//
//            } else {
//
//            }
//        }

        if (ChatConstants.MESSAGE_CREATE_GROUP.equals(chatModel.getMessageType())) {
            holder.swipeRevealLayout.setVisibility(View.GONE);
            holder.tv_right_log.setVisibility(View.GONE);
            holder.tv_left_log.setVisibility(View.VISIBLE);
            holder.tv_left_log.setText(UtilMethods.getDecodedMessage(chatModel.getUserName().toUpperCase()) + " created");
        } else if (ChatConstants.MESSAGE_JOIN_GROUP.equals(chatModel.getMessageType())) {
            holder.swipeRevealLayout.setVisibility(View.GONE);
            holder.tv_right_log.setVisibility(View.GONE);
            holder.tv_left_log.setVisibility(View.VISIBLE);
            holder.tv_left_log.setText(UtilMethods.getDecodedMessage(chatModel.getUserName().toUpperCase()) + " joined");
        } else if (ChatConstants.MESSAGE_LEFT_GROUP.equals(chatModel.getMessageType())) {
            holder.swipeRevealLayout.setVisibility(View.GONE);
            holder.tv_left_log.setVisibility(View.GONE);
            holder.tv_right_log.setVisibility(View.VISIBLE);
            holder.tv_right_log.setText(UtilMethods.getDecodedMessage(chatModel.getUserName().toUpperCase()) + " left");
        } else {
            holder.tv_right_log.setVisibility(View.GONE);
            holder.tv_left_log.setVisibility(View.GONE);
            holder.swipeRevealLayout.setVisibility(View.VISIBLE);
        }

        if (holder.swipeRevealLayout.isOpened()) {
            binderHelper.closeLayout(String.valueOf(position));
        }
        holder.swipeRevealLayout.setTag(position);
        holder.swipeRevealLayout.setSwipeListener(this);

        if (onItemClickListener instanceof ChatFragment) {
            if ((!((ChatFragment) onItemClickListener).isMessageHistoryLoading) && (position > getItemCount() - 5) && (getItemCount() > 10)) {
                if (onItemClickListener != null) {
                    Log.e("hit message history", "" + position);
                    ((ChatFragment) onItemClickListener).isMessageHistoryLoading = true;
                    ((ChatFragment) onItemClickListener).getReadMessageHistory(chatModel.getMessageUniqueId());
                }
            }
        } else if (onItemClickListener instanceof WhispererFragment) {
            if ((!((WhispererFragment) onItemClickListener).isMessageHistoryLoading) && (position > getItemCount() - 5) && (getItemCount() > 10)) {
                if (onItemClickListener != null) {
                    Log.e("hit message history", "" + position);
                    ((WhispererFragment) onItemClickListener).isMessageHistoryLoading = true;
                    ((WhispererFragment) onItemClickListener).getReadMessageHistory(chatModel.getMessageUniqueId());
                }
            }
        }
        if (showheader(position)) {
            holder.tv_date.setVisibility(View.VISIBLE);
            holder.tv_date.setText(ChatDateFormat.getRequiredFormat(getItem(position).getTimeStamp(), "EEEE, dd MMM"));

            RelativeLayout.LayoutParams mainViewParams = (RelativeLayout.LayoutParams) holder.mainView.getLayoutParams();
            mainViewParams.setMargins(0, 0, 0, 0);
            holder.mainView.setLayoutParams(mainViewParams);
        } else {
            holder.tv_date.setVisibility(View.GONE);
            int top = 2, bottom = 0;
            if (!isLogMessage(position) && isNextLogMessage(position)) {
                top = 7;
            }
            if (!isLogMessage(position) && !isBottomShowHeader(position - 1) && isPreviousLogMessage(position)) {
                bottom = 7;
            }

            if (isLogMessage(position) && !isNextLogMessage(position)) {
                top = 0;
            }

            if (isLogMessage(position) && !isNextLogMessage(position) && !isPreviousLogMessage(position)) {
                top = 7;
            }

            RelativeLayout.LayoutParams mainViewParams = (RelativeLayout.LayoutParams) holder.mainView.getLayoutParams();
            mainViewParams.setMargins(0, (int) UtilMethods.getPixelFromDP(top, context), 0, (int) UtilMethods.getPixelFromDP(bottom, context));
            holder.mainView.setLayoutParams(mainViewParams);
        }

        if (chatModel.getUserId().equalsIgnoreCase(userId)) {
            holder.swipeRevealLayout.setDragEdge(SwipeRevealLayout.DRAG_EDGE_RIGHT);
            holder.optionsView.setBackground(ContextCompat.getDrawable(context, R.drawable.green_right_corners));
            UtilMethods.setBackgroundTintColor(holder.optionsView, AppThemeResource.getAppThemeResource(context).getNormalButtonBackgroundColor());
            holder.option1.setBackground(ContextCompat.getDrawable(context, R.drawable.grey_right_corners));
            holder.option1.setText(context.getResources().getString(R.string.delete));
            holder.option2.setText(context.getResources().getString(R.string.edit));
            holder.option1.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.option2.setTextColor(AppThemeResource.getAppThemeResource(context).getNormalButtonTextColor());
            holder.option2.setBackground(null);
            holder.option2.setVisibility(View.VISIBLE);
            holder.option1.setTag(position);
            holder.option1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(v.getTag().toString());
                    if (onItemClickListener != null) {
                        onItemClickListener.onDeleteClick(tag, getItem(tag));
                        hideDeleteOptionView();
                    }
                }
            });
            holder.option2.setTag(position);
            holder.option2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(v.getTag().toString());
                    if (onItemClickListener != null) {
                        onItemClickListener.onEditClick(tag, getItem(tag));
                        hideDeleteOptionView();
                    }
                }
            });

            //sender block
            holder.right_view.setVisibility(View.VISIBLE);
            holder.left_view.setVisibility(View.GONE);
            holder.right_bubble.setTag(position);
            holder.right_bubble.setOnClickListener(this);
            holder.tv_document_name.setTag(position);
            holder.tv_document_name.setOnClickListener(this);
            holder.right_bubble.setOnLongClickListener(this);

            holder.right_videoSymbols.setImageResource(0);
            holder.right_videoSymbols.setBackground(null);
            holder.tv_document_name.setVisibility(View.GONE);
            holder.right_editView.setVisibility(View.GONE);
            holder.right_document_progress.setVisibility(View.GONE);
            holder.tv_document_name.setText("");

            showImage(holder.right_userImage, AppThemeResource.getAppThemeResource(context).getLoginResponsePojo().getProfilePicture());
            holder.right_time.setText(ChatDateFormat.getRequiredFormat(getItem(position).getTimeStamp(), "HH:mm"));
            holder.right_userName.setText(UtilMethods.getDecodedMessage(AppThemeResource.getAppThemeResource(context).getLoginResponsePojo().getUserName()));

            if (chatModel.getMessage() != null && !chatModel.getMessage().isEmpty()) {
                holder.right_text_message.setText(UtilMethods.getDecodedMessage(chatModel.getMessage()));
                holder.right_text_message.setVisibility(View.VISIBLE);
            } else {
                holder.right_text_message.setVisibility(View.GONE);
            }
            //change drawable accroding to text message length
            if (chatModel.getMessageStatus().equals(ChatConstants.MESSAGE_DELETE_STATUS) || (chatModel.getMessageType().equals(ChatConstants.MESSAGE_TEXT) && chatModel.getParentId().equals("0") && !chatModel.getMessage().contains("\\n") && getNumberOfLine(holder.right_text_message.getTextSize(), (int) width, chatModel.getMessage()) < 2)) {
                holder.right_bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_white_box));
                UtilMethods.setBackgroundTintColor(holder.right_bubble, AppThemeResource.getAppThemeResource(context).getMyChatBubbleColor());
                holder.right_shadow.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_trannsprent_whitebox));
            } else {
                holder.right_bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.white_right));
                UtilMethods.setBackgroundTintColor(holder.right_bubble, AppThemeResource.getAppThemeResource(context).getMyChatBubbleColor());
                holder.right_shadow.setBackground(ContextCompat.getDrawable(context, R.drawable.shadow_right));
            }

            if (chatModel.isOnline()) {
                //online
                holder.right_offlineStatus.setVisibility(View.GONE);
                holder.right_onlineStatus.setVisibility(View.VISIBLE);
            } else {
                //offline
                holder.right_offlineStatus.setVisibility(View.VISIBLE);
                holder.right_onlineStatus.setVisibility(View.GONE);
            }

            if (chatModel.getIsEdited().equals("0")) {
                holder.right_editView.setVisibility(View.GONE);
            } else {
                holder.right_editView.setVisibility(View.VISIBLE);
                holder.right_edit.setText(context.getResources().getString(R.string.message_edit_alert) + " " + ChatDateFormat.getRequiredFormat(chatModel.getUpdatedTimeStamp(), "dd MMM HH:mm") + " -");
            }

            if (chatModel.getParentId().equals("0")) {
                holder.right_replyView.setVisibility(View.GONE);
            } else {
                try {
                    holder.right_replyView.setVisibility(View.VISIBLE);
                    holder.right_replyCardView.setVisibility(View.GONE);
                    holder.right_reply_document.setVisibility(View.GONE);
                    holder.right_replyVideoSymbols.setVisibility(View.GONE);

                    JSONObject jsonObject = new JSONObject(chatModel.getReplyList());
                    holder.right_replyView.setTag(String.valueOf(jsonObject.get("message_id")));

                    if (onItemClickListener instanceof ChatFragment) {
                        if (((ChatFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id"))).isEmpty()) {
                            if (jsonObject.has("user_name"))
                                holder.right_reply_name.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("user_name"))));
                            else
                                holder.right_reply_name.setText("");
                        } else {
                            holder.right_reply_name.setText(UtilMethods.getDecodedMessage(((ChatFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id")))));
                        }
                    } else if (onItemClickListener instanceof WhispererFragment) {
                        if (((WhispererFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id"))).isEmpty()) {
                            if (jsonObject.has("user_name"))
                                holder.right_reply_name.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("user_name"))));
                            else
                                holder.right_reply_name.setText("");
                        } else {
                            holder.right_reply_name.setText(UtilMethods.getDecodedMessage(((WhispererFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id")))));
                        }
                    }
                    holder.right_reply_message.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("message"))));
                    if (holder.right_reply_message.getText().toString().trim().length() == 0) {
                        holder.right_reply_message.setVisibility(View.GONE);
                    } else {
                        holder.right_reply_message.setVisibility(View.VISIBLE);
                    }
                    if (String.valueOf(jsonObject.get("message_type")).equals(ChatConstants.MESSAGE_IMAGE)) {
                        holder.right_replyCardView.setVisibility(View.VISIBLE);
                        showImageUsingGlide(holder.right_replyImageView, Constants.SOCKET_CHAT_IMAGE_URL + String.valueOf(jsonObject.get("filelink")));
                    } else if (String.valueOf(jsonObject.get("message_type")).equals(ChatConstants.MESSAGE_VIDEO)) {
                        holder.right_replyVideoSymbols.setVisibility(View.VISIBLE);
                        holder.right_replyCardView.setVisibility(View.VISIBLE);
                        showImageUsingGlide(holder.right_replyImageView, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + String.valueOf(jsonObject.get("thumbnail")));
                    } else if (String.valueOf(jsonObject.get("message_type")).equals(ChatConstants.MESSAGE_DOCS)) {
                        holder.right_replyCardView.setVisibility(View.GONE);
                        holder.right_reply_document.setVisibility(View.VISIBLE);
                        if (jsonObject.has("filename") && jsonObject.get("filename") != null && !jsonObject.get("filename").toString().isEmpty()) {
                            final String name = jsonObject.get("filename").toString();
                            if (name.contains(MediaUtil.FILE_NAME_SEPRATOR))
                                holder.right_reply_document.setText(name.substring(0, name.indexOf(MediaUtil.FILE_NAME_SEPRATOR)) + "." + FilenameUtils.getExtension(name));
                            else
                                holder.right_reply_document.setText(name);
                        } else {
                            holder.right_reply_document.setText(String.valueOf(jsonObject.get("filelink")));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.right_replyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null && v.getTag() != null) {
                            onItemClickListener.onReplyMessageClicked(v.getTag().toString());
                        }
                    }
                });
            }
            if (!chatModel.getMessageType().equals(ChatConstants.MESSAGE_TEXT)) {
                holder.right_mediaView.setVisibility(View.VISIBLE);
                holder.right_mediaImage.setVisibility(View.VISIBLE);
                holder.right_VideoSymbols.setVisibility(View.GONE);
                if (chatModel.isUploading() || chatModel.isDownloading()) {
                    holder.right_progress.setVisibility(View.VISIBLE);
                    if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_DOCS)) {
                        holder.right_document_progress.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.right_document_progress.setVisibility(View.GONE);
                    holder.right_progress.setVisibility(View.GONE);
                }
                if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_IMAGE)) {
                    if (chatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaPath()).exists()) {
                        holder.right_videoSymbols.setBackground(null);
                        holder.right_videoSymbols.setImageResource(0);
                        showMediaImageUsingGlide(holder.right_mediaImage, mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaPath()).getAbsolutePath());
                    } else {
                        showMediaImageUsingGlide(holder.right_mediaImage, Constants.SOCKET_CHAT_IMAGE_URL + chatModel.getFilelink());
                        holder.right_videoSymbols.setImageResource(R.mipmap.download_play);
                    }
                } else if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_VIDEO)) {
                    holder.right_VideoSymbols.setVisibility(View.VISIBLE);
                    if (chatModel.getLocalMediaThumbnail() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaThumbnail()).exists()) {
                        showMediaImageUsingGlide(holder.right_mediaImage, mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaThumbnail()).getAbsolutePath());
                    } else {
                        showMediaImageUsingGlide(holder.right_mediaImage, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + chatModel.getThumbnail());
                    }
                    if (chatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_VIDEO_PATH, chatModel.getLocalMediaPath()).exists()) {
                        holder.right_videoSymbols.setImageResource(R.mipmap.video_play);
                    } else {
                        holder.right_videoSymbols.setImageResource(R.mipmap.download_play);
                    }
                } else if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_DOCS)) {
                    holder.right_mediaView.setVisibility(View.GONE);
                    holder.tv_document_name.setVisibility(View.VISIBLE);
                    if (chatModel.getFileName() != null && !chatModel.getFileName().isEmpty()) {
                        if (chatModel.getFileName().contains(MediaUtil.FILE_NAME_SEPRATOR))
                            holder.tv_document_name.setText(chatModel.getFileName().substring(0, chatModel.getFileName().indexOf(MediaUtil.FILE_NAME_SEPRATOR)) + "." + FilenameUtils.getExtension(chatModel.getFileName()));
                        else
                            holder.tv_document_name.setText(chatModel.getFileName());
                    } else {
                        if (chatModel.getFilelink() != null && !chatModel.getFilelink().isEmpty()) {
                            holder.tv_document_name.setText(chatModel.getFilelink());
                        } else {
                            holder.tv_document_name.setText(context.getResources().getString(R.string.document));
                        }
                    }
                }
                if (chatModel.isError()) {
                    // holder.right_videoSymbols.setImageResource(R.mipmap.error);
                    holder.right_progress.setVisibility(View.GONE);
                }
            } else {
                holder.right_mediaView.setVisibility(View.GONE);
            }

            if (chatModel.getIsEdited().equals("0") && !chatModel.getMessageStatus().equals("0") && chatModel.getIsForwarded() != null && chatModel.getIsForwarded().equalsIgnoreCase(ChatConstants.MESSAGE_FORWARDED)) {
                holder.right_editView.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(chatModel.getForwardedOriginalMessage());
                    holder.right_edit.setText(context.getResources().getString(R.string.message_forward_alert) + " " + holder.right_userName.getText() + " " + ChatDateFormat.getRequiredFormat(chatModel.getTimeStamp(), "HH:mm") + " - ");
                    holder.right_time.setText(ChatDateFormat.getRequiredFormat(jsonObject.getString("created_at"), "HH:mm"));
                    holder.right_userName.setText(UtilMethods.getDecodedMessage(jsonObject.getString("user_name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (chatModel.getMessageStatus().equals("0")) {
                holder.right_editView.setVisibility(View.VISIBLE);
                holder.right_edit.setText(context.getResources().getString(R.string.message_delete_alert) + " " + ChatDateFormat.getRequiredFormat(chatModel.getUpdatedTimeStamp(), "dd MMM HH:mm") + " -");
                holder.right_mediaView.setVisibility(View.GONE);
                holder.right_text_message.setVisibility(View.GONE);
                holder.tv_document_name.setVisibility(View.GONE);
                holder.right_replyView.setVisibility(View.GONE);
                holder.swipeRevealLayout.setLockDrag(true);
            } else {
                holder.swipeRevealLayout.setLockDrag(false);
                holder.right_deleteView.setVisibility(View.GONE);
            }
        } else {
            holder.swipeRevealLayout.setDragEdge(SwipeRevealLayout.DRAG_EDGE_LEFT);
            holder.optionsView.setBackground(ContextCompat.getDrawable(context, R.drawable.green_left_corner));
            UtilMethods.setBackgroundTintColor(holder.optionsView, AppThemeResource.getAppThemeResource(context).getNormalButtonBackgroundColor());
            holder.option2.setBackground(ContextCompat.getDrawable(context, R.drawable.grey_left_corner));
            holder.option1.setTextColor(AppThemeResource.getAppThemeResource(context).getNormalButtonTextColor());
            holder.option2.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.option1.setText(context.getResources().getString(R.string.reply));
            holder.option2.setText(context.getResources().getString(R.string.forward));
            holder.option1.setBackground(null);
            holder.option2.setVisibility(View.VISIBLE);
            holder.option1.setTag(position);
            holder.option1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(v.getTag().toString());
                    if (onItemClickListener != null) {
                        onItemClickListener.onReplyClick(tag, getItem(tag));
                        hideDeleteOptionView();
                    }
                }
            });
            holder.option2.setTag(position);
            holder.option2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(v.getTag().toString());
                    if (onItemClickListener != null) {
                        onItemClickListener.onForwardClick(tag, getItem(tag));
                        hideDeleteOptionView();
                    }
                }
            });

            if (onItemClickListener instanceof WhispererFragment) {
                holder.option2.setVisibility(View.GONE);
            }

            //receiver block
            holder.left_view.setVisibility(View.VISIBLE);
            holder.right_view.setVisibility(View.GONE);
            holder.left_bubble.setTag(position);
            holder.left_bubble.setOnClickListener(this);
            holder.left_document_name.setTag(position);
            holder.left_document_name.setOnClickListener(this);
            holder.left_bubble.setOnLongClickListener(this);
            holder.left_videoSymbols.setBackground(null);
            holder.left_videoSymbols.setImageResource(0);
            holder.left_document_name.setVisibility(View.GONE);
            holder.left_document_name.setText("");
            holder.left_editView.setVisibility(View.GONE);
            holder.left_document_progress.setVisibility(View.GONE);
            holder.left_deleteView.setVisibility(View.GONE);

            if (chatModel.getMessage() != null && !chatModel.getMessage().isEmpty()) {
                holder.left_text_message.setText(UtilMethods.getDecodedMessage(chatModel.getMessage()));
                holder.left_text_message.setVisibility(View.VISIBLE);
            } else {
                holder.left_text_message.setVisibility(View.GONE);
            }

            //change drawable accroding to text message length
            if (chatModel.getMessageStatus().equals(ChatConstants.MESSAGE_DELETE_STATUS) || (chatModel.getMessageType().equals(ChatConstants.MESSAGE_TEXT) && chatModel.getParentId().equals("0") && !chatModel.getMessage().contains("\\n") && getNumberOfLine(holder.left_text_message.getTextSize(), (int) width, chatModel.getMessage()) < 2)) {
                holder.left_bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_white_box));
                UtilMethods.setBackgroundTintColor(holder.left_bubble, AppThemeResource.getAppThemeResource(context).getOtherChatBubbleColor());
                holder.left_shadow.setBackground(ContextCompat.getDrawable(context, R.drawable.chat_trannsprent_whitebox));
            } else {
                holder.left_bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.white_left));
                UtilMethods.setBackgroundTintColor(holder.left_bubble, AppThemeResource.getAppThemeResource(context).getOtherChatBubbleColor());
                holder.left_shadow.setBackground(ContextCompat.getDrawable(context, R.drawable.shadow_left));
            }

            holder.left_time.setText(ChatDateFormat.getRequiredFormat(getItem(position).getTimeStamp(), "HH:mm"));
            if (onItemClickListener instanceof ChatFragment) {
                if (((ChatFragment) onItemClickListener).getUserName(chatModel.getUserId()).isEmpty()) {
                    holder.left_userName.setText(UtilMethods.getDecodedMessage(chatModel.getUserName()));
                } else {
                    holder.left_userName.setText(UtilMethods.getDecodedMessage(((ChatFragment) onItemClickListener).getUserName(chatModel.getUserId())));
                }

                if (((ChatFragment) onItemClickListener).getProfilePicture(chatModel.getUserId()).isEmpty()) {
                    showImage(holder.left_userImage, chatModel.getUserProfile());
                } else {
                    showImage(holder.left_userImage, ((ChatFragment) onItemClickListener).getProfilePicture(chatModel.getUserId()));
                }
            } else if (onItemClickListener instanceof WhispererFragment) {
                if (((WhispererFragment) onItemClickListener).getUserName(chatModel.getUserId()).isEmpty()) {
                    holder.left_userName.setText(UtilMethods.getDecodedMessage(chatModel.getUserName()));
                } else {
                    holder.left_userName.setText(UtilMethods.getDecodedMessage(((WhispererFragment) onItemClickListener).getUserName(chatModel.getUserId())));
                }

                if (((WhispererFragment) onItemClickListener).getProfilePicture(chatModel.getUserId()).isEmpty()) {
                    showImage(holder.left_userImage, chatModel.getUserProfile());
                } else {
                    showImage(holder.left_userImage, ((WhispererFragment) onItemClickListener).getProfilePicture(chatModel.getUserId()));
                }
            }
            if (chatModel.isOnline()) {
                //online
                holder.left_offlineStatus.setVisibility(View.GONE);
                holder.left_onlineStatus.setVisibility(View.VISIBLE);
            } else {
                //offline
                holder.left_offlineStatus.setVisibility(View.VISIBLE);
                holder.left_onlineStatus.setVisibility(View.GONE);
            }

            if (chatModel.getIsEdited().equals("0")) {
                holder.left_editView.setVisibility(View.GONE);
            } else {
                holder.left_editView.setVisibility(View.VISIBLE);
                holder.left_editView.setText("- " + (context.getResources().getString(R.string.message_edit_alert) + " " + ChatDateFormat.getRequiredFormat(chatModel.getUpdatedTimeStamp(), "dd MMM HH:mm")));
            }

            if (chatModel.getParentId().equals("0")) {
                holder.left_replyView.setVisibility(View.GONE);
            } else {
                try {
                    holder.left_replyView.setVisibility(View.VISIBLE);
                    holder.left_replyCardView.setVisibility(View.GONE);
                    holder.left_replyVideoSymbols.setVisibility(View.GONE);
                    holder.left_reply_document.setVisibility(View.GONE);

                    JSONObject jsonObject = new JSONObject(chatModel.getReplyList());
                    holder.left_replyView.setTag(String.valueOf(jsonObject.get("message_id")));
                    if (onItemClickListener instanceof ChatFragment) {
                        if (((ChatFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id"))).isEmpty()) {
                            if (jsonObject.has("user_name"))
                                holder.left_reply_name.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("user_name"))));
                            else
                                holder.left_reply_name.setText("");
                        } else {
                            holder.left_reply_name.setText(UtilMethods.getDecodedMessage(((ChatFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id")))));
                        }
                    } else if (onItemClickListener instanceof WhispererFragment) {
                        if (((WhispererFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id"))).isEmpty()) {
                            if (jsonObject.has("user_name"))
                                holder.left_reply_name.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("user_name"))));
                            else
                                holder.left_reply_name.setText("");
                        } else {
                            holder.left_reply_name.setText(UtilMethods.getDecodedMessage(((WhispererFragment) onItemClickListener).getUserName(String.valueOf(jsonObject.get("user_id")))));
                        }
                    }
                    holder.left_reply_message.setText(UtilMethods.getDecodedMessage(String.valueOf(jsonObject.get("message"))));
                    if (holder.left_reply_message.getText().toString().trim().length() == 0) {
                        holder.left_reply_message.setVisibility(View.GONE);
                    } else {
                        holder.left_reply_message.setVisibility(View.VISIBLE);
                    }
                    if (String.valueOf(jsonObject.get("message_type")).equals(ChatConstants.MESSAGE_IMAGE)) {
                        holder.left_replyCardView.setVisibility(View.VISIBLE);
                        showImageUsingGlide(holder.left_replyImageView, Constants.SOCKET_CHAT_IMAGE_URL + String.valueOf(jsonObject.get("filelink")));
                    } else if (String.valueOf(jsonObject.get("message_type")).equals(ChatConstants.MESSAGE_VIDEO)) {
                        holder.left_replyVideoSymbols.setVisibility(View.VISIBLE);
                        holder.left_replyCardView.setVisibility(View.VISIBLE);
                        showImageUsingGlide(holder.left_replyImageView, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + String.valueOf(jsonObject.get("thumbnail")));
                    } else if (String.valueOf(jsonObject.get("message_type")).equals(ChatConstants.MESSAGE_DOCS)) {
                        holder.left_replyCardView.setVisibility(View.GONE);
                        holder.left_reply_document.setVisibility(View.VISIBLE);
                        if (jsonObject.has("filename") && jsonObject.get("filename") != null && !jsonObject.get("filename").toString().isEmpty()) {
                            final String name = jsonObject.get("filename").toString();
                            if (name.contains(MediaUtil.FILE_NAME_SEPRATOR))
                                holder.left_reply_document.setText(name.substring(0, name.indexOf(MediaUtil.FILE_NAME_SEPRATOR)) + "." + FilenameUtils.getExtension(name));
                            else
                                holder.left_reply_document.setText(name);
                        } else {
                            holder.left_reply_document.setText(String.valueOf(jsonObject.get("filelink")));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.left_replyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null && v.getTag() != null) {
                            onItemClickListener.onReplyMessageClicked(v.getTag().toString());
                        }
                    }
                });
            }

            if (!chatModel.getMessageType().equals(ChatConstants.MESSAGE_TEXT)) {
                holder.left_mediaImage.setVisibility(View.VISIBLE);
                holder.left_mediaView.setVisibility(View.VISIBLE);
                holder.left_VideoSymbols.setVisibility(View.GONE);
                if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_IMAGE)) {
                    if (chatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaPath()).exists()) {
                        holder.left_videoSymbols.setBackground(null);
                        holder.left_videoSymbols.setImageResource(0);
                        showMediaImageUsingGlide(holder.left_mediaImage, ((mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaPath()).exists()) ? mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_IMAGE_PATH, chatModel.getLocalMediaPath()).getAbsolutePath() : Constants.SOCKET_CHAT_IMAGE_URL + chatModel.getFilelink()));
                    } else {
                        holder.left_videoSymbols.setImageResource(R.mipmap.download_play);
                        showMediaImageUsingGlide(holder.left_mediaImage, Constants.SOCKET_CHAT_IMAGE_URL + chatModel.getFilelink());
                    }
                } else if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_VIDEO)) {
                    holder.left_VideoSymbols.setVisibility(View.VISIBLE);
                    showMediaImageUsingGlide(holder.left_mediaImage, Constants.SOCKET_CHAT_VIDEO_THUMBNAIL_URL + chatModel.getThumbnail());
                    if (chatModel.getLocalMediaPath() != null && mediaUtil.getFolderPath(ChatConstants.LOCAL_FOLDER_VIDEO_PATH, chatModel.getLocalMediaPath()).exists()) {
                        holder.left_videoSymbols.setImageResource(R.mipmap.video_play);
                    } else {
                        holder.left_videoSymbols.setImageResource(R.mipmap.download_play);
                    }
                } else if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_DOCS)) {
                    holder.left_mediaView.setVisibility(View.GONE);
                    holder.left_document_name.setVisibility(View.VISIBLE);
                    if (chatModel.getFileName() != null && !chatModel.getFileName().isEmpty()) {
                        if (chatModel.getFileName().contains(MediaUtil.FILE_NAME_SEPRATOR))
                            holder.left_document_name.setText(chatModel.getFileName().substring(0, chatModel.getFileName().indexOf(MediaUtil.FILE_NAME_SEPRATOR)) + "." + FilenameUtils.getExtension(chatModel.getFileName()));
                        else
                            holder.left_document_name.setText(chatModel.getFileName());
                    } else {
                        if (chatModel.getFilelink() != null && !chatModel.getFilelink().isEmpty()) {
                            holder.left_document_name.setText(chatModel.getFilelink());
                        } else {
                            holder.left_document_name.setText(context.getResources().getString(R.string.document));
                        }
                    }
                }

                if (chatModel.isUploading() || chatModel.isDownloading()) {
                    holder.left_progress.setVisibility(View.VISIBLE);
                    if (chatModel.getMessageType().equals(ChatConstants.MESSAGE_DOCS)) {
                        holder.left_document_progress.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.left_document_progress.setVisibility(View.GONE);
                    holder.left_progress.setVisibility(View.GONE);
                }
                if (chatModel.isError()) {
                    holder.left_progress.setVisibility(View.GONE);
                }
            } else {
                holder.left_mediaView.setVisibility(View.GONE);
            }

            if (chatModel.getIsEdited().equals("0") && !chatModel.getMessageStatus().equals("0") && chatModel.getIsForwarded() != null && chatModel.getIsForwarded().equalsIgnoreCase(ChatConstants.MESSAGE_FORWARDED)) {
                holder.left_editView.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(chatModel.getForwardedOriginalMessage());
                    holder.left_editView.setText(" - " + ChatDateFormat.getRequiredFormat(chatModel.getTimeStamp(), "HH:mm") + " " + holder.left_userName.getText() + " " + context.getResources().getString(R.string.message_forward_alert));
                    holder.left_time.setText(ChatDateFormat.getRequiredFormat(jsonObject.getString("created_at"), "HH:mm"));
                    holder.left_userName.setText(UtilMethods.getDecodedMessage(jsonObject.getString("user_name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (chatModel.getMessageStatus().equals("0")) {
                holder.left_editView.setVisibility(View.VISIBLE);
                holder.left_editView.setText("- " + (context.getResources().getString(R.string.message_delete_alert) + " " + ChatDateFormat.getRequiredFormat(chatModel.getUpdatedTimeStamp(), "dd MMM HH:mm")));
                holder.left_text_message.setVisibility(View.GONE);
                holder.left_mediaView.setVisibility(View.GONE);
                holder.left_document_name.setVisibility(View.GONE);
                holder.left_replyView.setVisibility(View.GONE);
                holder.swipeRevealLayout.setLockDrag(true);
            } else {
                holder.swipeRevealLayout.setLockDrag(false);
                holder.left_deleteView.setVisibility(View.GONE);
            }
        }
    }


    private void showMediaImageUsingGlide(ImageView imageView, String path) {
        Glide.with(context)
                .load(path)
                .centerCrop()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(imageView);
    }

    private void showImageUsingGlide(ImageView imageView, String path) {
        Glide.with(context)
                .load(path)
                .centerCrop()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(imageView);
    }

    private void showImage(final ImageView imageView, String path) {
        AppThemeResource.getAppThemeResource(context).setImagePlaceHolderThemeColor(imageView);
        Glide.with(context).load(path).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                AppThemeResource.getAppThemeResource(context).setImagePlaceHolderDefaultThemeColor(imageView);
                return false;
            }
        }).bitmapTransform(new RoundedCornersTransformation(context, Constants.PLACE_HOLDER_RADIOUS, 0)).placeholder(R.mipmap.upload_icon).error(R.mipmap.upload_icon).into(imageView);
    }

    @Override
    public void onClick(View v) {
        int tag = Integer.parseInt(v.getTag().toString());
        if (onItemClickListener != null && !getItem(tag).getMessageStatus().equals("0")) {
            onItemClickListener.onItemClick(tag, getItem(tag));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int tag = Integer.parseInt(v.getTag().toString());
        if (onItemClickListener != null && !getItem(tag).getMessageStatus().equals("0")) {
            onItemClickListener.onItemLongClick(tag, getItem(tag));
        }
        return true;
    }

    private boolean isNextLogMessage(int pos) {
        if (pos + 1 < getItemCount()) {
            if (getItem(pos + 1).getMessageType().equals(ChatConstants.MESSAGE_CREATE_GROUP) || getItem(pos + 1).getMessageType().equals(ChatConstants.MESSAGE_JOIN_GROUP) || getItem(pos + 1).getMessageType().equals(ChatConstants.MESSAGE_LEFT_GROUP)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPreviousLogMessage(int pos) {
        if (pos - 1 >= 0) {
            if (getItem(pos - 1).getMessageType().equals(ChatConstants.MESSAGE_CREATE_GROUP) || getItem(pos - 1).getMessageType().equals(ChatConstants.MESSAGE_JOIN_GROUP) || getItem(pos - 1).getMessageType().equals(ChatConstants.MESSAGE_LEFT_GROUP)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLogMessage(int pos) {
        if (pos < getItemCount()) {
            if (getItem(pos).getMessageType().equals(ChatConstants.MESSAGE_CREATE_GROUP) || getItem(pos).getMessageType().equals(ChatConstants.MESSAGE_JOIN_GROUP) || getItem(pos).getMessageType().equals(ChatConstants.MESSAGE_LEFT_GROUP)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBottomShowHeader(int pos) {
        if (pos >= 0 && pos + 1 < getItemCount()) {
            Date curr = ChatDateFormat.getDateObject(getItem(pos).getTimeStamp());
            Date per = ChatDateFormat.getDateObject(getItem(pos + 1).getTimeStamp());
            if (curr.getYear() != per.getYear())
                return true;
            if (curr.getMonth() != per.getMonth())
                return true;
            if (curr.getDay() != per.getDay())
                return true;
            return false;
        }
        return false;
    }

    private boolean isTopShowHeader(int pos) {
        if (pos < getItemCount() && pos + 1 < getItemCount()) {
            Date curr = ChatDateFormat.getDateObject(getItem(pos).getTimeStamp());
            Date per = ChatDateFormat.getDateObject(getItem(pos + 1).getTimeStamp());
            if (curr.getYear() != per.getYear())
                return true;
            if (curr.getMonth() != per.getMonth())
                return true;
            if (curr.getDay() != per.getDay())
                return true;
            return false;
        }
        return false;
    }

    private boolean showheader(int pos) {
        if (pos + 1 < getItemCount()) {
            Date curr = ChatDateFormat.getDateObject(getItem(pos).getTimeStamp());
            Date per = ChatDateFormat.getDateObject(getItem(pos + 1).getTimeStamp());
            if (curr.getYear() != per.getYear())
                return true;
            if (curr.getMonth() != per.getMonth())
                return true;
            if (curr.getDay() != per.getDay())
                return true;
            return false;
        }
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EmoticonTextView left_text_message;
        private EmoticonTextView left_reply_name;
        private EmoticonTextView right_reply_name;
        private EmoticonTextView left_reply_message;
        private EmoticonTextView right_reply_message;
        private EmoticonTextView right_text_message;
        private RelativeLayout left_bubble;
        private LinearLayout left_view;
        private LinearLayout right_view;
        private LinearLayout right_bubble;
        private View right_replyView;
        private View right_shadow;
        private View left_shadow;
        private View left_replyView;
        private TextView left_deleteView;
        private TextView right_deleteView;
        private EmoticonTextView left_editView;
        private EmoticonTextView right_edit;
        private TextView tv_date;
        private TextView left_time;
        private TextView right_reply_document;
        private TextView left_reply_document;
        private TextView right_time;
        private EmoticonTextView right_userName;
        private EmoticonTextView left_userName;
        private LinearLayout right_editView;
        private ImageView left_userImage;
        private ImageView left_mediaImage;
        private ImageView right_userImage;
        private ImageView right_mediaImage;
        private TextView right_onlineStatus;
        private TextView left_onlineStatus;
        private TextView right_offlineStatus;
        private TextView left_offlineStatus;
        private CardView left_mediaView;
        private CardView right_mediaView;
        private ProgressBar left_progress;
        private ProgressBar right_progress;
        private ProgressBar right_document_progress;
        private ProgressBar left_document_progress;
        private ImageView left_videoSymbols;
        private ImageView right_videoSymbols;
        private CardView left_replyCardView;
        private CardView right_replyCardView;
        private ImageView left_replyImageView;
        private ImageView right_replyImageView;
        private View right_VideoSymbols;
        private View left_VideoSymbols;
        private View right_replyVideoSymbols;
        private View left_replyVideoSymbols;
        private SwipeRevealLayout swipeRevealLayout;
        private EmoticonTextView tv_left_log;
        private EmoticonTextView tv_right_log;
        private TextView option2;
        private TextView option1;
        private TextView tv_document_name;
        private TextView left_document_name;
        private LinearLayout optionsView;
        private View mainView;
        private View parentView, leftDrawableView, rightDrawableView;

        public ViewHolder(View view) {
            super(view);
            mainView = view.findViewById(R.id.mainView);
            parentView = view.findViewById(R.id.parentView);
            left_view = view.findViewById(R.id.left_view);
            right_view = view.findViewById(R.id.right_view);
            left_bubble = view.findViewById(R.id.left_box);
            left_shadow = view.findViewById(R.id.left_shadow);
            UtilMethods.setBackgroundTintColor(left_bubble, AppThemeResource.getAppThemeResource(context).getOtherChatBubbleColor());
            right_bubble = view.findViewById(R.id.right_box);
            right_shadow = view.findViewById(R.id.right_shadow);
            UtilMethods.setBackgroundTintColor(right_bubble, AppThemeResource.getAppThemeResource(context).getMyChatBubbleColor());
            left_text_message = view.findViewById(R.id.left_text_message);
            right_text_message = view.findViewById(R.id.right_text_message);
            right_userName = view.findViewById(R.id.right_userName);
            left_userName = view.findViewById(R.id.left_userName);
            right_replyView = view.findViewById(R.id.right_replyView);
            left_replyView = view.findViewById(R.id.left_replyView);
            left_reply_name = view.findViewById(R.id.left_reply_name);
            right_reply_name = view.findViewById(R.id.right_reply_name);
            left_reply_message = view.findViewById(R.id.left_reply_message);
            right_reply_message = view.findViewById(R.id.right_reply_message);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (context.getResources().getDisplayMetrics().widthPixels - UtilMethods.getPixelFromDP(140, context)), View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            left_text_message.measure(widthMeasureSpec, heightMeasureSpec);
            right_text_message.measure(widthMeasureSpec, heightMeasureSpec);
            width = left_text_message.getMeasuredWidth();
            left_deleteView = view.findViewById(R.id.left_deleteView);
            right_deleteView = view.findViewById(R.id.right_deleteView);
            left_editView = view.findViewById(R.id.left_editView);
            right_editView = view.findViewById(R.id.right_editView);
            tv_date = view.findViewById(R.id.tv_date);
            left_time = view.findViewById(R.id.left_time);
            right_time = view.findViewById(R.id.right_time);
            left_userImage = view.findViewById(R.id.left_userImage);
            right_userImage = view.findViewById(R.id.right_userImage);
            left_onlineStatus = view.findViewById(R.id.left_onlineStatus);
            right_onlineStatus = view.findViewById(R.id.right_onlineStatus);
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.online_circle_drawable);
            drawable.setColor(AppThemeResource.getAppThemeResource(context).getSelectedIconColor());
            left_onlineStatus.setBackground(drawable);
            right_onlineStatus.setBackground(drawable);
            left_offlineStatus = view.findViewById(R.id.left_offlineStatus);
            right_offlineStatus = view.findViewById(R.id.right_offlineStatus);
            GradientDrawable offlineDrawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.offline_circle_drawable);
            offlineDrawable.setStroke(1, AppThemeResource.getAppThemeResource(context).getSelectedIconColor());
            right_offlineStatus.setBackground(offlineDrawable);
            left_offlineStatus.setBackground(offlineDrawable);
            left_mediaView = view.findViewById(R.id.left_mediaView);
            right_mediaView = view.findViewById(R.id.right_mediaView);
            left_mediaImage = view.findViewById(R.id.left_imageView);
            right_mediaImage = view.findViewById(R.id.right_imageView);
            left_progress = view.findViewById(R.id.left_progress);
            left_progress.getIndeterminateDrawable().setColorFilter(AppThemeResource.getAppThemeResource(context).getUnSelectedWheelBackgroundColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
            right_progress = view.findViewById(R.id.right_progress);
            right_progress.getIndeterminateDrawable().setColorFilter(AppThemeResource.getAppThemeResource(context).getUnSelectedWheelBackgroundColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
            right_document_progress = view.findViewById(R.id.right_document_progress);
            right_document_progress.getIndeterminateDrawable().setColorFilter(AppThemeResource.getAppThemeResource(context).getUnSelectedWheelBackgroundColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
            left_document_progress = view.findViewById(R.id.left_document_progress);
            left_document_progress.getIndeterminateDrawable().setColorFilter(AppThemeResource.getAppThemeResource(context).getUnSelectedWheelBackgroundColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
            right_videoSymbols = view.findViewById(R.id.right_videoSymbol);
            left_videoSymbols = view.findViewById(R.id.left_videoSymbol);
            left_replyCardView = view.findViewById(R.id.left_replyCardView);
            right_replyCardView = view.findViewById(R.id.right_replyCardView);
            left_replyImageView = view.findViewById(R.id.left_replyImageView);
            right_replyImageView = view.findViewById(R.id.right_replyImageView);
            right_VideoSymbols = view.findViewById(R.id.right_VideoSymbols);
            left_VideoSymbols = view.findViewById(R.id.left_VideoSymbols);
            right_VideoSymbols = view.findViewById(R.id.right_VideoSymbols);
            left_VideoSymbols = view.findViewById(R.id.left_VideoSymbols);
            right_replyVideoSymbols = view.findViewById(R.id.right_replyVideoSymbols);
            left_replyVideoSymbols = view.findViewById(R.id.left_replyVideoSymbols);
            swipeRevealLayout = view.findViewById(R.id.swipeRevealLayout);
            option1 = view.findViewById(R.id.option1);
            option2 = view.findViewById(R.id.option2);
            optionsView = view.findViewById(R.id.optionsView);
            right_reply_document = view.findViewById(R.id.right_reply_document);
            left_reply_document = view.findViewById(R.id.left_reply_document);
            tv_document_name = view.findViewById(R.id.tv_document_name);
            left_document_name = view.findViewById(R.id.left_document_name);
            tv_left_log = view.findViewById(R.id.tv_left_log);
            UtilMethods.setBackgroundTintColor(tv_left_log, AppThemeResource.getAppThemeResource(context).getNormalButtonBackgroundColor());
            tv_left_log.setTextColor(AppThemeResource.getAppThemeResource(context).getNormalButtonTextColor());
            tv_right_log = view.findViewById(R.id.tv_right_log);
            right_edit = view.findViewById(R.id.right_edit);
            rightDrawableView = view.findViewById(R.id.rightDrawableView);
            leftDrawableView = view.findViewById(R.id.leftDrawableView);
            leftDrawableView.setBackgroundColor(AppThemeResource.getAppThemeResource(context).getNormalButtonTextColor());
            rightDrawableView.setBackgroundColor(AppThemeResource.getAppThemeResource(context).getNormalButtonTextColor());


        }
    }
}