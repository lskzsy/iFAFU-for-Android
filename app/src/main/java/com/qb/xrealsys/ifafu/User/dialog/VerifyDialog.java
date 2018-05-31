package com.qb.xrealsys.ifafu.User.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Tool.ZFVerify;

import org.w3c.dom.Text;

public class VerifyDialog extends Dialog {

    private ImageView verifyImage;

    private TextView  verifyAnswer;

    private Activity  activity;

    public VerifyDialog(@NonNull Context context) {
        super(context, R.style.styleIOSDialog);
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verify);
        setCanceledOnTouchOutside(false);
        verifyImage     = findViewById(R.id.verifyImg);
        verifyAnswer    = findViewById(R.id.verifyAnswer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ZFVerify zfVerify = ((MainApplication) activity.getApplication()).getZfVerify();
                final Bitmap bitmap = zfVerify.getVerifyImg("http://jwgl.fafu.edu.cn/CheckCode.aspx");
                final String verify = zfVerify.todo(bitmap);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        verifyImage.setImageBitmap(bitmap);
                        verifyAnswer.setText(verify);
                    }
                });
            }
        }).start();
    }
}
