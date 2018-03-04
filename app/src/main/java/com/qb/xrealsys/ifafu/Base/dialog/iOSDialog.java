package com.qb.xrealsys.ifafu.Base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.Base.delegate.iOSDialogButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 25/02/2018.
 */

public class iOSDialog extends Dialog implements AdapterView.OnItemClickListener {

    private iOSDialogButtonOnClickedDelegate delegate;

    public iOSDialog(@NonNull Context context) {
        super(context, R.style.styleIOSDialog);
        setContentView(R.layout.dialog_ios);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
    }

    public iOSDialog setButtons(List<String> buttons) {
        ListView buttonView = findViewById(R.id.iOSDialogSingleBtnList);

        List<Map<String, Object>> adapterData = new ArrayList<>();
        for (String button: buttons) {
            Map<String, Object> map = new HashMap<>();
            map.put("buttonTitle", button);
            adapterData.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(
                this.getContext(),
                adapterData,
                R.layout.gadget_item_ios_dialog_button,
                new String[] {"buttonTitle"},
                new int[] {R.id.iOSDialogButtonTitle});
        buttonView.setAdapter(adapter);
        buttonView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) GlobalLib.GetRawSize(
                        this.getContext(),
                        TypedValue.COMPLEX_UNIT_DIP,
                        buttons.size() * 41)));
        buttonView.setOnItemClickListener(this);

        return this;
    }

    public iOSDialog setTitle(String title) {
        TextView titleView = findViewById(R.id.iOSDialogTitle);
        titleView.setText(title);
        return this;
    }

    public iOSDialog setContent(String content) {
        TextView contentView = findViewById(R.id.iOSDialogContent);
        contentView.setText(content);
        return this;
    }

    public iOSDialog setOnClickedListener(iOSDialogButtonOnClickedDelegate delegate) {
        this.delegate = delegate;
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (delegate != null) {
            delegate.iOSButtonOnClicked(position);
        }
    }
}
