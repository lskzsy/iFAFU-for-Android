package com.qb.xrealsys.ifafu.Score.web;

import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Score.model.MakeupExam;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Tool.HttpHelper;
import com.qb.xrealsys.ifafu.Tool.HttpResponse;
import com.qb.xrealsys.ifafu.Base.web.WebInterface;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 02/03/2018.
 */

public class MakeupInterface extends WebInterface {

    private String MakeupExamPage = "xsbkkscx.aspx";

    public MakeupInterface(String inHost, UserAsyncController userController) {
        super(inHost, userController);
    }

    public void InitMakeupExam(String number, String name) throws IOException {
        String accessUrl = makeAccessUrlHead() + MakeupExamPage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121617";

        Map<String, String> header = GetRefererHeader(number);
        HttpHelper request = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Get(header);
        if (response.getStatus() == 200) {
            String html = response.getResponse();
            setViewParams(html);
        }
    }

    public MakeupExam GetMakeupExam(String number, String name, final Score score) throws IOException {
        MakeupExam makeupExam = new MakeupExam();

        String accessUrl = makeAccessUrlHead() + MakeupExamPage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121617";

        Map<String, String> header = new HashMap<>();
        header.put("Referer", accessUrl);
        Map<String, String> postData = new HashMap<String, String>() {{
            put("__EVENTTARGET", "xqd");
            put("__EVENTARGUMENT", "");
            put("__VIEWSTATE", URLEncoder.encode(viewState, "gbk"));
            put("__VIEWSTATEGENERATOR", viewStateGenerator);

            String[] time = nextTerm(score.getYear(), score.getTerm());
            put("xnd", time[0]);
            put("xqd", time[1]);
        }};
        HttpHelper request = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Post(header, postData, false);
        if (response.getStatus() == 200) {
            String html = response.getResponse();
            if (!LoginedCheck(html)) {
                return GetMakeupExam(number, name, score);
            }

            Pattern pattern = Pattern.compile("<td( height=\"\\d+\")?>.*?</td><td>(.*?)/td>" +
                    "<td>.*?</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>");
            String  formHtml = html.substring(html.indexOf("formbox"));
            Matcher matcher = pattern.matcher(formHtml);
            while (matcher.find()) {
                if (matcher.group(2).contains(score.getCourseName())) {
                    makeupExam.setCourseName(getRealStringData(matcher.group(2)));
                    makeupExam.setTime(getRealStringData(matcher.group(3)));
                    makeupExam.setLocation(getRealStringData(matcher.group(4)));
                    makeupExam.setSeatNumber(getRealStringData(matcher.group(5)));
                    makeupExam.setMethod(getRealStringData(matcher.group(6)));
                    break;
                }
            }
        }

        return makeupExam;
    }

    private String[] nextTerm(String year, String term) {
        if (term.contains("1")) {
            return new String[] {year, "2"};
        } else {
            Matcher matcher = Pattern.compile("(\\d+)-(\\d+)").matcher(year);
            if (matcher.find()) {
                int lyear = Integer.parseInt(matcher.group(1));
                int ryear = Integer.parseInt(matcher.group(2));
                String format =  String.format(
                        Locale.getDefault(),
                        "%d-%d",
                        lyear + 1,
                        ryear + 1);
                return new String[] {format, "1"};
            } else {
                return new String[] {year, term};
            }
        }
    }
}
