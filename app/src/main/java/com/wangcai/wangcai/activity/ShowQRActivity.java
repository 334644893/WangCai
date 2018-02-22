package com.wangcai.wangcai.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wangcai.wangcai.R;
import com.wangcai.wangcai.common.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowQRActivity extends BaseActivity {

    @Bind(R.id.simple_draweeView)
    SimpleDraweeView simpleDraweeView;
    public static String url;
    @Bind(R.id.fl_save_image)
    FrameLayout flSaveImage;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qr);
        ButterKnife.bind(this);
        context = this;
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            simpleDraweeView.setImageURI(uri);
        }
    }

    @OnClick({R.id.negativeButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.negativeButton:
                saveView();
                break;
        }
    }

    private Handler mHandler = new Handler();

    private void saveView() {

        // 获取图片某布局
        flSaveImage.setDrawingCacheEnabled(true);
        flSaveImage.buildDrawingCache();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 要在运行在子线程中
                final Bitmap bmp = flSaveImage.getDrawingCache(); // 获取图片

                savePicture(bmp, System.currentTimeMillis() + ".jpg");// 保存图片
                flSaveImage.destroyDrawingCache(); // 保存过后释放资源
            }
        }, 0);
    }

    public void savePicture(Bitmap bm, String fileName) {
        String imageurl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hecaigou";
        if (bm == null) {
            Toast.makeText(context, "savePicture null !", Toast.LENGTH_SHORT).show();
            return;
        }
        File foder = new File(imageurl);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(foder, fileName);
        try {
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(imageurl)) {
            Toast.makeText(context, "保存成功!图片路径:" + imageurl, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(myCaptureFile);
            intent.setData(uri);
            context.sendBroadcast(intent);

            context.sendBroadcast(
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/hecaigou/" + fileName)))
            );


        } else {
            Toast.makeText(context, "保存失败!", Toast.LENGTH_SHORT).show();

        }
        finish();

    }

}
