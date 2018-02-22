package com.wangcai.wangcai.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.wangcai.wangcai.R;


/**
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to
 * create and use your own look & feel.
 * <p>
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * <p>
 * <a href="http://my.oschina.net/arthor" target="_blank" rel="nofollow">@author</a> antoine vianey
 */
public class NoticeDialog extends Dialog {

    public NoticeDialog(Context context, int theme) {
        super(context, theme);
    }

    public NoticeDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private View contentView;
        private String amount;
        private OnClickListener
                positiveButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the positive button text and it's listener
         *
         * @param listener
         * @return
         */
        public Builder setPositiveButton(OnClickListener listener) {

            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param amount
         * @return
         */
        public Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }


        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         *
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }


        /**
         * Create the custom dialog
         */
        public NoticeDialog create() {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final NoticeDialog dialog = new NoticeDialog(context,
                    R.style.Dialog);
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.setCanceledOnTouchOutside(true);
            View layout = inflater.inflate(R.layout.notice_custom_dialog_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            //initView
            ((TextView) layout.findViewById(R.id.tv_2)).setText(amount);
            ((TextView) layout.findViewById(R.id.tv_2)).setMovementMethod(ScrollingMovementMethod.getInstance());
            // set the confirm button
            layout.findViewById(R.id.positiveButton)
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                    });


            dialog.setContentView(layout);
            return dialog;
        }
    }

}
