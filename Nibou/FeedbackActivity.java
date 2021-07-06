package com.nibou.nibouexpert.activitys;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.databinding.ActivityFeedbackBinding;

public class FeedbackActivity extends BaseActivity {

    private ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        setToolbar();
    }

    private void setToolbar() {

        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);

        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setOnClickListener(v -> {
            onBackPressed();
        });

    }
}
