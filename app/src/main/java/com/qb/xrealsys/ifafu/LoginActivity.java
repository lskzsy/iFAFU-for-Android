package com.qb.xrealsys.ifafu;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.model.Response;
import com.qb.xrealsys.ifafu.tool.GlobalLib;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private UserController currentUserController;

    private Button   loginBtn;

    private EditText loginAccount;

    private EditText loginPassword;

    private Toast    progressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn        = (Button) findViewById(R.id.loginBtn);
        loginAccount    = (EditText) findViewById(R.id.loginAccount);
        loginPassword   = (EditText) findViewById(R.id.loginPassword);

        loginBtn.setOnClickListener(this);
        initProgress();
        try {
            currentUserController = new UserController(this.getBaseContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!currentUserController.isLogin()) {
            System.exit(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                loginAction();
                break;
        }
    }

    private void loginAction() {
        final String account  = loginAccount.getText().toString();
        final String password = loginPassword.getText().toString();

        displayProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Response response = currentUserController.Login(account, password, true);
                    progressToast.cancel();
                    Toast.makeText(LoginActivity.this, response.getMessage(LoginActivity.this), Toast.LENGTH_SHORT).show();
                    if (response.isSuccess()) {
                        //  Login success
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userObject", currentUserController.getData());
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

    private void initProgress() {
        progressToast = Toast.makeText(getBaseContext(), "正在验证...", Toast.LENGTH_LONG);
        progressToast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) progressToast.getView();
        ImageView imageCodeProject = new ImageView(getApplicationContext());
        imageCodeProject.setLayoutParams(new LinearLayout.LayoutParams(
                (int) GlobalLib.GetRawSize(this, TypedValue.COMPLEX_UNIT_DIP, 50),
                (int) GlobalLib.GetRawSize(this, TypedValue.COMPLEX_UNIT_DIP, 50)
        ));
        imageCodeProject.setImageResource(R.drawable.icon_runelective);
        toastView.addView(imageCodeProject, 0);
    }

    private void displayProgress() {
        progressToast.show();
    }
}
