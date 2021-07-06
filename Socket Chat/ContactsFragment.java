package com.bawa.inr.livechat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bawa.inr.R;
import com.bawa.inr.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class ContactsFragment extends BaseFragment {

    @BindView(R.id.recyclerView_userList)
    RecyclerView recyclerView_userList;


    private Activity activity;
    private ArrayList<String> userList;

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_contacts;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView_userList.setLayoutManager(linearLayoutManager);


        userList = new ArrayList<>();
        recyclerView_userList.setAdapter(new UserListAdapter(activity, userList));
    }


    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
        private Context context;
        private ArrayList<String> userList;


        public UserListAdapter(Context context, ArrayList<String> userList) {
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

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View v) {
                super(v);
            }
        }
    }


}
