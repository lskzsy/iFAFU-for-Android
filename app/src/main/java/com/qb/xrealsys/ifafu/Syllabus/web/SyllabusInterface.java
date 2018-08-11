package com.qb.xrealsys.ifafu.Syllabus.web;

import com.qb.xrealsys.ifafu.Base.model.Response;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Syllabus.model.Course;
import com.qb.xrealsys.ifafu.Base.model.Model;
import com.qb.xrealsys.ifafu.Syllabus.model.Syllabus;
import com.qb.xrealsys.ifafu.User.model.User;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;
import com.qb.xrealsys.ifafu.Tool.HttpHelper;
import com.qb.xrealsys.ifafu.Tool.HttpResponse;
import com.qb.xrealsys.ifafu.Base.web.WebInterface;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 11/02/2018.
 */

public class SyllabusInterface extends WebInterface {

    private static final String SyllabusPage = "xskbcx.aspx";

    public SyllabusInterface(String inHost, UserAsyncController userController) throws IOException {
        super(inHost, userController);
    }

    public Map<String, Model> GetSyllabus(String number, String name) throws IOException {
        Syllabus    syllabus        = new Syllabus();
        Map<String, Model> answer   = new HashMap<>();
        String      accessUrl = makeAccessUrlHead() + SyllabusPage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121603";

        Map<String, String> header = GetRefererHeader(number);
        HttpHelper   request  = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Get(header);

        if (response.getStatus() != 200) {
            return null;
        }

        String  html     = response.getResponse();
        if (!LoginedCheck(html)) {
            return GetSyllabus(number, name);
        }
        Pattern      patternA = Pattern.compile("alert\\('(.*?)'\\)");
        Matcher      matcherA = patternA.matcher(response.getResponse());
        if (matcherA.find()) {
            return null;
        }

        /* Get search option */
        getSearchOptions(html, syllabus, "id=\"xnd\"", "学年第");

        /* Get syllabus information */
        syllabus.setCampus(0);
        Map<String, List<Course>> mapNameToCourse = new HashMap<>();
        Pattern patternC = Pattern.compile("(<br>|<td( class=\"noprint\"){0,1} align=\"Center\"" +
                "( rowspan=\"\\d+\"){0,1}( width=\"\\d+%\"){0,1}>)(((?!td).)*?)<br>" +
                "(((?!td).)*?)<br>(((?!td).)*?)<br>(((?!td).)*?)(<br>(((?!td).)*?)年(.*?)月(.*?)日(.*?)<br>(.*?)){0,1}(<br>|</td>)");
        Matcher matcherC = patternC.matcher(html);
        while (matcherC.find()) {
            Course course = new Course();
            course.setAccount(number);
            course.setName(matcherC.group(5));
            course.setTeacher(matcherC.group(9));
            course.setAddress(matcherC.group(11));
            if (syllabus.getCampus() == 0 && course.getAddress().contains("旗教")) {
                syllabus.setCampus(1);
            }
            if (!analysisCourseTime(course, matcherC.group(7))) {
                analysisCourseTime2(course, html, matcherC.start(), matcherC.group(7));
            }

            if (mapNameToCourse.containsKey(course.getName())) {
                //  merge
                for (Course queryCourse: mapNameToCourse.get(course.getName())) {
                    if (queryCourse.getWeekDay() == course.getWeekDay()
                            && queryCourse.getAddress().equals(course.getAddress())) {
                        int repeatBegin = queryCourse.getWeekBegin() > course.getWeekBegin() ?
                                queryCourse.getWeekBegin() : course.getWeekBegin();
                        int repeatEnd   = queryCourse.getWeekEnd() > course.getWeekEnd() ?
                                course.getWeekEnd() : queryCourse.getWeekEnd();

                        if (repeatEnd > repeatBegin) {
                            if (queryCourse.getEnd() + 1 == course.getBegin()) {
                                queryCourse.setEnd(course.getEnd());
                                if (repeatBegin == course.getWeekBegin()) {
                                    course.setWeekBegin(repeatEnd + 1);
                                } else {
                                    course.setWeekEnd(repeatBegin - 1);
                                }
                            }
                        }
                    }
                }
            } else {
                mapNameToCourse.put(course.getName(), new ArrayList<Course>());
            }

            if (course.getWeekEnd() >= course.getWeekBegin()) {
                syllabus.append(course);
            }
            mapNameToCourse.get(course.getName()).add(course);
        }

        answer.put("syllabus", syllabus);
        return answer;
    }

    private boolean analysisCourseTime(Course course, String timeString) throws IOException {
        Map<String, Integer> weekMap = new HashMap<String, Integer>() {{
            put(URLEncoder.encode("一", "GBK"), 1);
            put(URLEncoder.encode("二", "GBK"), 2);
            put(URLEncoder.encode("三", "GBK"), 3);
            put(URLEncoder.encode("四", "GBK"), 4);
            put(URLEncoder.encode("五", "GBK"), 5);
            put(URLEncoder.encode("六", "GBK"), 6);
            put(URLEncoder.encode("日", "GBK"), 0);
        }};

        Pattern pattern = Pattern.compile("周(.*)第((\\d+),)?(.*?)(\\d+)节\\{第(\\d+)-(\\d+)周(\\|(.*)周)?\\}");
        Matcher matcher = pattern.matcher(timeString);

        if (matcher.find()) {
            course.setTimeString(timeString);
            course.setWeekDay(weekMap.get(URLEncoder.encode(matcher.group(1), "GBK")));
            if (matcher.group(3) == null) {
                course.setBegin(Integer.parseInt(matcher.group(5)));
            } else {
                course.setBegin(Integer.parseInt(matcher.group(3)));
            }
            course.setEnd(Integer.parseInt(matcher.group(5)));
            course.setWeekBegin(Integer.parseInt(matcher.group(6)));
            course.setWeekEnd(Integer.parseInt(matcher.group(7)));
            if (matcher.group(8) != null) {
                if (GlobalLib.CompareUtfWithGbk("单", matcher.group(9))) {
                    course.setOddOrTwice(1);
                } else if (GlobalLib.CompareUtfWithGbk("双", matcher.group(9))) {
                    course.setOddOrTwice(2);
                }
            } else {
                course.setOddOrTwice(0);
            }

            return true;
        } else {
            return false;
        }
    }

    private void analysisCourseTime2(
            Course course, String html, int courseBeginIndex, String timeString) {
        int[] line = new int[8];
        for (int i = 0; i < line.length; i++) {
            line[i] = 0;
        }

        String tableContent = html.substring(html.indexOf("上午"), courseBeginIndex);
        Pattern patternA = Pattern.compile("<td( class=\"noprint\")? align=\"Center\"" +
                "( rowspan=\"(\\d+)\")?( width=\"\\d+%\")?>");
        Matcher matcherA = patternA.matcher(tableContent);

        int nowWeekDay      = 1;
        int nowCourseLine   = 1;
        while (matcherA.find()) {
            int rowspan = 1;
            if (matcherA.group(2) != null) {
                rowspan = Integer.parseInt(matcherA.group(3));
            }

            while (line[nowWeekDay] >= nowCourseLine) {
                nowWeekDay++;
                if (nowWeekDay == 8) {
                    nowWeekDay = 1;
                    nowCourseLine++;
                }
            }

            line[nowWeekDay] += rowspan;
            nowWeekDay += 1;
            if (nowWeekDay == 8) {
                nowWeekDay = 1;
                nowCourseLine++;
            }
        }

        while (line[nowWeekDay] >= nowCourseLine) {
            nowWeekDay++;
            if (nowWeekDay == 8) {
                nowWeekDay = 1;
                nowCourseLine++;
            }
        }

        course.setWeekDay(nowWeekDay);
        course.setBegin(nowCourseLine);

        Pattern patternB = Pattern.compile("\\{第(\\d+)-(\\d+)周\\|(\\d+)节/周\\}");
        Matcher matcherB = patternB.matcher(timeString);
        if (matcherB.find()){
            course.setWeekBegin(Integer.parseInt(matcherB.group(1)));
            course.setWeekEnd(Integer.parseInt(matcherB.group(2)));
            course.setEnd(nowCourseLine + Integer.parseInt(matcherB.group(3)) - 1);
        }
    }
}
