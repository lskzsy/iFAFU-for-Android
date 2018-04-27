package com.qb.xrealsys.ifafu.Syllabus.controller;

import android.util.Log;

import com.qb.xrealsys.ifafu.Base.controller.AsyncController;
import com.qb.xrealsys.ifafu.DB.SystemConfig;
import com.qb.xrealsys.ifafu.DB.UserConfig;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Syllabus.delegate.UpdateMainSyllabusViewDelegate;
import com.qb.xrealsys.ifafu.Syllabus.delegate.UpdateMainUserViewDelegate;
import com.qb.xrealsys.ifafu.Syllabus.model.Course;
import com.qb.xrealsys.ifafu.Base.model.Model;
import com.qb.xrealsys.ifafu.Syllabus.model.Syllabus;
import com.qb.xrealsys.ifafu.User.model.User;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Syllabus.web.SyllabusInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sky on 12/02/2018.
 */

public class SyllabusAsyncController extends AsyncController {

    private static int[][] studyBeginTime = new int[][] {
            {800, 850, 955, 1045, 1135, 1400, 1450, 1550, 1640, 1825, 1915, 2005},
            {830, 920, 1025, 1115, 1205, 1400, 1450, 1550, 1640, 1825, 1915, 2005}};

    private Syllabus                        syllabus;

    private User                            user;

    private UserAsyncController             userController;

    private ConfigHelper                    configHelper;

    private UpdateMainSyllabusViewDelegate  updateMainSyllabusViewDelegate;

    public SyllabusAsyncController(UserAsyncController userController, ConfigHelper configHelper) {
        super(userController.getThreadPool());
        this.userController = userController;
        this.user           = userController.getData();
        this.syllabus       = new Syllabus();
        this.configHelper   = configHelper;
    }

    public void SyncDataWithLocal() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Realm   realm   = Realm.getDefaultInstance();
                String  account = configHelper.GetValue("account");

                SystemConfig    systemConfig = realm.where(SystemConfig.class)
                        .equalTo("account", account).findFirst();
                RealmResults<Course> results = realm.where(Course.class)
                        .equalTo("account", account).findAll();

                syllabus.setData(results);

                if (systemConfig != null) {
                    syllabus.setSearchYearOptions(Arrays.asList(systemConfig.getDefaultYear()));
                    syllabus.setSearchTermOptions(Arrays.asList(systemConfig.getDefaultTerm()));
                } else {
                    syllabus.setSearchYearOptions(Arrays.asList("加载中"));
                    syllabus.setSearchTermOptions(Arrays.asList("加载中"));
                }
                syllabus.setSelectedYearOption(0);
                syllabus.setSelectedTermOption(0);

                updateMainSyllabusViewDelegate.updateMainSyllabus(syllabus);
            }
        });
    }

    public void SyncData() throws IOException {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SyllabusInterface syllabusInterface = new SyllabusInterface(
                            configHelper.GetSystemValue("host"),
                            userController);
                    Map<String, Model> answer = syllabusInterface.GetSyllabus(user.getAccount(), user.getName());

                    syllabus = (Syllabus) answer.get("syllabus");
                    Collections.sort(syllabus.getData(), new SortCourseComparator());
                    updateMainSyllabusViewDelegate.updateMainSyllabus(syllabus);
                    SyncLocalSyllabus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void SyncLocalSyllabus() {
        Realm mRealm = Realm.getDefaultInstance();

        final RealmResults<Course> results = mRealm.where(Course.class)
                .equalTo("account", user.getAccount()).findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();

                List<Course> courseList = syllabus.getData();
                SystemConfig systemConfig = new SystemConfig();
                systemConfig.setAccount(user.getAccount());
                systemConfig.setDefaultYear(
                        syllabus.getSearchYearOptions().get(syllabus.getSelectedYearOption()));
                systemConfig.setDefaultTerm(
                        syllabus.getSearchTermOptions().get(syllabus.getSelectedTermOption()));
                realm.insert(courseList);
                realm.insertOrUpdate(systemConfig);
            }
        });
    }

    public Syllabus GetData() {
        return this.syllabus;
    }

    public List<List<Course>> GetCourseInfoByWeek(int nowWeek) {
        List<Course>        courseList = this.syllabus.getData();
        List<List<Course>>  answer     = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            answer.add(new ArrayList<Course>());
        }

        for (Course course: courseList) {
            if (isCourseInWeek(nowWeek, course)) {
                answer.get(course.getWeekDay()).add(course);
            }
        }

        return answer;
    }

    public List<Course> GetCourseInfoByWeekAndWeekday(int nowWeek, int weekDay) {
        List<Course> courseList = this.syllabus.getData();
        List<Course> answer     = new ArrayList<>();

        for (Course course: courseList) {
            if (isCourseInWeek(nowWeek, course) && weekDay == course.getWeekDay()) {
                answer.add(course);
            }
        }

        return answer;
    }

    public String GetNowStudyTime(String format) {
        return String.format(
                Locale.getDefault(),
                format,
                syllabus.getSearchYearOptions().get(syllabus.getSelectedYearOption()),
                syllabus.getSearchTermOptions().get(syllabus.getSelectedTermOption()));
    }

    private boolean isCourseInWeek(int nowWeek, Course course) {
        if (nowWeek < course.getWeekBegin() || nowWeek > course.getWeekEnd()) {
            return false;
        }

        int oddOrTwice = course.getOddOrTwice();
        if (oddOrTwice != 0) {
            if (oddOrTwice == 1 && nowWeek % 2 == 0) {
                return false;
            } else if (oddOrTwice == 2 && nowWeek % 2 == 1) {
                return false;
            }
        }

        return true;
    }

    class SortCourseComparator implements Comparator<Course> {

        @Override
        public int compare(Course c1, Course c2) {
            int weekDay = c1.getWeekDay() - c2.getWeekDay();
            return weekDay != 0 ? weekDay : c1.getBegin() - c2.getBegin();
        }
    }

    public String GetWillStudyTime(List<Course> courseList) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);
        int j = 0;
        int k = 0;
        for (int i = 0; i < studyBeginTime[syllabus.getCampus()].length; i++) {
            int time = studyBeginTime[syllabus.getCampus()][i];
            if (now < time) {
                j = i + 1;
                k = time;
                break;
            }
        }

        if (j == 0) {
            return null;
        }

        for (Course course: courseList) {
            if (course.getBegin() >= j) {
                String answer = String.format(
                        Locale.getDefault(), "下一门课:%s@%s\n",
                        course.getName(), course.getAddress());

                k = studyBeginTime[syllabus.getCampus()][course.getBegin()];
                if (k - now < 100) {
                    int cMin = k % 100;
                    int nMin = now % 100;

                    answer += String.format(
                            Locale.getDefault(), "%d分种后开始",
                            cMin - nMin + (cMin < nMin ? 60 : 0));
                } else {
                    answer += String.format(
                            Locale.getDefault(), "%d小时后开始",
                            (k - now) / 100);
                }

                return answer;
            }
        }

        return null;
    }

    public void setUpdateMainSyllabusViewDelegate(UpdateMainSyllabusViewDelegate updateMainSyllabusViewDelegate) {
        this.updateMainSyllabusViewDelegate = updateMainSyllabusViewDelegate;
    }
}
