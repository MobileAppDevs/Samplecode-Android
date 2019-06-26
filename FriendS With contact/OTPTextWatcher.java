package com.joe.friendscontacts.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import com.joe.friendscontacts.R;
import com.joe.friendscontacts.databinding.FragmentEnterPhoneBinding;

/**
 * Created By Madhur on 15/11/18 , 2:42 PM
 */
public class OTPTextWatcher implements TextWatcher {

    private View view;
    private FragmentEnterPhoneBinding binding;
    public OTPTextWatcher(View view, FragmentEnterPhoneBinding binding)
    {
        this.view = view;
        this.binding = binding;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();

        switch(view.getId())
        {
            case R.id.enterVerificationCode1:
                if(text.length()==1)
                    binding.enterVerificationCode2.requestFocus();
                else if (TextUtils.isEmpty(text)){
                    binding.enterVerificationCode1.clearFocus();
                }
                break;
            case R.id.enterVerificationCode2:
                if(text.length()==1)
                    binding.enterVerificationCode3.requestFocus();
                else if (TextUtils.isEmpty(text)){
                    binding.enterVerificationCode1.requestFocus();
                }
                break;
            case R.id.enterVerificationCode3:
                if(text.length()==1)
                    binding.enterVerificationCode4.requestFocus();
                else if (TextUtils.isEmpty(text)){
                    binding.enterVerificationCode2.requestFocus();
                }
                break;
            case R.id.enterVerificationCode4:
                if(text.length()==1)
                    binding.enterVerificationCode5.requestFocus();
                else if (TextUtils.isEmpty(text)){
                    binding.enterVerificationCode3.requestFocus();
                }
                break;
            case R.id.enterVerificationCode5:
                if(text.length()==1)
                    binding.enterVerificationCode6.requestFocus();
                else if (TextUtils.isEmpty(text)){
                    binding.enterVerificationCode4.requestFocus();
                }
                break;
            case R.id.enterVerificationCode6:
                if(text.length()==1)
                    binding.enterVerificationCode6.clearFocus();
                else if (TextUtils.isEmpty(text)){
                    binding.enterVerificationCode5.requestFocus();
                }
                break;

        }
    }
}
