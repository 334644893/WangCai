package com.wangcai.wangcai.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangcai.wangcai.R;


public class LoadingDialogUtils {

    public static Dialog createLoadingDialog(Context context, String msg
    ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_loading_view);// 加载布局
        Dialog progressDialog;
        progressDialog = new Dialog(context, R.style.MyDialogStyle);
        progressDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView amsg = (TextView) progressDialog.findViewById(R.id.tipTextView);
        amsg.setText(msg);
//        amsg.setText("加载中...");
        progressDialog.show();
        return progressDialog;
    }


    /**
     * 关闭dialog
     * <p>
     * http://blog.csdn.net/qq_21376985
     *
     * @param mDialogUtils
     */
    public static void closeDialog(Dialog mDialogUtils) {
        if (mDialogUtils != null && mDialogUtils.isShowing()) {
            mDialogUtils.dismiss();
        }
    }

}