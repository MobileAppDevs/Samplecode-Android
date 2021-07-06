package com.nibou.nibouexpert.activitys;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.adapter.EarningListAdapter;
import com.nibou.nibouexpert.adapter.ReviewListAdapter;
import com.nibou.nibouexpert.databinding.ActivityEarningBinding;

import java.util.ArrayList;

public class EarningActivity extends BaseActivity {

    private ActivityEarningBinding binding;
    private EarningListAdapter earningListAdapter ;
    private ArrayList<String> dateList = new ArrayList<>();
    private ArrayList<String> moneyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earning);
        setToolbar();
        init();
    }

    private void init() {
        moneyList.add("MARCH 2019") ;
        moneyList.add("JAN 2019") ;
        moneyList.add("DECEMBER 2019") ;
        moneyList.add("APRIL 2019") ;

        dateList.add("102 min - unpaid") ;
        dateList.add("99  min - unpaid") ;
        dateList.add("102 min - unpaid") ;
        dateList.add("25 min - paid") ;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.earningList.setLayoutManager(layoutManager);
        earningListAdapter = new EarningListAdapter(this,moneyList ,dateList);
        binding.earningList.setAdapter(earningListAdapter);
    }

    private void setToolbar() {
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setOnClickListener(v -> {
            onBackPressed();
        });

    }
}
