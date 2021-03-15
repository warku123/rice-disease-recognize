package com.example.cogrice;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;





public class TigDialog extends BaseDialog {

    TextView  tv_commit, tv;

    int myTag;//标记
    String content;

    public TigDialog(Activity activity, String content) {
        super(activity);
        this.content=content;

    }

    @Override
    public void initDialogEvent(Window window) {
        window.setContentView(R.layout.dialog_regi);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //init view
        tv = window.findViewById(R.id.tv); tv_commit = window.findViewById(R.id.tv_commit);
        tv.setText(content);
        //set view

        //确定
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    @Override
    public void showDialog() {
        dialog = new AlertDialog.Builder(activity).create();
        //点击外部区域取消dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(null);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout((int) (ScreenUtil.getScreenWidth(activity) * 0.8), (int) (ScreenUtil.getScreenHeight(activity) * 0.5));
        window.setGravity(Gravity.CENTER);
        //解决棱角问题
        window.setBackgroundDrawable(new BitmapDrawable());
        initDialogEvent(window);
    }
}

