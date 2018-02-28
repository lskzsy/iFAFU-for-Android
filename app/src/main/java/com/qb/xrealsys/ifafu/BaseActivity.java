package com.qb.xrealsys.ifafu;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.qb.xrealsys.ifafu.dialog.ProgressDialog;

import java.util.List;

/**
 * Created by sky on 25/02/2018.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            MainApplication mainApplication = (MainApplication) getApplication();

            if (Boolean.valueOf(mainApplication.getConfigHelper().GetValue("verify"))) {
                Intent intent = new Intent(BaseActivity.this, ProtectActivity.class);
                startActivity(intent);
            }
        }

        super.onStop();
    }

    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager =
                (ActivityManager) getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance ==
                            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

}
