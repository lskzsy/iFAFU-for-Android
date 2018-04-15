package com.qb.xrealsys.ifafu.User;

import android.content.Intent;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Base.dialog.ProgressDialog;
import com.qb.xrealsys.ifafu.Base.model.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private MainApplication     mainApplication;

    private UserAsyncController currentUserController;

    private ExecutorService     threadPool;

    private Button          loginBtn;

    private EditText        loginAccount;

    private EditText        loginPassword;

    private ImageView       loginFinishBtn;

    private boolean         isKill;

    private ProgressDialog  progressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn        = findViewById(R.id.loginBtn);
        loginAccount    = findViewById(R.id.loginAccount);
        loginPassword   = findViewById(R.id.loginPassword);
        loginFinishBtn  = findViewById(R.id.loginFinishBtn);

        loginFinishBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        initProgress();

        mainApplication = (MainApplication) getApplication();
        currentUserController = mainApplication.getUserController();
        threadPool            = mainApplication.getCachedThreadPool();

        isKill = getIntent().getBooleanExtra("isKill", true);
        if (!isKill) {
            loginFinishBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isKill && !currentUserController.isLogin()) {
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
            case R.id.loginFinishBtn:
                if (!isKill) {
                    finish();
                }
                break;
        }
    }

    private void loginAction() {
        final String account  = loginAccount.getText().toString();
        final String password = loginPassword.getText().toString();

        displayProgress();
        threadPool.execute(new Runnable() {
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
        });
    }

    private void initProgress() {
        progressToast = new ProgressDialog(this);
    }

    private void displayProgress() {
        progressToast.show("正在验证...");
    }
}
