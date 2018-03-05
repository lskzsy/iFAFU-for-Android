package com.qb.xrealsys.ifafu.Main.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.Main.delegate.UpdatePauseDelegate;
import com.qb.xrealsys.ifafu.Main.delegate.UpdateQueryCallbackDelegate;
import com.qb.xrealsys.ifafu.Main.model.UpdateInf;
import com.qb.xrealsys.ifafu.R;

import java.util.Locale;

/**
 * Created by sky on 05/03/2018.
 */

public class UpdateDialog extends Dialog implements View.OnClickListener {

    private TextView updateStyleView;

    private TextView updateVersionView;

    private TextView commemtView;

    private Button   btn1;

    private Button   btn2;

    private UpdateInf updateInf;

    private UpdateQueryCallbackDelegate delegate;

    private UpdatePauseDelegate pauseDelegate;

    public UpdateDialog(
            @NonNull Context context,
            UpdateQueryCallbackDelegate delegate,
            UpdatePauseDelegate pauseDelegate) {
        super(context, R.style.styleProgressDialog);
        setContentView(R.layout.dialog_update);
        setCanceledOnTouchOutside(false);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        this.delegate       = delegate;
        this.pauseDelegate  = pauseDelegate;

        updateStyleView     = findViewById(R.id.updateDialogStyle);
        updateVersionView   = findViewById(R.id.updateDialogVersion);
        commemtView         = findViewById(R.id.updateDialogDetail);
        btn1                = findViewById(R.id.updateDialogCancel);
        btn2                = findViewById(R.id.updateDialogOk);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    public void show(UpdateInf updateInf) {
        this.updateInf = updateInf;
        if (updateInf.isForceUpdating()) {
            btn1.setText("退出");
        }
        updateStyleView.setText(updateInf.isForceUpdating() ? "强制更新" : "推荐更新");
        updateVersionView.setText(
                String.format(Locale.getDefault(), "V%s", updateInf.getVersionName()));
        commemtView.setText(updateInf.getComment());

        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateDialogCancel:
                if (updateInf.isForceUpdating()) {
                    System.exit(0);
                } else {
                    pauseDelegate.UpdatePause();
                    this.cancel();
                }
                break;
            case R.id.updateDialogOk:
                delegate.UpdateQueryCallback();
                btn2.setText("下载中...");
                btn2.setEnabled(false);
                break;
        }
    }
}
