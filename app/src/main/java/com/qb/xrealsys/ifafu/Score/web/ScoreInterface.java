package com.qb.xrealsys.ifafu.Score.web;

import com.qb.xrealsys.ifafu.Base.model.Model;
import com.qb.xrealsys.ifafu.Base.model.Response;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Score.model.ScoreTable;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;
import com.qb.xrealsys.ifafu.Tool.HttpHelper;
import com.qb.xrealsys.ifafu.Tool.HttpResponse;
import com.qb.xrealsys.ifafu.Base.web.WebInterface;
import com.qb.xrealsys.ifafu.User.model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 14/02/2018.
 */

public class ScoreInterface extends WebInterface {

    private String ScorePage = "xscjcx_dq.aspx";

    private String TrainningPlanPage = "pyjh.aspx";

    public ScoreInterface(String inHost, UserAsyncController userController) {
        super(inHost, userController);
    }

    public List<Score> updateScoreTable(
            String number,
            String name,
            final String year,
            final String term) throws IOException {
        String accessUrl = makeAccessUrlHead() + ScorePage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121605";

        Map<String, String> header = new HashMap<>();
        header.put("Referer", accessUrl);
        Map<String, String> postData = new HashMap<String, String>() {{
            put("__EVENTTARGET", "");
            put("__EVENTARGUMENT", "");
            put("__VIEWSTATE", URLEncoder.encode(viewState, "gbk"));
            put("__VIEWSTATEGENERATOR", viewStateGenerator);
            put("ddlxn", year);
            put("ddlxq", URLEncoder.encode(term, "gbk"));
            put("btnCx", "+%B2%E9++%D1%AF+");
        }};
        HttpHelper request = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Post(header, postData, false);
        List<Score> scoreList = new ArrayList<>();
        if (response.getStatus() == 200) {
            String html = response.getResponse();
            if (!LoginedCheck(html)) {
                return updateScoreTable(number, name, year, term);
            }

            analysisScore(html, scoreList);
        }
        return scoreList;
    }

    public Map<String, Float> GetElectiveTargetScore(
            String number, String name) throws IOException {
        Map<String, Float> answer = new HashMap<>();

        String accessUrl = makeAccessUrlHead() + TrainningPlanPage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121607";

        Map<String, String> header = GetRefererHeader(number);
        HttpHelper request = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Get(header);

        if (response.getStatus() != 200) {
            return null;
        }

        String html = response.getResponse();
        if (!LoginedCheck(html)) {
            return GetElectiveTargetScore(number, name);
        }

        int oneStrBegin = html.indexOf("\"DataGrid4");
        int oneStrEnd = html.indexOf("<td>自然科学类</td>");
        String oneStr = html.substring(oneStrBegin, oneStrEnd);
        String twoStr = html.substring(oneStrEnd);
        Pattern pattern = Pattern.compile("<td>(.*)</td><td>(.*)</td>");
        Matcher matcher = pattern.matcher(oneStr);
        while (matcher.find()) {
            if (GlobalLib.CompareUtfWithGbk("任意选修课", matcher.group(1))) {
                answer.put("总学分", getRealFloatData(matcher.group(2)));
                break;
            }
        }

        matcher = pattern.matcher(twoStr);
        while (matcher.find()) {
            answer.put(matcher.group(1), getRealFloatData(matcher.group(2)));
        }

        return answer;
    }

    public Map<String, Model> GetScoreTable(String number, String name) throws IOException {
        ScoreTable          scoreTable  = new ScoreTable();
        User                user        = new User();
        Map<String, Model>  answer      = new HashMap<>();

        String accessUrl = makeAccessUrlHead() + ScorePage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121605";

        Map<String, String> header = GetRefererHeader(number);
        HttpHelper request = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Get(header);

        if (response.getStatus() != 200) {
            return null;
        }

        String html = response.getResponse();
        if (!LoginedCheck(html)) {
            return GetScoreTable(number, name);
        }

        /* Error */
        if (html.indexOf("教学质量评价") > 0) {
            answer.put("error", new Response(false, -1, "还未评教，无法获取数据"));
            return answer;
        }

        /* Get student information */
        Pattern patternA = Pattern.compile("学院：(.*)</td>");
        Pattern patternB = Pattern.compile("行政班：(.*)</td>");
        Matcher matcherA = patternA.matcher(html);
        Matcher matcherB = patternB.matcher(html);

        if (matcherA.find() && matcherB.find()) {
            user.setInstitute(matcherA.group(1));
            user.setClas(matcherB.group(1));
            user.setEnrollment(Integer.parseInt("20" + user.getClas().substring(0, 2)));
        } else {
            return null;
        }

        /* Get view params */
        setViewParams(html);

        /* Get options for searching */
        getSearchOptions(html, scoreTable, "学年：", "学期：");

        /* Get scoreTable */
        List<Score> scoreList = scoreTable.getData();
        analysisScore(html, scoreList);

        answer.put("scoreTable", scoreTable);
        answer.put("user", user);
        return answer;
    }

    private void analysisScore(String html, List<Score> scoreList) throws IOException {
        int scoreTableBegin = html.indexOf("补考备注");
        int scoreTableEnd = html.indexOf("footbox");
        String ScoreTable = html.substring(scoreTableBegin, scoreTableEnd);
        Pattern patternB = Pattern.compile("<td>(.*)</td><td>(.*)</td><td>(.*)</td>" +
                "<td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td>" +
                "<td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td>");
        Matcher matcherScore = patternB.matcher(ScoreTable);
        while (matcherScore.find()) {
            Score score = new Score();
            score.setYear(matcherScore.group(1));
            score.setTerm(matcherScore.group(2));
            score.setCourseCode(matcherScore.group(3));
            score.setCourseName(matcherScore.group(4));
            score.setCourseType(matcherScore.group(5));
            score.setCourseOwner(getRealStringData(matcherScore.group(6)));
            score.setStudyScore(Float.parseFloat(matcherScore.group(7)));
            if (GlobalLib.CompareUtfWithGbk("缓考", matcherScore.group(8))) {
                score.setDelayExam(true);
                score.setScore(0);
            } else {
                score.setDelayExam(false);
                score.setScore(Float.parseFloat(matcherScore.group(8)));
            }
            score.setMakeupScore(getRealFloatData(matcherScore.group(9)));
            score.setRestudy(!(matcherScore.group(10).equals("&nbsp;")));
            score.setInstitute(matcherScore.group(11));
            score.setScorePoint(getRealFloatData(matcherScore.group(12)));
            score.setComment(getRealStringData(matcherScore.group(13)));
            score.setMakeupComment(getRealStringData(matcherScore.group(14)));
            scoreList.add(score);
        }
    }
}
