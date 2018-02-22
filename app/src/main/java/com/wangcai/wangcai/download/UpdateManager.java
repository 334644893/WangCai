package com.wangcai.wangcai.download;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.wangcai.wangcai.activity.WebMainActivity;
import com.wangcai.wangcai.widget.LoadingDialogUtils;


/**
 */
public class UpdateManager {
    private Context mContext;
    public static int nVersion_code;
    public static String prompt;
    public static int type;
    public static String url;

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    /**
     * 检测软件更新
     *
     * @param nVersion_code 最新版本号
     * @param prompt        提示更新语
     * @param type          1为强制更新,0可选
     * @param url           更新地址
     * @param isToast       是否弹出提示不用更新
     */
    public void checkUpdate(int nVersion_code, String prompt, int type, String url, final boolean isToast) {
        /**
         * 在这里请求后台接口，获取更新的内容和最新的版本号
         */
        // 版本的更新信息
        this.nVersion_code = nVersion_code;
        this.prompt = prompt;
        this.type = type;
        this.url = url;
        int mVersion_code = DeviceUtils.getVersionCode(mContext);// 当前的版本号
        if (mVersion_code < nVersion_code) {
            // 显示提示对话
             showNoticeDialog(prompt);
        } else {
            if (isToast) {
                Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 显示更新对话框
     *
     * @param version_info
     */
    private Dialog noticeDialog;
    private void showNoticeDialog(String version_info) {

        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("更新提示");
        builder.setMessage(version_info);
        // 更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                WebMainActivity.mUpdateLoading = LoadingDialogUtils.createLoadingDialog(mContext, "更新中...");//添加等待框
                mContext.startService(new Intent(mContext, DownLoadService.class));

            }
        });
        if (type == 0) {
            // 稍后更新
            builder.setNegativeButton("以后更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        noticeDialog = builder.create();
        if (type == 0) {
            noticeDialog.setCancelable(true);
        } else if (type == 1) {
            noticeDialog.setCancelable(false);
        }
        noticeDialog.show();
    }
}
