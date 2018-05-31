package com.qb.xrealsys.ifafu.CommentTeacher.web

import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.Base.web.WebInterface
import com.qb.xrealsys.ifafu.Tool.HttpHelper
import com.qb.xrealsys.ifafu.Tool.ZFVerify
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        val response    = request.Get(header)
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

        return Response(true, 0, "评教成功")
    }
}