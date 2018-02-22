package com.wangcai.wangcai;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wangcai.wangcai.activity.ForgotPasswordActivity;
import com.wangcai.wangcai.activity.RegisteredActivity;
import com.wangcai.wangcai.activity.WebMainActivity;
import com.wangcai.wangcai.common.BaseActivity;
import com.wangcai.wangcai.helper.GsonUtils;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.utils.SharedPreferencesUtils;
import com.wangcai.wangcai.utils.ToastUtil;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @Bind(R.id.tv_jzmm)
    TextView tvJzmm;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_verify)
    EditText etVerify;
    private int loginjzmm;
    Drawable drawable_41, drawable_40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AutomaticLogin();
        initJzmm();
        //动态添加窗口权限
        verifyStoragePermissions();

    }
    /**
     * 手动添加SD卡权限
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public void verifyStoragePermissions() {
        //动态添加窗口权限
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(MainActivity.this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 10);
//            }
//        }
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void AutomaticLogin() {

        if (!TextUtils.isEmpty(SharedPreferencesUtils.getParam(getApplicationContext(), "token", "").toString())) {
            // 跳转主页
            App.token = SharedPreferencesUtils.getParam(getApplicationContext(), "token", "").toString();
            App.phone = SharedPreferencesUtils.getParam(getApplicationContext(), "phone", "").toString();
            App.bankcard = SharedPreferencesUtils.getParam(getApplicationContext(), "bankcard", "").toString();
            startActivity(new Intent(MainActivity.this, WebMainActivity.class));
            finish();
        }
    }

    /**
     * 初始化记住密码
     * loginjzmm 0：未选中 1：选中
     */
    private void initJzmm() {
        loginjzmm = (int) SharedPreferencesUtils.getParam(getApplicationContext(), "loginjzmm", 0);
        //初始化账号密码
        etPhone.setText(SharedPreferencesUtils.getParam(getApplicationContext(), "phone", "") + "");
        if (loginjzmm == 1) {
            etVerify.setText(SharedPreferencesUtils.getParam(getApplicationContext(), "password", "") + "");
        } else {
            etVerify.setText("");
            SharedPreferencesUtils.setParam(getApplicationContext(), "password", "");
        }
        //初始化标记
        drawable_41 = getResources().getDrawable(R.mipmap.icon41);
        drawable_40 = getResources().getDrawable(R.mipmap.icon40);
        drawable_41.setBounds(0, 0, drawable_41.getMinimumWidth(), drawable_41.getMinimumHeight());
        drawable_40.setBounds(0, 0, drawable_40.getMinimumWidth(), drawable_40.getMinimumHeight());

        if (loginjzmm == 0) {
            tvJzmm.setCompoundDrawables(drawable_41, null, null, null);
        } else if (loginjzmm == 1) {
            tvJzmm.setCompoundDrawables(drawable_40, null, null, null);
        }

        etVerify.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etVerify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Login();
                return false;
            }
        });
    }

    @OnClick({R.id.tv_wjmm, R.id.btn_login, R.id.tv_zczh, R.id.tv_jzmm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //TODO 发送登录请求并跳转主页
//                if (MANDATORY) {
//                    version();
//                } else {
                if (TextUtils.isEmpty(etPhone.getText().toString()) || etPhone.getText().toString().length() != 11) {
                    ToastUtil.showMessage("手机号格式错误");
                } else if (TextUtils.isEmpty(etVerify.getText().toString())) {
                    ToastUtil.showMessage("密码不能为空");
                } else {
                    Login();

                }

//                }
                break;
            case R.id.tv_zczh:
                startActivity(new Intent(MainActivity.this, RegisteredActivity.class));

                break;
            case R.id.tv_wjmm:
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));

                break;
            case R.id.tv_jzmm:
                if (loginjzmm == 0) {
                    loginjzmm = 1;
                    SharedPreferencesUtils.setParam(getApplicationContext(), "loginjzmm", 1);
                    tvJzmm.setCompoundDrawables(drawable_40, null, null, null);
                } else if (loginjzmm == 1) {
                    loginjzmm = 0;
                    SharedPreferencesUtils.setParam(getApplicationContext(), "loginjzmm", 0);
                    tvJzmm.setCompoundDrawables(drawable_41, null, null, null);
                    SharedPreferencesUtils.setParam(getApplicationContext(), "password", "");
                }


                break;
        }
    }


    /**
     * 登录接口
     */

    private void Login() {
        showLoading("登录中...");
        Call<Object> call = getApi().login(etPhone.getText().toString(),
                etVerify.getText().toString());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        // do SomeThing
                        LogUtil.i("登陆成功");

                        JSONObject data = GsonUtils.getResultData(response.body());
                        App.phone = data.optString("phone");
                        App.token = data.optString("token");
                        App.bankcard = data.optString("bankcard");
                        App.bankcardimg = data.optString("bankcardimg");
                        App.idcardimg = data.optString("idcardimg");
                        LogUtil.d("-------登录返回App.token---------", App.token);
                        //记住手机号
                        SharedPreferencesUtils.setParam(getApplicationContext(), "phone", etPhone.getText().toString());
                        //刷新TOKEN用密码
                        SharedPreferencesUtils.setParam(getApplicationContext(), "token_password", etVerify.getText().toString());
                        //保存自动登录所需参数
                        SharedPreferencesUtils.setParam(getApplicationContext(), "token", data.optString("token"));
                        SharedPreferencesUtils.setParam(getApplicationContext(), "bankcard", data.optString("bankcard"));
                        SharedPreferencesUtils.setParam(getApplicationContext(), "bankcardimg", data.optString("bankcardimg"));
                        SharedPreferencesUtils.setParam(getApplicationContext(), "idcardimg", data.optString("idcardimg"));
                        //根据记住密码保存密码
                        if (loginjzmm == 1) {
                            SharedPreferencesUtils.setParam(getApplicationContext(), "password", etVerify.getText().toString());
                        } else {
                            SharedPreferencesUtils.setParam(getApplicationContext(), "password", "");
                        }
                        // 跳转主页
                        startActivity(new Intent(MainActivity.this, WebMainActivity.class));
                        closeLoading();//取消等待框
                        finish();
                    } else {
                        closeLoading();//取消等待框
                        ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                    }

                } else {
                    closeLoading();//取消等待框
                    LogUtil.i("登陆失败response.message():" + response.message());
                    ToastUtil.showMessage("登陆失败");
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
                closeLoading();//取消等待框
                ToastUtil.showMessage("登录超时");
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(MainActivity.this, "not granted", Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
