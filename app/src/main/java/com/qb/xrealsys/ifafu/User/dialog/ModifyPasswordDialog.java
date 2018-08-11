package com.qb.xrealsys.ifafu.User.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.Base.model.Response;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.User.delegate.ModifyPasswordCallbackDelegate;
import com.qb.xrealsys.ifafu.User.delegate.ModifyPasswordDelegate;

public class ModifyPasswordDialog extends Dialog implements View.OnClickListener, ModifyPasswordCallbackDelegate {

    private Activity    activity;

    private ImageView   cancelBtn;

    private Button      submitBtn;

    private EditText    newPassword;

    private ModifyPasswordDelegate delegate;

    public ModifyPasswordDialog(@NonNull Context context, ModifyPasswordDelegate delegate) {
        super(context, R.style.styleProgressDialog);
        this.activity = (Activity) context;
        this.delegate = delegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_modify_password);
        setCanceledOnTouchOutside(false);

        cancelBtn   = findViewById(R.id.closeBtn);
        submitBtn   = findViewById(R.id.queryBtn);
        newPassword = findViewById(R.id.newPasswordInput);
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeBtn:
                if (delegate.cancelClick()) {
                    this.cancel();
                }
                break;
            case R.id.queryBtn:
                delegate.submitClick(newPassword.getText().toString());
                break;
        }
    }

    @Override
    public void modifyPasswordCallback(Response response) {
        final Response fResponse = response;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fResponse.isSuccess()) {
                    delegate.successModify();
                } else {
                    Toast.makeText(activity, fResponse.getMessage(activity), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
