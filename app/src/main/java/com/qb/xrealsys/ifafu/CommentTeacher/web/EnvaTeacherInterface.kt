package com.qb.xrealsys.ifafu.CommentTeacher.web

import android.util.Log
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.Base.web.WebInterface
import com.qb.xrealsys.ifafu.Tool.HttpHelper
import com.qb.xrealsys.ifafu.Tool.ZFVerify
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.HashMap

/**
 * Created by sky on 15/05/2018.
 */
class EnvaTeacherInterface(
        inHost: String?,
        userController: UserAsyncController?): WebInterface(inHost, userController) {

    private val envaTeacherPage: String = "xsjxpj2fafu.aspx"

    fun accessEnvaTeacher(number: String, name: String): Response {
        var accessUrl: String = makeAccessUrlHead() + envaTeacherPage
        accessUrl += "?xh=$number"
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk")
        accessUrl += "&gnmkdm=" + "N121400"

        val header      = GetRefererHeader(number)
        val request     = HttpHelper(accessUrl, "gbk")
        var response    = request.Get(header)
        if (response.status != 200) {
            return Response(false, -1, "网络异常")
        }

        val html = response.response
        if (!LoginedCheck(html)) {
            return accessEnvaTeacher(number, name)
        }

        val pattern: Pattern = Pattern.compile("alert\\('(.*?)'\\)")
        val matcher: Matcher = pattern.matcher(html)
        if (matcher.find()) {
            return Response(false, -2, matcher.group(1))
        }

        val patternList: Pattern = Pattern.compile("open\\('(.*?)',")
        val matcherList: Matcher = patternList.matcher(html)
//        val teacherList: MutableList<String> = ArrayList()
        while (matcherList.find()) {
//            teacherList.add(matcherList.group(1))
            Thread.sleep(300)
            if (!commentTeacher(matcherList.group(1))) {
                return Response(false, -3, "网络异常")
            }
        }

        /* Get view params */
        setViewParams(html)

        val postData: MutableMap<String, String> = HashMap()
        postData["__EVENTARGUMENT"] = ""
        postData["__EVENTTARGET"] = ""
        postData["__VIEWSTATE"] = URLEncoder.encode(viewState, "gbk")
        postData["__VIEWSTATEGENERATOR"] = viewStateGenerator
        postData["btn_tj"] = "+%CC%E1+%BD%BB+"
        response = request.Post(header, postData, false)

        when (response.status != 200) {
            true    -> return Response(false, -4, "网络异常")
            false   -> {
                val m: Matcher = Pattern.compile("alert\\('(.*?)'\\)").matcher(response.response)
                when (m.find() && m.group(1).contains("完成评价")) {
                    true    -> return Response(true, 0, "评教成功，请在主界面下拉刷新数据")
                    false   -> return Response(false, -5, m.group(1))
                }
            }
        }
    }

    fun commentTeacher(path: String): Boolean {
        val accessUrl: String = makeAccessUrlHead() + path
        val random      = Random()
        val request     = HttpHelper(accessUrl, "gbk")
        var response    = request.Get()
        if (response.status != 200) {
            return false
        }

        val html: String = response.response
        /* Get view params */
        setViewParams(html)

        val postData: MutableMap<String, String> = HashMap()
        val header: MutableMap<String, String> = HashMap()
        val pattern: Pattern = Pattern.compile("table id=\"Datagrid1__(.*?)_rb\"")
        val matcher: Matcher = pattern.matcher(html)
        while (matcher.find()) {
            if (random.nextInt(100) > 10) {
                postData["Datagrid1%3A_" + matcher.group(1) + "%3Arb"] = "94"
            } else {
                postData["Datagrid1%3A_" + matcher.group(1) + "%3Arb"] = "82"
            }
        }
        postData["Datagrid1%3A_" + String.format("ctl%d", 4 + random.nextInt(2)) + "%3Arb"] = "94"
        postData["Datagrid1%3A_" + String.format("ctl%d", 2 + random.nextInt(2)) + "%3Arb"] = "82"
        postData["__VIEWSTATE"] = URLEncoder.encode(viewState, "gbk")
        postData["__VIEWSTATEGENERATOR"] = viewStateGenerator
        postData["txt_pjxx"] = ""
        postData["Button1"] = "+%CC%E1+%BD%BB+"

        header["Host"] = "jwgl.fafu.edu.cn"
        response = request.Post(header, postData, false)
        Log.d("debug", accessUrl)
//        Log.d("debug", response.response)
        val m: Matcher = Pattern.compile("script>alert\\('(.*?)'\\)").matcher(response.response)
        if (m.find()) {
            postData.remove("__VIEWSTATE")
            Log.d("debug", postData.toString())
            Log.d("debug", m.group(1))
        } else {
            Log.d("debug", response.response.contains("提交成功").toString())
        }

        return response.status == 200 && response.response.contains("提交成功")
    }
}