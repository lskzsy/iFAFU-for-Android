package com.qb.xrealsys.ifafu.Widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusAsyncController;
import com.qb.xrealsys.ifafu.Syllabus.delegate.UpdateMainSyllabusViewDelegate;
import com.qb.xrealsys.ifafu.Syllabus.model.Course;
import com.qb.xrealsys.ifafu.Syllabus.model.Syllabus;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class iFAFUWidget extends AppWidgetProvider {

    final static private String btnCallMessage  = "com.qb.xrealsys.ifafu.Widget.btn.call";

    final static public  String updateAllWidget = "com.qb.xrealsys.ifafu.Widget.update.all";

    static private SyllabusAsyncController syllabusController;

    static private ConfigHelper            configHelper;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.i_fafuwidget);

        //  Update content data
        Syllabus inSyllabus = syllabusController.GetData();
        views.setTextViewText(R.id.main_syllabus_title, String.format(Locale.getDefault(),
                context.getString(R.string.format_main_syllabus_title),
                inSyllabus.getSearchYearOptions().get(inSyllabus.getSelectedYearOption()),
                inSyllabus.getSearchTermOptions().get(inSyllabus.getSelectedTermOption())));

        String[] studyTime = GlobalLib.GetStudyTime(
                configHelper.GetValue("nowTermFirstWeek"));
        views.setTextViewText(R.id.main_syllabus_time, studyTime[0]);

        int nowWeek = Integer.parseInt(studyTime[1]);
        int weekDay = Integer.parseInt(studyTime[2]);
        if (nowWeek < 1 || nowWeek > 24) {
            views.setTextViewText(R.id.main_syllabus_content, "今天没有课");
            return;
        }

        List<Course> courseList
                = syllabusController.GetCourseInfoByWeekAndWeekday(nowWeek, weekDay);
        if (courseList.size() < 1) {
            views.setTextViewText(R.id.main_syllabus_content, "今天没有课");
        } else {
            String display = String.format(
                    Locale.getDefault(),"今天有%d节课\n", courseList.size());
            String willStudyTime = syllabusController.GetWillStudyTime(courseList);
            if (willStudyTime == null) {
                display += "无待上课程";
            } else {
                display += willStudyTime;
            }
            views.setTextViewText(R.id.main_syllabus_content, display);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d("log", "Widget onUpdate.");
        InitData(context);

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.i_fafuwidget);
        remoteViews.setOnClickPendingIntent(
                R.id.IFAFUWidgetContent, getPendingIntent(context, R.id.IFAFUWidgetContent));

        final int[]             fAppWidgetIds       = appWidgetIds;
        final Context           fContext            = context;
        final AppWidgetManager  fAppWidgetManager   = appWidgetManager;
        syllabusController.setUpdateWidgetSyllabusViewDelegate(new UpdateMainSyllabusViewDelegate() {
            @Override
            public void updateMainSyllabus(Syllabus syllabus) {
                Log.d("log", "SyncMessage: success.");
                updateAllWidgets(fContext, fAppWidgetManager, fAppWidgetIds);
            }
        });
        syllabusController.SyncDataWithLocal();
    }

    private void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appIds) {
        Log.d("log", "Widgets update.");
        for (int appWidgetId : appIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private PendingIntent getPendingIntent(Context context, int resID){
        Intent intent = new Intent();
        intent.setAction(btnCallMessage);
        intent.setData(Uri.parse("id:" + resID));

        return PendingIntent.getBroadcast(context, 0, intent,0);
    }

    private void InitData(Context context) {
        if (syllabusController == null) {
            MainApplication mainApplication = (MainApplication) context.getApplicationContext();

            syllabusController = mainApplication.getSyllabusController();
            configHelper       = mainApplication.getConfigHelper();

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendIntent = PendingIntent.getBroadcast(
                    context, 0, new Intent(updateAllWidget), PendingIntent.FLAG_UPDATE_CURRENT);

            long triggerAtTime = SystemClock.elapsedRealtime() + 60 * 1000;
            int interval = 60 * 1000;
            alarmMgr.setRepeating(AlarmManager.RTC, triggerAtTime, interval, pendIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            super.onReceive(context, intent);
            return;
        }
        Log.d("log", "Receive " + action);

        if (btnCallMessage.equals(action)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.i_fafuwidget);
            Uri data = intent.getData();
            if (data != null){
                int resId = Integer.parseInt(data.getSchemeSpecificPart());

                switch (resId) {
                    case R.id.IFAFUWidgetContent:
                        Intent startIntent = new Intent();
                        startIntent.setComponent(
                                new ComponentName(
                                        "com.qb.xrealsys.ifafu",
                                        "com.qb.xrealsys.ifafu.SyllabusActivity"));
                        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startIntent);
                        break;
                }
            }

            AppWidgetManager manger = AppWidgetManager.getInstance(context);
            ComponentName thisName = new ComponentName(context, iFAFUWidget.class);
            manger.updateAppWidget(thisName, remoteViews);
        } else if (updateAllWidget.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    updateAllWidgets(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

