package com.arms.perfect.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.arms.perfect.mylibrary.R;

/**
 * @Author: Joe liuzhaojava@foxmail.com 2018/3/10 21:16
 * @Description: 主要用作消息提示显示，如权限请求，其他请自定义DialogFragment
 */
public class DialogUtil {

    public static AlertDialog.Builder dialogBuilder(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (msg != null) {
//            builder.setMessage(msg);
            View msgView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_msg, null);
            TextView tv_dialog_msg = msgView.findViewById(R.id.tv_dialog_msg);
            tv_dialog_msg.setText(msg);
            builder.setView(msgView);
        }
        if (title != null) {
//            builder.setTitle(title);
            View titleView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_title, null);
            TextView tv_dialog_title = titleView.findViewById(R.id.tv_dialog_title);
            tv_dialog_title.setText(title);
            builder.setCustomTitle(titleView);
        }
        return builder;
    }

    public static AlertDialog.Builder dialogBuilder(Context context, String title, String msg, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (msg != null) {
            builder.setMessage(Html.fromHtml(msg));
        }
        if (title != null) {
            builder.setTitle(title);
        }
        return builder;
    }


    public static AlertDialog.Builder dialogBuilder(Context context, int title, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (view != null) {
            builder.setView(view);
        }
        if (title > 0) {
            builder.setTitle(title);
        }
        return builder;
    }

    public static AlertDialog.Builder dialogBuilder(Context context, int titleResId, int msgResId) {
        String title = titleResId > 0 ? context.getResources().getString(titleResId) : null;
        String msg = msgResId > 0 ? context.getResources().getString(msgResId) : null;
        return dialogBuilder(context, title, msg);
    }

    public static Dialog showTips(Context context, String title, String btn) {
        return showTips(context, title, null, btn, null);
    }

    public static Dialog showTips(Context context, int title, int msg) {
        return showTips(context, context.getString(title), context.getString(msg));
    }

    public static Dialog showTips(Context context, int title, int msg, int btn, DialogInterface.OnDismissListener
            dismissListener) {
        return showTips(context, context.getString(title), context.getString(msg), context.getString(btn),
                dismissListener);
    }

    public static Dialog showTips(Context context, String title, String msg, String btn, DialogInterface
            .OnDismissListener dismissListener) {
        AlertDialog.Builder builder = dialogBuilder(context, title, msg);
        builder.setCancelable(true);
        builder.setPositiveButton(btn, null);
        Dialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(dismissListener);
        return dialog;
    }
}
