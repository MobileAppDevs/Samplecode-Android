package com.bawa.inr.livechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bawa.inr.R;
import com.bawa.inr.ui.news_module.fragments.CreateNewFragment;
import com.bawa.inr.util.AppThemeResource;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.esafirm.imagepicker.model.Image;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PhotoEditActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    Unbinder unbinder;
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.bottomRecylerView)
    RecyclerView bottomRecylerView;
    @BindView(R.id.bottom)
    RelativeLayout bottom;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_done)
    TextView tv_done;

    private int currentImagePosition;
    private ArrayList<Image> imaArrayList;
    private SmallImageAdapter smallImageAdapter;
    private FullScreenImageAdapter fullScreenImageAdapter;
    private int cropType;
    private boolean deleteOldFile;

    private File requestCropingFile;

    private int width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        width = getResources().getDisplayMetrics().widthPixels;
        unbinder = ButterKnife.bind(this);
        imaArrayList = new ArrayList<>();
        cropType = getIntent().getIntExtra("cropType", 2);
        if (getIntent().hasExtra("deleteFile")) {
            deleteOldFile = true;
        }
        if (CreateNewFragment.isForceCrop)
            tv_done.setVisibility(View.GONE);
        else
            tv_done.setVisibility(View.VISIBLE);
        imaArrayList.addAll((ArrayList<Image>) getIntent().getSerializableExtra("data"));
        if (imaArrayList.size() == 1)
            bottom.setVisibility(View.GONE);
        showImages();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (deleteOldFile) {
                if (requestCropingFile != null && requestCropingFile.exists()) {
                    requestCropingFile.delete();
                }
            }
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imaArrayList.get(currentImagePosition).setPath(getRealPathFromURI(result.getUri()));
            fullScreenImageAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(currentImagePosition);
            smallImageAdapter.notifyDataSetChanged();
            bottomRecylerView.scrollToPosition(currentImagePosition);
        }
        tv_done.setVisibility(View.VISIBLE);
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    @OnClick(R.id.iv_back)
    public void onBckClicked() {
        finish();
    }

    @OnClick(R.id.tv_done)
    public void onDoneClicked() {
        Intent intent = new Intent();
        intent.putExtra("data", imaArrayList);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        try {
            if (imaArrayList != null) {
                for (int i = 0; i < imaArrayList.size(); i++) {
                    File f = new File(imaArrayList.get(i).getPath());
                    if (f.exists())
                        f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @OnClick(R.id.iv_crop)
    public void onCropClicked() {
        CreateNewFragment.isForceCrop = false;
        requestCropingFile = new File(imaArrayList.get(currentImagePosition).getPath());
        if (cropType == MediaUtil.SQUARE_CROP) {
            CropImage.activity(Uri.fromFile(requestCropingFile))
                    .setAspectRatio(40, 40)
                    .setFixAspectRatio(true)
                    .start(this);
        } else if (cropType == MediaUtil.CUSTOM_CROP) {
            CropImage.activity(Uri.fromFile(requestCropingFile))
                    .start(this);
        } else if (cropType == MediaUtil.ART_CROP) {
            CropImage.activity(Uri.fromFile(requestCropingFile))
                    .setMinCropResultSize(1440, 540)
                    .setMaxCropResultSize(1440, 540)
                    .setMultiTouchEnabled(true)
                    .start(this);
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    public void showImages() {
        viewPager.setOnPageChangeListener(this);
        fullScreenImageAdapter = new FullScreenImageAdapter();
        viewPager.setAdapter(fullScreenImageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bottomRecylerView.setLayoutManager(linearLayoutManager);
        smallImageAdapter = new SmallImageAdapter();
        bottomRecylerView.setAdapter(smallImageAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentImagePosition = position;
        smallImageAdapter.notifyDataSetChanged();
        bottomRecylerView.scrollToPosition(currentImagePosition);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class FullScreenImageAdapter extends PagerAdapter {

        public FullScreenImageAdapter() {
        }

        @Override
        public int getCount() {
            return imaArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View viewLayout = ((LayoutInflater) PhotoEditActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.adapter_image_view, container, false);
            final ProgressBar progressBar = (ProgressBar) viewLayout.findViewById(R.id.progressBar);
            ImageView fullImageView = (ImageView) viewLayout.findViewById(R.id.fullImageView);
            showImage(imaArrayList.get(position).getPath(), fullImageView, progressBar);
            ((ViewPager) container).addView(viewLayout);
            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
    }

    public class SmallImageAdapter extends RecyclerView.Adapter<SmallImageAdapter.ViewHolder> {

        public SmallImageAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PhotoEditActivity.this).inflate(R.layout.adapter_small_imageview, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            showImage(imaArrayList.get(position).getPath(), holder.smallImageView, null);
            if (currentImagePosition == position) {
                holder.cardView.setCardBackgroundColor(AppThemeResource.getAppThemeResource(PhotoEditActivity.this).getNormalButtonBackgroundColor());
            } else {
                holder.cardView.setCardBackgroundColor(Color.WHITE);
            }
            holder.cardView.setTag(position);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = Integer.parseInt(v.getTag().toString());
                    viewPager.setCurrentItem(p);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imaArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView smallImageView;
            CardView cardView;

            public ViewHolder(View v) {
                super(v);
                smallImageView = (ImageView) v.findViewById(R.id.smallImageView);
                cardView = (CardView) v.findViewById(R.id.cardView);
            }
        }
    }

    private void showImage(String url, ImageView fullImageView, final ProgressBar progressBar) {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .fitCenter()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(fullImageView);
    }
}
