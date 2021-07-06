package com.bawa.inr.livechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bawa.inr.R;
import com.bawa.inr.base.BaseActivity;
import com.bawa.inr.util.UtilMethods;

import butterknife.OnClick;

public class ContactSharingActivity extends BaseActivity {


    @Override
    protected int getContentView() {
        return R.layout.activity_contact_activity;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        iv_back.setVisibility(View.VISIBLE);

        UtilMethods.setFragment(R.id.frameLayout, getSupportFragmentManager(), new ContactsFragment(), "ForwardUserListFragment", false, true);
    }

    @OnClick(R.id.iv_back)
    public void onBackButtonClicked() {
        finish();
    }
}
