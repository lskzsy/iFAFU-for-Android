package com.qb.xrealsys.ifafu.User.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.Base.dialog.iOSDialog;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.User.delegate.ReplaceUserDelegate;
import com.qb.xrealsys.ifafu.Base.delegate.iOSDialogButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sky on 25/02/2018.
 */

public class AccountSettingDialog extends Dialog implements
        View.OnClickListener,
        AdapterView.OnItemClickListener {

    private iOSDialog userManagerDialog;

    private iOSDialog               queryPasswordDialog;

    private iOSDialog               clearUserInUserSavedDialog;

    private MainApplication         mainApplication;

    private UserAsyncController userController;

    private ReplaceUserDelegate     replaceUserDelegate;

    private JSONObject              userList;

    public AccountSettingDialog(@NonNull Context context, ReplaceUserDelegate replaceUserDelegate) {
        super(context, R.style.styleAccountSettingDialog);
        this.replaceUserDelegate = replaceUserDelegate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_account_setting);
        setCanceledOnTouchOutside(false);

        findViewById(R.id.accountSettingCancel).setOnClickListener(this);

        mainApplication = (MainApplication) getContext().getApplicationContext();
        userController  = mainApplication.getUserController();

        InitList();
    }

    private void InitList() {
        ListView    listView = findViewById(R.id.accountSettingList);

        userList = userController.getUserList();

        List<Map<String, Object>> adapterData = new ArrayList<>();
        int userCount = 1;
        try {
            Iterator<String> numberIter = userList.keys();
            while (numberIter.hasNext()) {
                String      number = numberIter.next();
                JSONObject  object = (JSONObject) userList.get(number);
                Map<String, Object> map = new HashMap<>();
                map.put("icon", R.drawable.icon_user);
                map.put("id", number);
                map.put("content", String.format(Locale.getDefault(), "%s(%s)",
                        object.getString("name"), number));
                adapterData.add(map);
                userCount++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> finalMap = new HashMap<>();
        finalMap.put("icon", R.drawable.icon_plus);
        finalMap.put("content", "添加账号");
        adapterData.add(finalMap);
        SimpleAdapter adapter = new SimpleAdapter(
                this.getContext(),
                adapterData,
                R.layout.gadget_item_account_setting,
                new String[] {"icon", "content"},
                new int[] {R.id.accountSettingItemIcon, R.id.accountSettingItemContent}
        );
        listView.setAdapter(adapter);
        listView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) GlobalLib.GetRawSize(
                        this.getContext(),
                        TypedValue.COMPLEX_UNIT_DIP,
                        (int)51 * userCount)));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void show() {
        super.show();

        Window window = this.getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.width  = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(windowParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accountSettingCancel:
                this.cancel();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.cancel();
        if (position == parent.getAdapter().getCount() - 1) {
            replaceUserDelegate.ReplaceUser();
        } else {
            showUserManagerDialog(parent, position);
        }
    }

    private void showUserManagerDialog(AdapterView<?> parent, int position) {
        userManagerDialog = new iOSDialog(this.getContext());
        Map<String, Object> object =
                (Map<String, Object>) parent.getAdapter().getItem(position);
        String userName  = "";
        String userPass  = "";

        try {
            JSONObject user = userList.getJSONObject(object.get("id").toString());
            userName = user.getString("name");
            userPass = user.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String userString   = String.format(
                Locale.getDefault(),
                "%s(%s)",
                userName, object.get("id").toString());
        final String userPassword = userPass;
        final String userNumber   = object.get("id").toString();

        userManagerDialog.setButtons(Arrays.asList("切换账号", "清除此账号", "查看密码", "取消"))
                .setTitle("账号管理")
                .setContent(String.format(
                        Locale.getDefault(),
                        this.getContext().getString(R.string.format_user_manager_switch_user),
                        userString))
                .setOnClickedListener(new iOSDialogButtonOnClickedDelegate() {
                    @Override
                    public void iOSButtonOnClicked(int position) {
                        userManagerDialog.cancel();

                        switch (position) {
                            case 0:
                                replaceUserDelegate.ReplaceUser(userNumber, userPassword);
                                break;
                            case 1:
                                showClearUserInUserSavedDialog(userString, userNumber);
                                break;
                            case 2:
                                showQueryPasswordDialog(userString, userPassword);
                                break;
                            case 3:
                                break;
                        }
                    }
                });
        userManagerDialog.show();
    }

    private void showClearUserInUserSavedDialog(final String userString, final String account) {
        clearUserInUserSavedDialog = new iOSDialog(this.getContext());
        clearUserInUserSavedDialog.setButtons(Arrays.asList("确定清除", "取消"))
                .setTitle("提示")
                .setContent(String.format(
                        Locale.getDefault(),
                        AccountSettingDialog
                                .this.getContext().getString(
                                R.string.format_clear_user),
                        userString))
                .setOnClickedListener(new iOSDialogButtonOnClickedDelegate() {
                    @Override
                    public void iOSButtonOnClicked(int position) {
                        clearUserInUserSavedDialog.cancel();

                        switch (position) {
                            case 0:
                                String message = "清除成功";
                                if (!userController.clearUserInfo(account)) {
                                    message = String.format(
                                            Locale.getDefault(),
                                            AccountSettingDialog
                                                    .this.getContext().getString(
                                                    R.string.error_reject_option),
                                            AccountSettingDialog
                                                    .this.getContext().getString(
                                                    R.string.error_clear_user));
                                }

                                Toast.makeText(
                                        AccountSettingDialog.this.getContext(),
                                        message,
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        clearUserInUserSavedDialog.show();
    }

    private void showQueryPasswordDialog(final String userString, final String password) {
        queryPasswordDialog = new iOSDialog(this.getContext());
        queryPasswordDialog.setButtons(Arrays.asList("复制到剪贴板", "好"))
                .setTitle(String.format(Locale.getDefault(), "%s的密码", userString))
                .setContent(password)
                .setOnClickedListener(new iOSDialogButtonOnClickedDelegate() {
                    @Override
                    public void iOSButtonOnClicked(int position) {
                        queryPasswordDialog.cancel();
                        switch (position) {
                            case 0:
                                GlobalLib.PutTextToClipboard(
                                        AccountSettingDialog.this.getContext(),
                                        "iFAFUPassword",
                                        password);
                                Toast.makeText(
                                        AccountSettingDialog.this.getContext(),
                                        "已复制到剪贴板",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        queryPasswordDialog.show();
    }
}
