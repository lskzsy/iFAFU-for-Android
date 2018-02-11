package com.qb.xrealsys.ifafu;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.delegate.LeftMenuClickedDelegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 10/02/2018.
 */

public class LeftMenuController implements View.OnClickListener {

    private Activity                activity;

    private LinearLayout            menuContent;

    private List<Unit>              unitObjects;

    private Map<Integer, Integer>   tabMap;

    private LeftMenuClickedDelegate clickedDelegate;

    public LeftMenuController(Activity inActivity, int id) {
        activity    = inActivity;
        menuContent = activity.findViewById(id);
        unitObjects = new ArrayList<>();
        tabMap      = new HashMap<>();
    }

    public void Make(List<String> units, Map<String, List<String>> tabs, Map<String, List<Integer>> tabIcons) {
        for (String unit: units) {
            //  Make Unit
            Unit u = new Unit(unit, menuContent);
            if (tabs != null
                    && tabIcons != null
                    && tabs.get(unit) != null
                    && tabIcons.get(unit) != null) {
                Iterator<String>  tabsIter       = tabs.get(unit).iterator();
                Iterator<Integer> tabIconsIter   = tabIcons.get(unit).iterator();
                int i    = 0;
                int last = tabs.get(unit).size() - 1;
                while (tabsIter.hasNext() && tabIconsIter.hasNext()) {
                    //  Make Tab
                    String tabName = tabsIter.next();
                    int    tabIcon = tabIconsIter.next();

                    if (last == i) {
                        u.AddTab(tabName, tabIcon, false);
                    } else {
                        u.AddTab(tabName, tabIcon, true);
                    }
                    i++;
                }
            }

            unitObjects.add(u);
        }
    }

    public float getRawSize(int unit, float value) {
        Resources res = activity.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    public void setClickedDelegate(LeftMenuClickedDelegate clickedDelegate) {
        this.clickedDelegate = clickedDelegate;
    }

    @Override
    public void onClick(View v) {
        Integer tabIndex = tabMap.get(v.getId());
        clickedDelegate.onTabClick(tabIndex);
    }

    public class Unit {

        private ImageView    splitLine;

        private TextView     unitNameView;

        private LinearLayout tabsView;

        private LinearLayout fillView;

        private LinearLayout tabsListView;

        private List<Tab>    tabs;

        public Unit(String unitName, LinearLayout content) {
            tabs = new ArrayList<>();

            //  Add unit split Line
            splitLine = new ImageView(activity);
            splitLine.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            splitLine.setBackgroundResource(R.drawable.shape_line_split);
            content.addView(splitLine);

            //  Add unit name
            unitNameView = new TextView(activity.getBaseContext());
            unitNameView.setText(unitName);
            unitNameView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 80),
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 26)));
            unitNameView.setTextSize(12);
            unitNameView.setTextColor(Color.parseColor("#ffffff"));
            unitNameView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            content.addView(unitNameView);

            //  New tabs list
            tabsView = new LinearLayout(activity.getBaseContext());
            tabsView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
            tabsView.setOrientation(LinearLayout.HORIZONTAL);

            fillView = new LinearLayout(activity.getBaseContext());
            fillView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 20),
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1));
            tabsView.addView(fillView);

            tabsListView = new LinearLayout(activity.getBaseContext());
            tabsListView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            12));
            tabsListView.setOrientation(LinearLayout.VERTICAL);
            tabsView.addView(tabsListView);

            content.addView(tabsView);
        }

        public void AddTab(String tabName, int icon, boolean isSplit) {
            tabs.add(new Tab(tabsListView, tabName, icon, isSplit));
        }
    }

    public class Tab {

        private LinearLayout tabView;

        private ImageView    splitLine;

        private ImageView    iconView;

        private TextView     tabNameView;

        private ImageView    goView;

        public Tab(LinearLayout content, String tabName, int icon, boolean isSplit) {
            tabView = new LinearLayout(activity.getBaseContext());
            tabView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            tabView.setOrientation(LinearLayout.HORIZONTAL);
            tabView.setGravity(Gravity.CENTER_VERTICAL);

            //  Create icon view
            iconView = new ImageView(activity);
            iconView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 40),
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 40),
                            1));
            iconView.setImageResource(icon);
            tabView.addView(iconView);

            //  Create title view
            tabNameView = new TextView(activity.getBaseContext());
            LinearLayout.LayoutParams tabNameViewParams = new LinearLayout.LayoutParams(
                    (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 200),
                    (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 50),
                    1);
            tabNameViewParams.setMarginStart((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 10));
            tabNameView.setLayoutParams(tabNameViewParams);
            tabNameView.setText(tabName);
            tabNameView.setTextColor(Color.parseColor("#ffffff"));
            tabNameView.setTextSize(18);
            tabNameView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            tabView.addView(tabNameView);

            //  Create go view
            goView = new ImageView(activity);
            goView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 50),
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 15),
                            1));
            goView.setImageResource(R.drawable.icon_go);
            tabView.addView(goView);

            int newId = View.generateViewId();
            tabView.setId(newId);
            tabMap.put(newId, tabMap.size());
            tabView.setOnClickListener(LeftMenuController.this);
            content.addView(tabView);

            if (isSplit) {
                splitLine = new ImageView(activity);
                splitLine.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                splitLine.setBackgroundResource(R.drawable.shape_line_split);
                content.addView(splitLine);
            }
        }
    }
}
