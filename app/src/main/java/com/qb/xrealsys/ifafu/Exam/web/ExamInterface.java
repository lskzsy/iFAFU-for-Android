package com.qb.xrealsys.ifafu.Exam.web;

import com.qb.xrealsys.ifafu.User.controller.UserController;
import com.qb.xrealsys.ifafu.Exam.model.Exam;
import com.qb.xrealsys.ifafu.Exam.model.ExamTable;
import com.qb.xrealsys.ifafu.Tool.HttpHelper;
import com.qb.xrealsys.ifafu.Tool.HttpResponse;
import com.qb.xrealsys.ifafu.Base.web.WebInterface;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 24/02/2018.
 */

public class ExamInterface extends WebInterface {

    private String ExamPage = "xskscx.aspx";

    public ExamInterface(String inHost, UserController userController) {
        super(inHost, userController);
    }

    public ExamTable GetExamTable(String number, String name) throws IOException {
        ExamTable examTable = new ExamTable();

        String accessUrl = makeAccessUrlHead() + ExamPage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121604";

        Map<String, String> header = GetRefererHeader(number);
        HttpHelper request  = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Get(header);

        if (response.getStatus() != 200) {
            return null;
        }

        String html = response.getResponse();
        if (!LoginedCheck(html)) {
            return GetExamTable(number, name);
        }

        /* Get view params */
        setViewParams(html);

        /* Get options for searching */
        getSearchOptions(html, examTable, "id=\"xnd\"", "学年第");

        /* Get examTable */
        String examTableStr = html.substring(html.indexOf("校区"));
        analysisExam(examTableStr, examTable.getData());

        return examTable;
    }

    private void analysisExam(String html, List<Exam> examList) {
        Pattern pattern = Pattern.compile("<td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>" +
                "<td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            Exam exam = new Exam();
            exam.setId(getRealStringData(matcher.group(1)));
            exam.setName(getRealStringData(matcher.group(2)));
            exam.setDatetime(getRealStringData(matcher.group(4)));
            exam.setAddress(getRealStringData(matcher.group(5)));
            exam.setSeatNumber(getRealStringData(matcher.group(7)));
            exam.setCampus(getRealStringData(matcher.group(8)));
            examList.add(exam);
        }
    }
}
