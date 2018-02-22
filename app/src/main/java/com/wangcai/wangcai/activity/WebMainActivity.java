package com.wangcai.wangcai.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.wangcai.wangcai.App;
import com.wangcai.wangcai.MainActivity;
import com.wangcai.wangcai.R;
import com.wangcai.wangcai.common.BaseActivity;
import com.wangcai.wangcai.download.UpdateManager;
import com.wangcai.wangcai.helper.APIService;
import com.wangcai.wangcai.helper.GsonUtils;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.utils.SharedPreferencesUtils;
import com.wangcai.wangcai.utils.ToastUtil;
import com.wangcai.wangcai.widget.CustomDialog;
import com.wangcai.wangcai.widget.NoScrollViewPager;
import com.wangcai.wangcai.widget.ScrollWebView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.KeyEvent.KEYCODE_BACK;

public class WebMainActivity extends BaseActivity {
    private static final String TAG = "WebMainActivity";
    @Bind(R.id.view_pager)
    NoScrollViewPager viewpager;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.tv_left)
    ImageView tvLeft;
    @Bind(R.id.text_left)
    TextView textLeft;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    public static TextView tvNumber;
    public static FrameLayout flNumber;
    @Bind(R.id.demo_swiperefreshlayout)
    SwipeRefreshLayout demoSwiperefreshlayout;
    private int[] mItemImage = {R.mipmap.icon_shouye2, R.mipmap.icon_zoushitu,
            R.mipmap.icon_tongzhi_30, R.mipmap.icon_wode2};
    private int[] mItemCheckedImage = {R.mipmap.icon_shouye, R.mipmap.icon_zoushi2,
            R.mipmap.icon_tongzhi, R.mipmap.icon_wode2_81};
    private String[] mItemText = {"首页", "红黑走势", "通知公告", "我的账户"};
    public static String URL1, URL2, URL3, URL4;
    @Bind(R.id.tv_but_im_1)
    ImageView tvButIm1;
    @Bind(R.id.tv_but_tv_1)
    TextView tvButTv1;
    @Bind(R.id.tv_but_im_order)
    ImageView tvButImOrder;
    @Bind(R.id.tv_but_tv_order)
    TextView tvButTvOrder;
    @Bind(R.id.tv_but_im_2)
    ImageView tvButIm2;
    @Bind(R.id.tv_but_tv_2)
    TextView tvButTv2;
    @Bind(R.id.tv_but_im_3)
    ImageView tvButIm3;
    @Bind(R.id.tv_but_tv_3)
    TextView tvButTv3;
    private View view1, view2, view3, view4;
    private LinearLayout frameLayout_1, frameLayout_2, frameLayout_3, frameLayout_4;
    public static ScrollWebView webView1, webView2, webView3, webView4;
    private ImageView iverr1, iverr2, iverr3, iverr4;
    public static int noticenumber = 0;
    private List<View> viewList = new ArrayList<View>();
    private int pageflag = 0;
    private CustomDialog dialog, dialog1;
    private static final int REFRESH_BANK_CARD = 100;
    public static boolean refresh_bank_card_state = false;//绑定银行卡后是否刷新我的页面
    public static boolean MANDATORY = false;//是否是强制更新，如果是则登录不可用
    private boolean RTost = false;//更新弹出提示标识
    public static Dialog mUpdateLoading;
    private boolean errFlag1, errFlag2, errFlag3, errFlag4;
    private boolean webviewflag_1, webviewflag_2, webviewflag_3, webviewflag_4;//控制web1返回退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);
        ButterKnife.bind(this);
        //绑定极光推送别名
        JPushInterface.setAlias(App.getInstance(), App.phone, mAliasCallback);
        //更新
//        versionupdate();
        tvNumber = (TextView) findViewById(R.id.tv_number);
        flNumber = (FrameLayout) findViewById(R.id.fl_number);
        URL1 = APIService.SERVER_IP + "h5/index/index?token=" + App.token;//首页
        URL2 = APIService.SERVER_IP + "h5/pub/chart?token=" + App.token;//红黑
        URL3 = APIService.SERVER_IP + "h5/notice/index?token=" + App.token;//通知
        URL4 = APIService.SERVER_IP + "h5/my/index?token=" + App.token;//我的
        tvButTv1.setText(mItemText[0]);
        tvButTvOrder.setText(mItemText[1]);
        tvButTv2.setText(mItemText[2]);
        tvButTv3.setText(mItemText[3]);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pop_notice();
    }

    private void init() {

        initViewPager();
        viewpager.setCurrentItem(0);
        setBottom(0);
        if (!TextUtils.isEmpty(App.bankcard)) {
            tvRight.setText("修改银行卡");
        } else {
            tvRight.setText("绑定银行卡");
        }
        textLeft.setText("注销");
        tvLeft.setVisibility(View.GONE);
//        isBankCar();
        //刷新控件
        demoSwiperefreshlayout.setColorSchemeResources(R.color.white);
        demoSwiperefreshlayout.setProgressBackgroundColorSchemeResource(R.color.theme);
        demoSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉重置数据
                refreshWeb(pageflag);
                RTost = true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REFRESH_BANK_CARD && refresh_bank_card_state) {

            webView4.reload();
            refresh_bank_card_state = false;
        }
    }

    public static void refreshWeb(int i) {
        switch (i) {
            case 0:
                if (webView1 != null) {
                    webView1.reload();
                }
                break;
            case 1:
                if (webView2 != null) {
                    webView2.reload();
                }
                break;
            case 2:
                if (webView3 != null) {
                    webView3.reload();
                }
                break;
            case 3:
                if (webView4 != null) {
                    webView4.reload();
                }

                break;
        }
    }

    /**
     * 绑定银行卡
     */
    private void isBankCar() {
        if (TextUtils.isEmpty(App.bankcard)) {
            CustomDialog.Builder customBuilder = new
                    CustomDialog.Builder(WebMainActivity.this);
            customBuilder
                    .setTitle("绑定银行卡")
                    .setMessage("您还未绑定银行卡\n是否现在去绑定银行卡?")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(WebMainActivity.this, BankCarActivity.class));
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
            dialog = customBuilder.create();
            dialog.show();
        }

    }

    @OnClick({R.id.tv_but_1, R.id.tv_but_order, R.id.tv_but_2, R.id.tv_but_3, R.id.tv_right, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                CustomDialog.Builder customBuilder = new
                        CustomDialog.Builder(WebMainActivity.this);
                customBuilder
                        .setTitle("注销")
                        .setMessage("您确认返回登录页吗?")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //清除TOKEN
                                cancellation();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                dialog = customBuilder.create();
                dialog.show();

                break;
            case R.id.tv_right:
                Intent intent = new Intent(WebMainActivity.this, BankCarActivity.class);
                startActivityForResult(intent, REFRESH_BANK_CARD);
                break;
            case R.id.tv_but_1:
                viewpager.setCurrentItem(0);
                showLoading("加载中...");
                webView1.loadUrl(URL1);
                break;
            case R.id.tv_but_order:
                viewpager.setCurrentItem(1);
                showLoading("加载中...");
                webView2.loadUrl(URL2);
                break;
            case R.id.tv_but_2:
                viewpager.setCurrentItem(2);
                showLoading("加载中...");
                webView3.loadUrl(URL3);
                break;
            case R.id.tv_but_3:
                viewpager.setCurrentItem(3);
                showLoading("加载中...");
                webView4.loadUrl(URL4);
                break;
        }
    }


    /**
     * 改变底部导航
     *
     * @param i
     */
    private void setBottom(int i) {
        tvTitle.setText(mItemText[i]);
        switch (i) {
            case 0:

                tvButIm1.setImageResource(mItemCheckedImage[0]);
                tvButImOrder.setImageResource(mItemImage[1]);
                tvButIm2.setImageResource(mItemImage[2]);
                tvButIm3.setImageResource(mItemImage[3]);
                tvButTv1.setTextColor(getResources().getColor(R.color.theme));
                tvButTvOrder.setTextColor(getResources().getColor(R.color.black_54));
                tvButTv2.setTextColor(getResources().getColor(R.color.black_54));
                tvButTv3.setTextColor(getResources().getColor(R.color.black_54));
                break;
            case 1:
                tvButIm1.setImageResource(mItemImage[0]);
                tvButImOrder.setImageResource(mItemCheckedImage[1]);
                tvButIm2.setImageResource(mItemImage[2]);
                tvButIm3.setImageResource(mItemImage[3]);
                tvButTv1.setTextColor(getResources().getColor(R.color.black_54));
                tvButTvOrder.setTextColor(getResources().getColor(R.color.theme));
                tvButTv2.setTextColor(getResources().getColor(R.color.black_54));
                tvButTv3.setTextColor(getResources().getColor(R.color.black_54));
                break;
            case 2:
                tvButIm1.setImageResource(mItemImage[0]);
                tvButImOrder.setImageResource(mItemImage[1]);
                tvButIm2.setImageResource(mItemCheckedImage[2]);
                tvButIm3.setImageResource(mItemImage[3]);
                tvButTv1.setTextColor(getResources().getColor(R.color.black_54));
                tvButTvOrder.setTextColor(getResources().getColor(R.color.black_54));
                tvButTv2.setTextColor(getResources().getColor(R.color.theme));
                tvButTv3.setTextColor(getResources().getColor(R.color.black_54));
                break;
            case 3:
                tvButIm1.setImageResource(mItemImage[0]);
                tvButImOrder.setImageResource(mItemImage[1]);
                tvButIm2.setImageResource(mItemImage[2]);
                tvButIm3.setImageResource(mItemCheckedImage[3]);
                tvButTv1.setTextColor(getResources().getColor(R.color.black_54));
                tvButTvOrder.setTextColor(getResources().getColor(R.color.black_54));
                tvButTv2.setTextColor(getResources().getColor(R.color.black_54));
                tvButTv3.setTextColor(getResources().getColor(R.color.theme));

                break;
        }


    }

    private ProgressBar pbProgress_1, pbProgress_2, pbProgress_3, pbProgress_4;

    public void initViewPager() {
        LayoutInflater lf = getLayoutInflater().from(this);
        view1 = lf.inflate(R.layout.item_web_1, null);
        view2 = lf.inflate(R.layout.item_web_1, null);
        view3 = lf.inflate(R.layout.item_web_1, null);
        view4 = lf.inflate(R.layout.item_web_1, null);
        iverr1 = (ImageView) view1.findViewById(R.id.iv_err);
        iverr2 = (ImageView) view2.findViewById(R.id.iv_err);
        iverr3 = (ImageView) view3.findViewById(R.id.iv_err);
        iverr4 = (ImageView) view4.findViewById(R.id.iv_err);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        viewpager.setAdapter(pagerAdapter);
        viewpager.setCurrentItem(0);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageflag = position;
                setBottom(position);
                if (position == 3) {
//                    if (TextUtils.isEmpty(App.bankcard)) {
                    tvRight.setVisibility(View.VISIBLE);
//                    }
                    textLeft.setVisibility(View.VISIBLE);
                    llBack.setVisibility(View.VISIBLE);


                } else {
                    tvRight.setVisibility(View.INVISIBLE);
                    textLeft.setVisibility(View.INVISIBLE);
                    llBack.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        frameLayout_1 = (LinearLayout) view1.findViewById(R.id.fl_sup);
        frameLayout_2 = (LinearLayout) view2.findViewById(R.id.fl_sup);
        frameLayout_3 = (LinearLayout) view3.findViewById(R.id.fl_sup);
        frameLayout_4 = (LinearLayout) view4.findViewById(R.id.fl_sup);
        pbProgress_1 = (ProgressBar) view1.findViewById(R.id.pb_progress);
        pbProgress_2 = (ProgressBar) view2.findViewById(R.id.pb_progress);
        pbProgress_3 = (ProgressBar) view3.findViewById(R.id.pb_progress);
        pbProgress_4 = (ProgressBar) view4.findViewById(R.id.pb_progress);
        //init view1
        webView1 = new ScrollWebView(getApplicationContext());
        frameLayout_1.addView(webView1);
        webView1.setVerticalScrollBarEnabled(false);
        webView1.setHorizontalScrollBarEnabled(false);
        webView1.getSettings().setJavaScriptEnabled(true); //加上这句话才能使用JavaScript方法
        webView1.getSettings().setUseWideViewPort(true);
        webView1.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
                demoSwiperefreshlayout.setEnabled(false);
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                //滑动到顶部
                demoSwiperefreshlayout.setEnabled(true);
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
            }
        });
        webView1.addJavascriptInterface(new JavascriptHandler(), "handler");
        webView1.addJavascriptInterface(new TokenJavascriptHandler(), "app");
        showLoading("加载中...");
        //WebView加载web资源
        webView1.loadUrl(URL1);
        LogUtil.d(URL1);


        webView1.setWebChromeClient(new WebChromeClient() {//监听网页加载
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // 加载中
                pbProgress_1.setVisibility(View.VISIBLE);
                pbProgress_1.setProgress(newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    pbProgress_1.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                LogUtil.d("-----访问网址111-----", url);

                if (url.contains("?")) {
                    url = url + "&token=" + App.token;
                } else {
                    url = url + "?&token=" + App.token;
                }
                LogUtil.d("-----访问网址222-----", url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.handler.getContent(document.body.innerHTML);");
                demoSwiperefreshlayout.setRefreshing(false);
                closeLoading();//取消等待框
                if (RTost) {
                    RTost = false;
                    ToastUtil.showMessage("已刷新");
                }
                if (errFlag1) {
                    webView1.setVisibility(View.GONE);
                    errFlag1 = false;
                } else {
                    webView1.setVisibility(View.VISIBLE);
                }
                if (URL1.equals(url)) {
                    //当页面加载到主页时改变返回状态
                    webviewflag_1 = false;
                } else {
                    webviewflag_1 = true;
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtil.showMessage("网络出错了");
                errFlag1 = true;
                demoSwiperefreshlayout.setRefreshing(false);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        //init view2
        webView2 = new ScrollWebView(getApplicationContext());
        frameLayout_2.addView(webView2);

        webView2.setVerticalScrollBarEnabled(false);
        webView2.setHorizontalScrollBarEnabled(false);
        webView2.getSettings().setJavaScriptEnabled(true); //加上这句话才能使用JavaScript方法
        webView2.getSettings().setUseWideViewPort(true);
        webView2.addJavascriptInterface(new JavascriptHandler(), "handler");
        webView2.addJavascriptInterface(new TokenJavascriptHandler(), "app");
        webView2.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
                demoSwiperefreshlayout.setEnabled(false);
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                //滑动到顶部
                demoSwiperefreshlayout.setEnabled(true);
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
            }
        });
        //WebView加载web资源
        webView2.loadUrl(URL2);
        webView2.setWebChromeClient(new WebChromeClient() {//监听网页加载
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pbProgress_2.setVisibility(View.VISIBLE);
                pbProgress_2.setProgress(newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    pbProgress_2.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                LogUtil.d("-----访问网址111-----", url);

                if (url.contains("?")) {
                    url = url + "&token=" + App.token;
                } else {
                    url = url + "?&token=" + App.token;
                }
                LogUtil.d("-----访问网址222-----", url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.handler.getContent(document.body.innerHTML);");
                demoSwiperefreshlayout.setRefreshing(false);
                closeLoading();//取消等待框
                if (RTost) {
                    RTost = false;
                    ToastUtil.showMessage("已刷新");
                }
                if (errFlag2) {
                    webView2.setVisibility(View.GONE);
                    errFlag2 = false;
                } else {
                    webView2.setVisibility(View.VISIBLE);
                }
                if (URL2.equals(url)) {
                    //当页面加载到主页时改变返回状态
                    webviewflag_2 = false;
                } else {
                    webviewflag_2 = true;
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtil.showMessage("网络出错了");
                errFlag2 = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        //init view3
        frameLayout_3 = (LinearLayout) view3.findViewById(R.id.fl_sup);
        webView3 = new ScrollWebView(getApplicationContext());
        frameLayout_3.addView(webView3);
        webView3.setVerticalScrollBarEnabled(false);
        webView3.setHorizontalScrollBarEnabled(false);
        webView3.getSettings().setJavaScriptEnabled(true); //加上这句话才能使用JavaScript方法
        webView3.getSettings().setUseWideViewPort(true);
        webView3.addJavascriptInterface(new JavascriptHandler(), "handler");
        webView3.addJavascriptInterface(new TokenJavascriptHandler(), "app");
        webView3.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
                demoSwiperefreshlayout.setEnabled(false);
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                //滑动到顶部
                demoSwiperefreshlayout.setEnabled(true);
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
            }
        });
        //WebView加载web资源
        webView3.loadUrl(URL3);
        webView3.setWebChromeClient(new WebChromeClient() {//监听网页加载
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pbProgress_3.setVisibility(View.VISIBLE);
                pbProgress_3.setProgress(newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    pbProgress_3.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView3.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                LogUtil.d("-----访问网址111-----", url);

                if (url.contains("?")) {
                    url = url + "&token=" + App.token;
                } else {
                    url = url + "?&token=" + App.token;
                }
                LogUtil.d("-----访问网址222-----", url);
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.handler.getContent(document.body.innerHTML);");
                demoSwiperefreshlayout.setRefreshing(false);
                closeLoading();//取消等待框
                if (RTost) {
                    RTost = false;
                    ToastUtil.showMessage("已刷新");
                }
                if (errFlag3) {
                    webView3.setVisibility(View.GONE);
                    errFlag3 = false;
                } else {
                    webView3.setVisibility(View.VISIBLE);
                }
                if (URL3.equals(url)) {
                    //当页面加载到主页时改变返回状态
                    webviewflag_3 = false;
                } else {
                    webviewflag_3 = true;
                }

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtil.showMessage("网络出错了");
                errFlag3 = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        //init view4
        frameLayout_4 = (LinearLayout) view4.findViewById(R.id.fl_sup);
        webView4 = new ScrollWebView(getApplicationContext());
        frameLayout_4.addView(webView4);
        webView4.setVerticalScrollBarEnabled(false);
        webView4.setHorizontalScrollBarEnabled(false);
        webView4.getSettings().setJavaScriptEnabled(true); //加上这句话才能使用JavaScript方法
        webView4.getSettings().setUseWideViewPort(true);
        webView4.addJavascriptInterface(new JavascriptHandler(), "handler");
        webView4.addJavascriptInterface(new TokenJavascriptHandler(), "app");
        webView4.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                //滑动中
                demoSwiperefreshlayout.setEnabled(false);
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                //滑动到顶部
                demoSwiperefreshlayout.setEnabled(true);
            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                //滑动到底部
            }
        });
        //WebView加载web资源
        webView4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;
                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {

                }

                // 这里可以拦截很多类型，我们只处理图片类型就可以了
                switch (type) {
//                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
//                        break;
//                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
//                        break;
//                    case WebView.HitTestResult.GEO_TYPE: //
//                        break;
//                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
//                        break;
//                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
//                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        // 获取图片的路径
                        ShowQRActivity.url = result.getExtra();
                        LogUtil.d("路径------------", ShowQRActivity.url);
                        startActivity(new Intent(WebMainActivity.this, ShowQRActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        webView4.loadUrl(URL4);
        LogUtil.d(URL4);
        webView4.setWebChromeClient(new WebChromeClient() {//监听网页加载
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pbProgress_4.setVisibility(View.VISIBLE);
                pbProgress_4.setProgress(newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    pbProgress_4.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView4.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                LogUtil.d("-----访问网址111-----", url);

//                if (url.contains("?")) {
//                        url = url + "&token=" + App.token;
//                    } else {
//                        url = url + "?&token=" + App.token;
//                    }
//                LogUtil.d("-----访问网址222-----", url);
//                view.loadUrl(url);
//                return true;
                if (url == null) return false;
                if (!url.startsWith(APIService.SERVER_IP)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                        return false;
                    }
                } else {
                    if (url.contains("?")) {
                        url = url + "&token=" + App.token;
                    } else {
                        url = url + "?&token=" + App.token;
                    }
                    LogUtil.d("-----访问网址222-----", url);
                    view.loadUrl(url);
                    return false;
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.handler.getContent(document.body.innerHTML);");
                demoSwiperefreshlayout.setRefreshing(false);
                closeLoading();//取消等待框
                if (RTost) {
                    RTost = false;
                    ToastUtil.showMessage("已刷新");
                }
                if (errFlag4) {
                    webView4.setVisibility(View.GONE);
                    errFlag4 = false;
                } else {
                    webView4.setVisibility(View.VISIBLE);
                }
                if (URL4.equals(url)) {
                    //当页面加载到主页时改变返回状态
                    webviewflag_4 = false;
                } else {
                    webviewflag_4 = true;
                }
                pushsum();
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtil.showMessage("网络出错了");
                errFlag4 = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

    }

    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    };
    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (pageflag) {
            case 0:
                if ((keyCode == KEYCODE_BACK) && webView1.canGoBack() && webviewflag_1) {
                    webView1.loadUrl(URL1);
                    return true;
                }
                break;
            case 1:
                if ((keyCode == KEYCODE_BACK) && webView2.canGoBack() && webviewflag_2) {
                    webView2.goBack();
                    return true;
                }
                break;
            case 2:
                if ((keyCode == KEYCODE_BACK) && webView3.canGoBack() && webviewflag_3) {
                    webView3.goBack();
                    return true;
                }
                break;
            case 3:
                if ((keyCode == KEYCODE_BACK) && webView4.canGoBack() && webviewflag_4) {
                    webView4.goBack();
                    return true;
                }
                break;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "--------取消别名------" + alias;
                    LogUtil.i(TAG, logs);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    LogUtil.e(TAG, logs);
            }

        }

    };

    /**
     * 改变通知数
     *
     * @param i
     */
    public static void setNoticenumber(int i) {
        noticenumber = i;
        if (noticenumber == 0) {
            flNumber.setVisibility(View.GONE);
        } else {
            flNumber.setVisibility(View.VISIBLE);
            tvNumber.setText(noticenumber + "");
        }

    }

    /**
     * TOKEN过期时重新获取
     */
    class JavascriptHandler {
        @JavascriptInterface
        public void getContent(String htmlContent) {
            if (htmlContent.length() < 100) {
                //数据小于100时TOKEN过期后回传JSON字符串小于100
                LogUtil.d("error_code===============", "html content error_code:" + GsonUtils.getStringV(htmlContent, "error_code"));
                if ("300".equals(GsonUtils.getStringV(htmlContent, "error_code"))) {
                    LogUtil.d("TOKEN过期重新获取===============", "TOKEN过期重新获取");
                    TokenLogin();

                } else if ("1".equals(GsonUtils.getStringV(htmlContent, "error_code"))) {
                    LogUtil.d("TOKEN错误返回登录===============", "TOKEN错误返回登录");
                    ToastUtil.showMessage("登录信息错误，请重新登录");
                    cancellation();
                }
            } else {

                LogUtil.d("H===============", "访问成功");
            }
        }

    }

    /**
     * 提现时传给页面TOKEN
     */
    class TokenJavascriptHandler {
        @JavascriptInterface
        public String getToken() {
            return App.token;
        }
    }

    /**
     * 注销
     */
    private void cancellation() {
        SharedPreferencesUtils.remove(WebMainActivity.this, "token");
        JPushInterface.setAlias(App.getInstance(), "0", mAliasCallback);
        startActivity(new Intent(WebMainActivity.this, MainActivity.class));
        finish();
    }

    /**
     * 更新
     */
    int type = -1;

    private void versionupdate() {
        getApi().versionupdate().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        // do SomeThing
                        LogUtil.i("获取更新信息成功");
                        //TODO 初始化数据
                        JSONObject data = GsonUtils.getResultData(response.body());
                        LogUtil.d("---data--", data.toString());
                        int version = data.optInt("androidVersion");//版本标识
                        String prompt = data.optString("prompt");
                        type = data.optInt("type");
                        String url = data.optString("android");
                        LogUtil.d("---------", "-version:" + version + "-prompt:" + prompt + "-type:" + type + "-android:" + url);
                        //信息对比是否更新
                        new UpdateManager(WebMainActivity.this).checkUpdate(version, prompt, type, url, false);
                    }

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                errFlag1 = true;
                // 构造对话框
                CustomDialog.Builder customBuilder = new
                        CustomDialog.Builder(WebMainActivity.this);
                customBuilder
                        .setTitle("检查更新提示")
                        .setMessage("网络出现了问题")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                System.exit(0);
                            }
                        })
                ;
                dialog1 = customBuilder.create();
                dialog1.setCancelable(false);
                dialog1.show();
            }

        });
    }

    /**
     * 登录接口
     */

    private void TokenLogin() {
        showLoading("刷新中...");
        Call<Object> call = getApi().login(SharedPreferencesUtils.getParam(App.getInstance(), "phone", "") + "",
                SharedPreferencesUtils.getParam(App.getInstance(), "token_password", "") + "");
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        // do SomeThing
                        LogUtil.i("登陆成功");
                        //TODO 初始化数据
                        JSONObject data = GsonUtils.getResultData(response.body());
                        App.phone = data.optString("phone");
                        App.token = data.optString("token");
                        App.bankcard = data.optString("bankcard");
                        //记住TOKEN用于自动登录
                        SharedPreferencesUtils.setParam(getApplicationContext(), "token", data.optString("token"));
                        LogUtil.d("-------登录返回App.token---------", App.token);
                        URL1 = APIService.SERVER_IP + "h5/index/index?token=" + App.token;//首页
                        URL2 = APIService.SERVER_IP + "h5/pub/chart?token=" + App.token;//红黑
                        URL3 = APIService.SERVER_IP + "h5/notice/index?token=" + App.token;//通知
                        URL4 = APIService.SERVER_IP + "h5/my/index?token=" + App.token;//我的
                        LogUtil.d("-------URL1---------", URL1);
                        webView1.loadUrl(URL1);
                        webView2.loadUrl(URL2);
                        webView3.loadUrl(URL3);
                        webView4.loadUrl(URL4);
                    } else {
                        ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                    }

                } else {
                    LogUtil.i("网络出现了问题" + response.message());
                    ToastUtil.showMessage("失败");
                }
                closeLoading();//取消等待框
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
                closeLoading();//取消等待框
            }

        });
    }

    /**
     * 弹窗
     */

    private void pop_notice() {
        Call<Object> call = getApi().pop_notice(App.token

        );
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        //获取公告
                        App.NoticeDialog(WebMainActivity.this, GsonUtils.getMsg(response.body()));
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
                ToastUtil.showMessage("网络出现了问题");
            }

        });
    }

    protected void onDestroy() {
        super.onDestroy();
        webView1.removeAllViews();
        webView2.removeAllViews();
        webView3.removeAllViews();
        webView4.removeAllViews();
        webView1.destroy();
        webView2.destroy();
        webView3.destroy();
        webView4.destroy();
    }
}

