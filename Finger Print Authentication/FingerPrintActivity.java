package com.bawa.inr.fingerprintlogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bawa.inr.R;
import com.bawa.inr.base.BaseActivity;
import com.bawa.inr.util.UtilMethods;

public class FingerPrintActivity extends BaseActivity implements FingerTouchHandler.FingerPrintCallback {

    @Override
    public void onBackPressed() {
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_fingerprint;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        iv_back.setVisibility(View.INVISIBLE);
        try {
            new FingerTouchHandler(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fingerAutenhticationSuccess() {
        finish();
    }

    @Override
    public void fingerAutenhticationFailed() {
        UtilMethods.vibrateDevice(this);
    }
}
