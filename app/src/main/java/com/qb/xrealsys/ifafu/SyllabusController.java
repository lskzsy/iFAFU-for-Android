package com.qb.xrealsys.ifafu;

import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.qb.xrealsys.ifafu.delegate.UpdateMainSyllabusViewDelegate;
import com.qb.xrealsys.ifafu.delegate.UpdateMainUserViewDelegate;
import com.qb.xrealsys.ifafu.model.Course;
import com.qb.xrealsys.ifafu.model.Model;
import com.qb.xrealsys.ifafu.model.Syllabus;
import com.qb.xrealsys.ifafu.model.User;
import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.web.SyllabusInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sky on 12/02/2018.
 */

public class SyllabusController {

    private static int[] studyBeginTime = new int[] {
        800, 850, 955, 1045, 1135, 1400, 1450, 1535, 1640, 1825, 1915, 2005};

    private Syllabus        syllabus;

    private User            user;

    private UserController  userController;

    private ConfigHelper    configHelper;

    private UpdateMainUserViewDelegate updateMainUserViewDelegate;

    private UpdateMainSyllabusViewDelegate updateMainSyllabusViewDelegate;

    public SyllabusController(UserController userController, ConfigHelper configHelper) {
        this.userController = userController;
        this.user           = userController.getData();
        this.syllabus       = new Syllabus();
        this.configHelper   = configHelper;
    }

    public void SyncData() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyllabusInterface syllabusInterface = new SyllabusInterface(
                            configHelper.GetSystemValue("host"),
                            userController);
                    Map<String, Model> answer = syllabusInterface.GetSyllabus(user.getAccount(), user.getName());
                    if (answer == null) {
                        updateMainUserViewDelegate.updateError("获取失败");
                        return;
                    }

                    User answerUser = (User) answer.get("user");
                    user.setClas(answerUser.getClas());
                    user.setEnrollment(answerUser.getEnrollment());
                    user.setInstitute(answerUser.getInstitute());
                    updateMainUserViewDelegate.updateMainUser(user);

                    syllabus = (Syllabus) answer.get("syllabus");
                    Collections.sort(syllabus.getData(), new SortCourseComparator());
                    updateMainSyllabusViewDelegate.updateMainSyllabus(syllabus);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        for (int i = 0; i < studyBeginTime.length; i++) {
            int time = studyBeginTime[i];
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
            if (course.getBegin() == j) {
                String answer = String.format(
                        Locale.getDefault(), "下一门课:%s@%s\n",
                        course.getName(), course.getAddress());

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

    public void setUpdateMainUserViewDelegate(UpdateMainUserViewDelegate updateMainUserViewDelegate) {
        this.updateMainUserViewDelegate = updateMainUserViewDelegate;
    }

    public void setUpdateMainSyllabusViewDelegate(UpdateMainSyllabusViewDelegate updateMainSyllabusViewDelegate) {
        this.updateMainSyllabusViewDelegate = updateMainSyllabusViewDelegate;
    }
}
