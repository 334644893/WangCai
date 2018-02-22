package com.wangcai.wangcai.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wangcai.wangcai.App;
import com.wangcai.wangcai.R;
import com.wangcai.wangcai.common.BaseActivity;
import com.wangcai.wangcai.helper.APIService;
import com.wangcai.wangcai.helper.GsonUtils;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.utils.PictureUtil;
import com.wangcai.wangcai.utils.SharedPreferencesUtils;
import com.wangcai.wangcai.utils.ToastUtil;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankCarActivity extends BaseActivity {
    private static final String TAG = "BankCarActivity";
    private static final int MY_SCAN_REQUEST_CODE = 111;
    private static final int TOBANKLISTACTIVITY = 888;
    public static String bankname = "";
    @Bind(R.id.tv_carnumber)
    EditText tvCarnumber;
    @Bind(R.id.tv_name)
    EditText tvName;
    @Bind(R.id.tv_bank)
    TextView tvBank;
    @Bind(R.id.btn_get_verify)
    TextView btnGetVerify;
    @Bind(R.id.et_f_p_v)
    EditText etFPV;
    @Bind(R.id.tv_card)
    EditText tvCard;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.user_avator_bank)
    SimpleDraweeView userAvatorBank;
    @Bind(R.id.ll_xx_bank)
    LinearLayout llXxBank;
    @Bind(R.id.user_avator_bank_bot)
    SimpleDraweeView userAvatorBank_bot;
    @Bind(R.id.ll_xx_bank_bot)
    LinearLayout llXxBank_bot;
    private int time;
    private Timer timer;
    public int ACTIVITY_REQUEST_SELECT_PHOTO = 1000;
    boolean onClick_bank = true;//身份证点击标识
    String url_ph_bank = "";//显示身份证地址
    String idcardimg = "";//上传身份证地址
    String idcard = "";//身份证号
    boolean onClick_bank_bot = true;//银行卡点击标识
    String url_ph_bank_bot = "";//显示银行卡地址
    String bankcardimg = "";//上传银行卡地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_car);
        ButterKnife.bind(this);
        tvTitle.setText("绑定银行卡");
        if (!TextUtils.isEmpty(App.idcardimg)) {
            idcardimg = App.idcardimg;
            userAvatorBank.setImageURI(APIService.SERVER_IP + App.idcardimg);
            onClick_bank = false;
            llXxBank.setVisibility(View.VISIBLE);
        }
        else {
            userAvatorBank.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.icon_photo3)).build());
        }
        if (!TextUtils.isEmpty(App.bankcardimg)) {
            bankcardimg = App.bankcardimg;
            userAvatorBank_bot.setImageURI(APIService.SERVER_IP + App.bankcardimg);
            onClick_bank_bot = false;
            llXxBank_bot.setVisibility(View.VISIBLE);
        }
        else {
            userAvatorBank_bot.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.icon_yhk)).build());
        }
    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // default: false
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                LogUtil.d("---------cardNumber", scanResult.cardNumber);
                tvCarnumber.setText(scanResult.cardNumber);
                LogUtil.d("---------cardholderName", scanResult.cardholderName);
                tvName.setText(scanResult.cardholderName);

            } else {
                ToastUtil.showMessage("获取银行卡信息失败");
            }
        } else if (requestCode == TOBANKLISTACTIVITY) {
            tvBank.setText(bankname);
        }

    }

    @OnClick({R.id.tv_carm, R.id.binding, R.id.ll_bank, R.id.btn_get_verify, R.id.tv_left, R.id.user_avator_bank, R.id.ll_xx_bank, R.id.user_avator_bank_bot, R.id.ll_xx_bank_bot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.ll_bank:
                startActivityForResult(new Intent(BankCarActivity.this, BankListActivity.class), TOBANKLISTACTIVITY);
                break;
            case R.id.tv_carm:
                onScanPress();
                break;
            case R.id.binding:
                if (TextUtils.isEmpty(tvCarnumber.getText().toString())) {
                    ToastUtil.showMessage("请填写银行卡号");
                } else if (TextUtils.isEmpty(etFPV.getText().toString())) {
                    ToastUtil.showMessage("请填写验证码");
                } else if (TextUtils.isEmpty(tvName.getText().toString())) {
                    ToastUtil.showMessage("请填写持卡人");
                } else if (TextUtils.isEmpty(tvBank.getText().toString())) {
                    ToastUtil.showMessage("请选择开户行");
                } else if (TextUtils.isEmpty(tvCard.getText().toString())) {
                    ToastUtil.showMessage("请上传身份证正面照");
                } else if (TextUtils.isEmpty(bankcardimg)) {
                    ToastUtil.showMessage("请上传银行卡照片");
                } else {
                    bindcard(
                            tvCarnumber.getText().toString(),
                            etFPV.getText().toString(),
                            tvName.getText().toString(),
                            tvBank.getText().toString(), idcard);
                }
                break;
            case R.id.btn_get_verify:
                startTimer(App.ver);
                send(TAG, App.phone);
                break;
            case R.id.user_avator_bank:
                //身份证

                if (onClick_bank) {
                    Album.image(this) // 选择图片。
                            .multipleChoice()
                            .requestCode(ACTIVITY_REQUEST_SELECT_PHOTO)
                            .camera(true)
                            .columnCount(2)
                            .selectCount(1)
                            .onResult(new Action<ArrayList<AlbumFile>>() {
                                @Override
                                public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                                    // 拿到用户选择的图片路径List：
                                    url_ph_bank = result.get(0).getPath();
                                    uploadMemberIcon(PictureUtil.smallPic(result.get(0).getPath()), 0);
                                }
                            })
                            .onCancel(new Action<String>() {
                                @Override
                                public void onAction(int requestCode, @NonNull String result) {
                                    // 根据需要提示用户取消了选择。
                                    ToastUtil.showMessage("无法获取图片");
                                }
                            })
                            .start();
                }
                break;
            case R.id.ll_xx_bank:
                userAvatorBank.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.icon_photo3)).build());
                onClick_bank = true;
                llXxBank.setVisibility(View.INVISIBLE);
                break;
            case R.id.user_avator_bank_bot:
                //银行卡
                if (onClick_bank_bot) {
                    Album.image(this) // 选择图片。
                            .multipleChoice()
                            .requestCode(ACTIVITY_REQUEST_SELECT_PHOTO)
                            .camera(true)
                            .columnCount(2)
                            .selectCount(1)
                            .onResult(new Action<ArrayList<AlbumFile>>() {
                                @Override
                                public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                                    // 拿到用户选择的图片路径List：
                                    url_ph_bank_bot = result.get(0).getPath();
                                    uploadMemberIcon(PictureUtil.smallPic(result.get(0).getPath()), 1);
                                }
                            })
                            .onCancel(new Action<String>() {
                                @Override
                                public void onAction(int requestCode, @NonNull String result) {
                                    // 根据需要提示用户取消了选择。
                                    ToastUtil.showMessage("无法获取图片");
                                }
                            })
                            .start();
                }
                break;
            case R.id.ll_xx_bank_bot:
                userAvatorBank_bot.setImageURI((new Uri.Builder()).scheme("res").path(String.valueOf(R.mipmap.icon_yhk)).build());
                onClick_bank_bot = true;
                llXxBank_bot.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 绑定银行卡接口
     */

    private void bindcard(String card, String code,
                          String truename, String bank, String idcard) {
        showLoading("绑定中...");
        Call<Object> call = getApi().bindcard(App.token, code,
                card, truename, bank, tvCard.getText().toString(), bankcardimg, idcardimg

        );
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        // do SomeThing
                        LogUtil.i("绑定成功");
                        ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                        WebMainActivity.refresh_bank_card_state = true;
                        App.bankcard = tvName.getText().toString();
                        App.bankcardimg = bankcardimg;
                        App.idcardimg = idcardimg;
                        SharedPreferencesUtils.setParam(getApplicationContext(), "bankcard", App.bankcard);
                        SharedPreferencesUtils.setParam(getApplicationContext(), "bankcardimg", App.bankcardimg);
                        SharedPreferencesUtils.setParam(getApplicationContext(), "idcardimg", App.idcardimg);
                        finish();
                    } else {
                        ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                    }

                } else {
                    LogUtil.i("失败response.message():" + response.message());

                }
                closeLoading();//取消等待框
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
                closeLoading();//取消等待框
                ToastUtil.showMessage("网络出现了问题");
            }

        });
    }

    private void startTimer(final int t) {
        time = t;
        btnGetVerify.setEnabled(false);
        btnGetVerify.setBackgroundColor(getResources().getColor(R.color.black_26));
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (btnGetVerify != null) {
                            btnGetVerify.setText("请稍等(" + String.valueOf(time--) + ")");
                        }
                        if (time < 0) {
                            time = t;
                            btnGetVerify.setText("获取验证码");
                            btnGetVerify.setEnabled(true);
                            btnGetVerify.setBackgroundColor(getResources().getColor(R.color.theme));
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * 上传图片
     */
    RequestBody tokenBody;
    RequestBody check_id_card;
    Call<Object> call;

    private void uploadMemberIcon(String filePath, final int flag) {
        if (TextUtils.isEmpty(filePath)) {
            ToastUtil.showMessage("图片不见了");
            return;
        }
        showLoading("上传中...");
        File file = new File(filePath);
        if (flag == 0) {
            tokenBody = RequestBody.create(MediaType.parse("text/plain"), App.token);
            check_id_card = RequestBody.create(MediaType.parse("text/plain"), "1");
        }
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imageBodyPart = MultipartBody.Part.createFormData("file", file.getName(), imageBody);
        if (flag == 0) {
            call = getApi().uploadMemberIcon(imageBodyPart, tokenBody, check_id_card);
        } else {
            call = getApi().uploadMemberIcon(imageBodyPart);
        }


        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                    // do SomeThing
                    LogUtil.i("上传成功");
                    JSONObject data = GsonUtils.getResultData(response.body());
                    switch (flag) {
                        case 0:
                            idcard = data.optString("idcard");
                            idcardimg = data.optString("url");
                            tvCard.setText(idcard);
                            userAvatorBank.setImageURI(Uri.fromFile(new File(url_ph_bank)));
                            onClick_bank = false;
                            llXxBank.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            bankcardimg = data.optString("url");
                            userAvatorBank_bot.setImageURI(Uri.fromFile(new File(url_ph_bank_bot)));
                            onClick_bank_bot = false;
                            llXxBank_bot.setVisibility(View.VISIBLE);
                            break;
                    }

                    closeLoading();//取消等待框
                } else {
                    ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                    closeLoading();//取消等待框
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
                closeLoading();//取消等待框
                ToastUtil.showMessage("图片上传失败");
            }
        });
    }
}
