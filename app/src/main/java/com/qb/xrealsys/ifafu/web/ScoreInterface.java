package com.qb.xrealsys.ifafu.web;

import com.qb.xrealsys.ifafu.model.Score;
import com.qb.xrealsys.ifafu.model.ScoreTable;
import com.qb.xrealsys.ifafu.tool.GlobalLib;
import com.qb.xrealsys.ifafu.tool.HttpHelper;
import com.qb.xrealsys.ifafu.tool.HttpResponse;

import java.io.IOException;
import java.net.URLEncoder;
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

    public ScoreInterface(String inHost, String inToken) {
        super(inHost, inToken);
    }

    public void updateScoreTable(
            ScoreTable scoreTable,
            String number,
            String name,
            final String year,
            final String term) throws IOException {
        String accessUrl = accessUrlHead + ScorePage;
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
        if (response.getStatus() == 200) {
            String html = response.getResponse();
            List<Score> scoreList = scoreTable.getData();
            scoreList.clear();
            analysisScore(html, scoreList);
        }
    }

    public ScoreTable GetScoreTable(String number, String name) throws IOException {
        ScoreTable scoreTable = new ScoreTable();

        String accessUrl = accessUrlHead + ScorePage;
        accessUrl += "?xh=" + number;
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk");
        accessUrl += "&gnmkdm=" + "N121605";

        Map<String, String> header = GetRefererHeader(number);
        HttpHelper request  = new HttpHelper(accessUrl, "gbk");
        HttpResponse response = request.Get(header);

        if (response.getStatus() != 200) {
            return null;
        }

        String html = response.getResponse();

        /* Get view params */
        setViewParams(html);

        /* Get options for searching */
        getSearchOptions(html, scoreTable, "学年：", "学期：");

        /* Get scoreTable */
        List<Score> scoreList = scoreTable.getData();
        analysisScore(html, scoreList);

        return scoreTable;
    }

    private void analysisScore(String html, List<Score> scoreList) throws IOException {
        int scoreTableBegin = html.indexOf("补考备注");
        int scoreTableEnd   = html.indexOf("footbox");
        String ScoreTable   = html.substring(scoreTableBegin, scoreTableEnd);
        Pattern patternB    = Pattern.compile("<td>(.*)</td><td>(.*)</td><td>(.*)</td>" +
                "<td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td>" +
                "<td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td>");
        Matcher matcherScore  = patternB.matcher(ScoreTable);
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

    private float getRealFloatData(String srcData) {
        if (srcData.equals("&nbsp;")) {
            return (float) 0.0;
        } else {
            return Float.parseFloat(srcData);
        }
    }

    private String getRealStringData(String srcData) {
        if (srcData.equals("&nbsp;")) {
            return "";
        } else {
            return srcData;
        }
    }
}
