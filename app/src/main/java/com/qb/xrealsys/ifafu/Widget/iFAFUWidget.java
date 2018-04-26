package com.qb.xrealsys.ifafu.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.RemoteViews;

import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusAsyncController;
import com.qb.xrealsys.ifafu.Syllabus.model.Course;
import com.qb.xrealsys.ifafu.Syllabus.model.Syllabus;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class iFAFUWidget extends AppWidgetProvider {

    static private SyllabusAsyncController syllabusController;

    static private UserAsyncController     userController;

    static private ConfigHelper            configHelper;

    static private SpannableString         mainSyllabusBlankString;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.i_fafuwidget);

        Syllabus inSyllabus = syllabusController.GetData();
        views.setTextViewText(R.id.main_syllabus_title, String.format(Locale.getDefault(),
                context.getString(R.string.format_main_syllabus_title),

        inSyllabus.getSearchYearOptions().get(
                inSyllabus.getSelectedYearOption()),
        inSyllabus.getSearchTermOptions().get(
                inSyllabus.getSelectedTermOption())));

        String[] studyTime = GlobalLib.GetStudyTime(
                configHelper.GetValue("nowTermFirstWeek"));
        views.setTextViewText(R.id.main_syllabus_time, studyTime[0]);

        int nowWeek = Integer.parseInt(studyTime[1]);
        int weekDay = Integer.parseInt(studyTime[2]);
        if (nowWeek < 1 || nowWeek > 24) {
            views.setTextViewText(R.id.main_syllabus_content, mainSyllabusBlankString);
            return;
        }

        List<Course> courseList
                = syllabusController.GetCourseInfoByWeekAndWeekday(nowWeek, weekDay);
        if (courseList.size() < 1) {
            views.setTextViewText(R.id.main_syllabus_content, mainSyllabusBlankString);
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
        MainApplication mainApplication = (MainApplication) context.getApplicationContext();

        userController     = mainApplication.getUserController();
        syllabusController = mainApplication.getSyllabusController();
        configHelper       = mainApplication.getConfigHelper();

        mainSyllabusBlankString = new SpannableString("0今天没有课");
        mainSyllabusBlankString.setSpan(new ImageSpan(context, R.drawable.drawable_superman),
                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Log.d("debug", String.valueOf(userController == null));

        if (userController == null || !userController.isLogin()) {
            return;
        }

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

