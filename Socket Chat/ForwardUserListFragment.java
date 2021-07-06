package com.bawa.inr.livechat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bawa.inr.Constants;
import com.bawa.inr.R;
import com.bawa.inr.base.BaseActivity;
import com.bawa.inr.base.BaseFragment;
import com.bawa.inr.model.BasicResponse;
import com.bawa.inr.model.ChannelModel;
import com.bawa.inr.model.MenuModel;
import com.bawa.inr.model.MyTeamsResponse;
import com.bawa.inr.realmmodels.MyTeamRealm;
import com.bawa.inr.rest.Apis;
import com.bawa.inr.ui.HomeAdapter;
import com.bawa.inr.ui.MainActivity;
import com.bawa.inr.ui.brief.board.CreateTeamFragment;
import com.bawa.inr.util.AppThemeResource;
import com.bawa.inr.util.ConversionUtils;
import com.bawa.inr.util.JsonUtil;
import com.bawa.inr.util.ServiceGenerator;
import com.bawa.inr.util.UtilMethods;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForwardUserListFragment extends BaseFragment {

    @BindView(R.id.top)
    View up_view;
    @BindView(R.id.middle_view)
    View middle_view;
    @BindView(R.id.bottom)
    View bottom;
    @BindView(R.id.ttalk_message)
    View ttalk_message;
    @BindView(R.id.whisper_message)
    View whisper_message;
    @BindView(R.id.recyclerView_userList)
    RecyclerView recyclerView_userList;
    @BindView(R.id.recyclerView_whisperList)
    RecyclerView recyclerView_whisperList;
    @BindView(R.id.tv_forward)
    TextView tv_forward;

    private MyTeamRealm selectedTeam;
    private String forwardMessageId;
    private ArrayList<ChannelModel> ttalkArraylist;
    private UserListAdapter ttalkAdapter;
    private ArrayList<ChannelModel> whisperArraylist;
    private UserListAdapter whisperAdapter;
    private ArrayList<ChannelModel> selectedChannelArraylist;


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_forward_user_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

        if (UtilMethods.isInternetAvailable(getActivity(), true)) {
            getChannelList();
        } else {
            init();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).tv_action != null) {
            ((MainActivity) getActivity()).tv_action.setVisibility(View.GONE);
        }
    }

    private void init() {
        forwardMessageId = bundle.getString("message_id");
        Log.e("Forward Message Id: ", "" + forwardMessageId);

        ttalkAdapter = new UserListAdapter(getActivity(), ttalkArraylist);
        recyclerView_userList.setAdapter(ttalkAdapter);

        whisperAdapter = new UserListAdapter(getActivity(), whisperArraylist);
        recyclerView_whisperList.setAdapter(whisperAdapter);


        recyclerView_userList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (ttalkAdapter != null) {
                    if (ttalkAdapter.getItemCount() > 0) {
                        ttalk_message.setVisibility(View.GONE);
                    } else {
                        ttalk_message.setVisibility(View.VISIBLE);
                    }
                }
                recyclerView_userList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        recyclerView_whisperList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (whisperAdapter != null) {
                    if (whisperAdapter.getItemCount() > 0) {
                        whisper_message.setVisibility(View.GONE);
                    } else {
                        whisper_message.setVisibility(View.VISIBLE);
                    }
                }
                recyclerView_whisperList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initView() {
        selectedTeam = (MyTeamRealm) realmHandler.getKeyModel(MyTeamRealm.class, "isSeleted", true);

        selectedChannelArraylist = new ArrayList<>();
        ttalkArraylist = new ArrayList<>();
        whisperArraylist = new ArrayList<>();

        if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).tv_action != null) {
            ((MainActivity) getActivity()).tv_action.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).tv_action.setText(UtilMethods.getDecodedMessage(selectedTeam.getTeam_name()));
        }

        UtilMethods.setBackgroundTintColor(up_view, AppThemeResource.getAppThemeResource(getActivity()).getBoxColor());
        UtilMethods.setBackgroundTintColor(bottom, AppThemeResource.getAppThemeResource(getActivity()).getBoxColor());
        UtilMethods.setBackgroundTintColor(middle_view, AppThemeResource.getAppThemeResource(getActivity()).getBoxColor());
        tv_forward.setTextColor(AppThemeResource.getAppThemeResource(getActivity()).getNormalButtonTextColor());

        LinearLayoutManager ttalkManager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView_userList.setHasFixedSize(true);
        recyclerView_userList.setLayoutManager(ttalkManager);

        LinearLayoutManager whisperManager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView_whisperList.setHasFixedSize(true);
        recyclerView_whisperList.setLayoutManager(whisperManager);
    }

    @OnClick(R.id.tv_forward)
    public void onForwardClick(TextView view) {
        if (UtilMethods.isInternetAvailable(getActivity(), true)) {
            forwardMessage();
        }
    }

    private void forwardMessage() {
        if (getSelectedChannel() == null)
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", ConversionUtils.encrypt_for_body(getUserid()));
        map.put("channel_names", ConversionUtils.encrypt_for_body(String.valueOf(getSelectedChannel())));
        map.put("message_ids", ConversionUtils.encrypt_for_body(String.valueOf(getMessageId())));
        if (getActivity() instanceof BaseActivity && ((BaseActivity) getActivity()).mAnimationView != null) {
            ((BaseActivity) getActivity()).mAnimationView.setVisibility(View.VISIBLE);
        }
        Call<BasicResponse> call = UploadMediaRetrofit.createService(Apis.class).forwardMessages(Constants.AUTH_KEY, getToken(), ConversionUtils.encrypt_for_body(getUserid()), map);
        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                try {
                    if (getActivity() instanceof BaseActivity && ((BaseActivity) getActivity()).mAnimationView != null) {
                        ((BaseActivity) getActivity()).mAnimationView.setVisibility(View.GONE);
                    }
                    if (response != null && response.isSuccessful() && response.body() != null && response.body().getStatus() == Constants.SUCCESS) {
                        showBaseToast(response.body().getMessage());
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).backClicked();
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
                if (getActivity() instanceof BaseActivity && ((BaseActivity) getActivity()).mAnimationView != null) {
                    ((BaseActivity) getActivity()).mAnimationView.setVisibility(View.GONE);
                }
                showFailureToast(getActivity(), c);
            }
        });
    }

    private JsonArray getSelectedChannel() {
        if (selectedChannelArraylist.size() > 0) {
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < selectedChannelArraylist.size(); i++) {
                jsonArray.add(selectedChannelArraylist.get(i).getChannel_name());
            }
            return jsonArray;
        } else
            return null;
    }

    private JsonArray getMessageId() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(forwardMessageId);
        return jsonArray;
    }

    private void getChannelList() {
        showBaseProgressDialog(getResourceString(R.string.please_wait));
        Call<BasicResponse> call = UploadMediaRetrofit.createService(Apis.class).getChannelList(Constants.AUTH_KEY, getToken(), ConversionUtils.encrypt_for_body(getUserid()), ConversionUtils.encrypt_for_body(getUserid()));
        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                try {
                    hideBaseProgressDialog();
                    if (response != null && response.isSuccessful() && response.body() != null && response.body().getStatus() == Constants.SUCCESS) {
                        JSONObject dataObject = new JSONObject(ConversionUtils.decrypt_text(response.body().getData()));

                        JSONArray whisper = (JSONArray) dataObject.get("whisper_channels");
                        for (int i = 0; i < whisper.length(); i++) {
                            JSONObject object = (JSONObject) whisper.get(i);
                            ChannelModel channelModel = new ChannelModel();
                            channelModel.setDisplay_name(String.valueOf(object.get("display_name")));
                            channelModel.setChannel_id(String.valueOf(object.get("channel_id")));
                            channelModel.setChannel_name(String.valueOf(object.get("channel_name")));
                            whisperArraylist.add(channelModel);
                        }

                        JSONArray ttalk = (JSONArray) dataObject.get("ttalk_channels");
                        for (int i = 0; i < ttalk.length(); i++) {
                            JSONObject object = (JSONObject) ttalk.get(i);
                            ChannelModel channelModel = new ChannelModel();
                            channelModel.setDisplay_name(String.valueOf(object.get("display_name")));
                            channelModel.setChannel_id(String.valueOf(object.get("channel_id")));
                            channelModel.setChannel_name(String.valueOf(object.get("channel_name")));
                            ttalkArraylist.add(channelModel);
                        }
                    } else {
                        handleErrorResponseForMyCard(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                init();
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable c) {
                hideBaseProgressDialog();
                showFailureToast(getActivity(), c);
                init();
            }
        });
    }


    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
        private Context context;
        private ArrayList<ChannelModel> userList;


        public UserListAdapter(Context context, ArrayList<ChannelModel> userList) {
            this.context = context;
            this.userList = userList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_forward_user_list, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv_name.setText(UtilMethods.getDecodedMessage(userList.get(position).getDisplay_name()));
            holder.checkBox.setTag(position);
            if (selectedChannelArraylist.contains(userList.get(position))) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        selectedChannelArraylist.add(userList.get(Integer.parseInt(v.getTag().toString())));
                    } else {
                        if (selectedChannelArraylist.contains(userList.get(Integer.parseInt(v.getTag().toString())))) {
                            selectedChannelArraylist.remove(userList.get(Integer.parseInt(v.getTag().toString())));
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            EmoticonEditText tv_name;
            CheckBox checkBox;

            public ViewHolder(View v) {
                super(v);
                tv_name = v.findViewById(R.id.tv_name);
                checkBox = v.findViewById(R.id.cb_tickbox);
            }
        }
    }

    @Override
    public void INRMenuClick(View view) {
        ArrayList<MenuModel> menuModelArrayList = new ArrayList<>();
        ((MainActivity) getActivity()).showINRMenuDialog(view, menuModelArrayList);
    }

    @Override
    public void INRMenuOptionClick(View view, String clickMenu) {
    }
}
