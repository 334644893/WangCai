package com.wangcai.wangcai;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.wangcai.wangcai.download.DeviceUtils;
import com.wangcai.wangcai.helper.APIService;
import com.wangcai.wangcai.helper.TokenAPIService;
import com.wangcai.wangcai.helper.TokenInterceptor;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.widget.NoticeDialog;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2017/1/10.
 */

public class App extends Application {
    private static final String TAG = "App";
    public static String VERSIONNAME = "";//
    public static String token = "";//用户标识，该token在其他用于获取用户信息的接口时必带
    public static String bankcard = "";//银行卡号
    public static String bankcardimg = "";//银行卡照片
    public static String idcardimg = "";//身份证照片
    public static String phone = "";//登录手机号
    public static int ver=60;//验证码时间
    private static App instance;
    private APIService serverApi;
    private TokenAPIService tokenapiservice;

    public APIService getServerApi() {
        return serverApi;
    }

    public TokenAPIService getTokenAPIService() {
        return tokenapiservice;
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        VERSIONNAME = DeviceUtils.getVersionName(this);
//        CrashHandler.getInstance().init(getApplicationContext());//异常捕获
        initFresco();//初始化图片加载
        initRest();//初始化网络通信
        LogUtil.isPrint = true;// 设置开启日志,发布时请关闭日志
//        LogUtil.isPrint = false;// 设置开启日志,发布时请关闭日志
        JPushInterface.setDebugMode(false);    // 设置开启极光日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
    }


    private void initFresco() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)//支持各种格式
                .build();
        Fresco.initialize(this, config);
    }

    private void initRest() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.SERVER_IP)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        serverApi = retrofit.create(APIService.class);
        //获取token时用到
        Retrofit retrofit_token = new Retrofit.Builder()
                .baseUrl(APIService.SERVER_IP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tokenapiservice = retrofit_token.create(TokenAPIService.class);
    }
    /**
     * 弹窗
     */
    private static NoticeDialog dialog1;
    public static void NoticeDialog(Context context, String amount) {
        if (dialog1 != null) {
            dialog1.dismiss();
        }
        NoticeDialog.Builder noticeBuilder = new
                NoticeDialog.Builder(context);
        noticeBuilder
                .setAmount(amount)
                .setPositiveButton(
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        dialog1 = noticeBuilder.create();
        dialog1.show();
    }

}
