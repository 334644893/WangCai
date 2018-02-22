package com.wangcai.wangcai.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.wangcai.wangcai.R;
import com.wangcai.wangcai.bean.BankBean;
import com.wangcai.wangcai.common.BaseActivity;
import com.wangcai.wangcai.helper.GsonUtils;
import com.wangcai.wangcai.utils.LogUtil;
import com.wangcai.wangcai.utils.ToastUtil;
import com.wangcai.wangcai.widget.TextAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankListActivity extends BaseActivity {
    private static final String TAG = "BankListActivity";
    LinearLayoutManager linearLayoutManager;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private Context context;
    TextAdapter mAdapter;
    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        ButterKnife.bind(this);
        context = this;
        getbanks();
    }

    private void initRecyclerview() {
        /**
         * 设置列表
         */
        linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TextAdapter(context, mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new TextAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                BankCarActivity.bankname=mDatas.get(position);
                finish();
            }
        });
    }

    /**
     * 获取银行接口
     */

    private void getbanks() {
        showLoading("获取中...");
        Call<BankBean> call = getApi().getbanks();
        call.enqueue(new Callback<BankBean>() {
            @Override
            public void onResponse(Call<BankBean> call, Response<BankBean> response) {
                if (response.isSuccessful()) {
                    if (GsonUtils.getError_code(response.body()) == GsonUtils.SUCCESSFUL) {
                        mDatas.addAll(response.body().getData());
                        initRecyclerview();
                    } else {
                        ToastUtil.showMessage(GsonUtils.getErrmsg(response.body()));
                    }

                } else {
                    LogUtil.i("失败response.message():" + response.message());

                }
                closeLoading();//取消等待框
            }

            @Override
            public void onFailure(Call<BankBean> call, Throwable t) {
                LogUtil.e(TAG, t.toString());
                closeLoading();//取消等待框
                ToastUtil.showMessage("网络出现了问题");
            }

        });
    }
}
