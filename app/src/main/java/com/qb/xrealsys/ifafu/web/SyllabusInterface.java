package com.qb.xrealsys.ifafu.web;

import com.qb.xrealsys.ifafu.model.Course;
import com.qb.xrealsys.ifafu.model.Model;
import com.qb.xrealsys.ifafu.model.Syllabus;
import com.qb.xrealsys.ifafu.model.User;
import com.qb.xrealsys.ifafu.tool.HttpHelper;
import com.qb.xrealsys.ifafu.tool.HttpResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 11/02/2018.
 */

public class SyllabusInterface extends WebInterface {

    private static final String SyllabusPage = "xskbcx.aspx";

    public SyllabusInterface(String inHost, String inToken) throws IOException {
        super(inHost, inToken);
    }

    public Map<String, Model> GetSyllabus(String number, String name) throws IOException {
        User        user            = new User();
        Syllabus    syllabus        = new Syllabus();
        Map<String, Model> answer   = new HashMap<>();
        String      accessUrl = accessUrlHead + SyllabusPage;
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
        /* Get search option */
        getSearchOptions(html, syllabus, "id=\"xnd\"", "学年第");

        /* Get student information */
        Pattern patternA = Pattern.compile("学院：(.*)</span>");
        Pattern patternB = Pattern.compile("行政班：(.*)</span>");
        Matcher matcherA = patternA.matcher(html);
        Matcher matcherB = patternB.matcher(html);

        if (matcherA.find() && matcherB.find()) {
            user.setInstitute(matcherA.group(1));
            user.setClas(matcherB.group(1));
            user.setEnrollment(Integer.parseInt("20" + user.getClas().substring(0, 2)));
        } else {
            return null;
        }

        /* Get syllabus information */
        Pattern patternC = Pattern.compile("<td align=\"Center\" rowspan=\"\\d+\"" +
                "( width=\"\\d+%\"){0,1}>(.*?)<br>(.*?)<br>(.*?)<br>(.*?)(</td>|<br>)");
        Matcher matcherC = patternC.matcher(html);
        while (matcherC.find()) {
            Course course = new Course();
            course.setName(matcherC.group(2));
            analysisCourseTime(course, matcherC.group(3));
            course.setTeacher(matcherC.group(4));
            course.setAddress(matcherC.group(5));

            syllabus.append(course);
        }

        answer.put("user", user);
        answer.put("syllabus", syllabus);
        return answer;
    }

    private void analysisCourseTime(Course course, String timeString) throws IOException {
        Map<String, Integer> weekMap = new HashMap<String, Integer>() {{
            put(URLEncoder.encode("一", "GBK"), 1);
            put(URLEncoder.encode("二", "GBK"), 2);
            put(URLEncoder.encode("三", "GBK"), 3);
            put(URLEncoder.encode("四", "GBK"), 4);
            put(URLEncoder.encode("五", "GBK"), 5);
            put(URLEncoder.encode("六", "GBK"), 6);
            put(URLEncoder.encode("日", "GBK"), 7);
        }};

        Pattern pattern = Pattern.compile("周(.*)第(\\d+),(.*?)(\\d+)节\\{第(\\d+)-(\\d+)周\\}");
        Matcher matcher = pattern.matcher(timeString);

        if (matcher.find()) {
            course.setWeek(weekMap.get(URLEncoder.encode(matcher.group(1), "GBK")));
            course.setBegin(Integer.parseInt(matcher.group(2)));
            course.setEnd(Integer.parseInt(matcher.group(4)));
            course.setWeekBegin(Integer.parseInt(matcher.group(5)));
            course.setWeekEnd(Integer.parseInt(matcher.group(6)));
        }
    }

}
