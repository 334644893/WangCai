package com.wangcai.wangcai.common;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.wangcai.wangcai.App;
import com.wangcai.wangcai.activity.WebMainActivity;
import com.wangcai.wangcai.helper.APIService;
import com.wangcai.wangcai.helper.GsonUtils;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.utils.ToastUtil;
import com.wangcai.wangcai.widget.LoadingDialogUtils;

import org.json.JSONObject;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/10.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private Dialog mLoading;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ButterKnife.bind(this);

    }

    public APIService getApi() {
        return App.getInstance().getServerApi();
    }

    public void showLoading(String text) {
//        mLoading = LoadingDialogUtils.createLoadingDialog(BaseActivity.this, text
//
//        );//添加等待框
    }


    public void closeLoading() {
//        if (mLoading.isShowing()) {
//            mLoading.dismiss();
//        }

    }

    /**
     * 短信接口
     */
    public void send(final String TAG, String phone) {
        LogUtil.i("/TAG:" + TAG + "/phone:" + phone);
        Call<Object> call = getApi().send(phone, getIMEIorIMSI());

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (!response.isSuccessful()) {
                    ToastUtil.showMessage("验证码失败:" + GsonUtils.getErrmsg(response.body()));
                } else {
                    ToastUtil.showMessage("发送成功请稍等...");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
            }

        });
    }

    /**
     * 获取手机唯一标示
     */
    public static String getIMEIorIMSI() {
        if (!TextUtils.isEmpty(Build.SERIAL)) {
            if (Build.SERIAL.length() < 15) {
                return Build.SERIAL;
            } else {
                return Build.SERIAL.substring(0, 15);
            }

        } else {
            return "";
        }
    }

    /**
     * 获取通知数
     */
    public void pushsum() {
        Call<Object> call = getApi().pushsum(App.token);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        JSONObject data = GsonUtils.getResultData(response.body());
                        WebMainActivity.setNoticenumber(data.optInt("pushsum"));
                    } else {
                        ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                    }

                } else {
                    LogUtil.i("失败response.message():" + response.message());

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
                closeLoading();//取消等待框
                ToastUtil.showMessage("超时");
            }

        });
    }
}
