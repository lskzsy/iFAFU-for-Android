package com.qb.xrealsys.ifafu.Main.controller;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.Main.delegate.UpdatePauseDelegate;
import com.qb.xrealsys.ifafu.Main.delegate.UpdateQueryCallbackDelegate;
import com.qb.xrealsys.ifafu.Main.model.UpdateInf;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;
import com.qb.xrealsys.ifafu.Tool.OSSHelper;

import java.util.Locale;

/**
 * Created by sky on 04/03/2018.
 */

public class UpdateController implements UpdatePauseDelegate, UpdateQueryCallbackDelegate {

    private Context         ctx;

    private OSSHelper       ossHelper;

    private ConfigHelper    configHelper;

    private boolean         checked;

    private UpdateInf       updateInf;

    private long            apkDownloadId;

    public UpdateController(Context ctx, OSSHelper ossHelper, ConfigHelper configHelper) {
        this.ossHelper      = ossHelper;
        this.configHelper   = configHelper;
        this.ctx            = ctx;

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        ctx.getApplicationContext().registerReceiver(new InstallReceiver(), filter);
    }

    public UpdateInf CheckUpdate() {
        setChecked(true);
        updateInf                   = new UpdateInf(ossHelper.getUpdateInf());

        int        nowVersionCode   = GlobalLib.GetLocalVersionCode(ctx);
        String     passVersion      = configHelper.GetValue("passVersion");

        if (passVersion != null && passVersion.equals(updateInf.getVersionName())) {
            return null;
        }

        if (updateInf.getVersionCode() > nowVersionCode) {
            return updateInf;
        }

        return null;
    }

    public void DownloadLastestVersionApk() {
        String accessUrl = configHelper.GetSystemValue("apkDownloadUrl");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(accessUrl));
        request.setTitle(String.format(
                Locale.getDefault(),
                "iFAFU V%s 下载中", updateInf.getVersionName()));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager)
                ctx.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

        apkDownloadId = manager.enqueue(request);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void UpdatePause() {
        configHelper.SetValue("passVersion", updateInf.getVersionName());
    }

    @Override
    public void UpdateQueryCallback() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadLastestVersionApk();
            }
        }).start();
    }

    public class InstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                installApk(context, downloadApkId);
            }
        }

        private void installApk(Context context, long downloadApkId) {
            if (downloadApkId == apkDownloadId) {
                DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
                if (downloadFileUri != null) {
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                } else {
                    Toast.makeText(ctx, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
