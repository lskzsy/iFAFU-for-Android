package com.qb.xrealsys.ifafu.ElectiveCourse.web

import android.util.Log
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.Base.web.WebInterface
import com.qb.xrealsys.ifafu.DB.ElectiveCourseTask
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourseList
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveFilter
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveTask
import com.qb.xrealsys.ifafu.R
import com.qb.xrealsys.ifafu.Tool.HttpHelper
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import java.net.URLEncoder
import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class ElectiveCourseInterface (
        inHost: String?,
        userController: UserAsyncController?): WebInterface(inHost, userController) {

    private val electiveCoursePage: String = "xf_xsqxxxk.aspx"

    private val patternOptions: Pattern = Pattern.compile("<option( selected=\"selected\"){0,1} value=\"(.*)\">")

    private val patternCourseList: Pattern = Pattern.compile("name=\"(.*?)\".*?window\\.open\\('(.*?)'\\)\">(.*?)" +
            "</a></td><td>(.*?)<.*?window\\.open\\('(.*?)'\\)\">(.*?)</a></td><td( title=\"(.*?)\")?.*?<td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)" +
            "</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>")

    private val patternElectived: Pattern = Pattern.compile("<td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>" +
            "<td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>.*?doPostBack\\('(.*?)'")

    private val patternPage: Pattern = Pattern.compile("第.*?(\\d+).*?页/共.*?(\\d+).*?页")

    fun getElectiveCourseIndex(number: String, name: String, courseList: ElectiveCourseList): Response {
        var accessUrl: String = makeAccessUrlHead() + electiveCoursePage
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
            return getElectiveCourseIndex(number, name, courseList)
        }

        val pattern: Pattern = Pattern.compile("alert\\('(.*?)'\\)")
        val matcher: Matcher = pattern.matcher(html)
        if (matcher.find()) {
            return Response(false, 0, matcher.group(1))
        }

        if (html.contains("防刷")) {
            return Response(false, 0, "")
        }

        setViewParams(html)
        analysisFilter(html, courseList.filter)
        analysisElectiveCourseList(html, courseList)

        return Response(true, 0, "获取成功")
    }

    fun disElectiveCourse(number: String, name: String, electiveCourseList: ElectiveCourseList, courseIndex: String): Response {
        var accessUrl: String = makeAccessUrlHead() + electiveCoursePage
        accessUrl += "?xh=$number"
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk")
        accessUrl += "&gnmkdm=" + "N121203"

        val filter: ElectiveFilter = electiveCourseList.filter
        val request = HttpHelper(accessUrl, "gbk")
        val postData = HashMap<String, String>()
        postData["__EVENTTARGET"] = courseIndex.replace("$", "%3A")
        postData["__EVENTARGUMENT"] = ""
        postData["__VIEWSTATE"] = URLEncoder.encode(this.viewState, "gbk")
        postData["__VIEWSTATEGENERATOR"] = this.viewStateGenerator
        postData["ddl_kcxz"] = URLEncoder.encode(filter.getCourseNature(), "gbk")
        postData["ddl_ywyl"] = URLEncoder.encode(filter.getCourseHave(), "gbk")
        postData["ddl_kcgs"] = URLEncoder.encode(filter.getCourseOwner(), "gbk")
        postData["ddl_xqbs"] = filter.getCourseCampus()
        postData["ddl_sksj"] = URLEncoder.encode(filter.getCourseTime(), "gbk")
        var courseName = ""
        if (filter.courseNameFilter != null) {
            courseName = filter.courseNameFilter!!
        }
        postData["TextBox1"] = URLEncoder.encode(courseName, "gbk")
        postData["dpkcmcGrid%3AtxtChoosePage"] = electiveCourseList.curPage.toString()
        postData["dpkcmcGrid%3AtxtPageSize"] = "15"

        val response = request.Post(postData, false)
        if (response.status != 200) {
            return Response(false, 0, R.string.error_system)
        }

        val html = response.response
        if (!LoginedCheck(html)) {
            return electiveCourse(number, name, electiveCourseList, courseIndex)
        }
        setViewParams(html)
        analysisElectivedCourseList(html, electiveCourseList)

        val pattern: Pattern = Pattern.compile("alert\\('(.*?)'\\)")
        val matcher: Matcher = pattern.matcher(html)
        if (matcher.find()) {
            return Response(true, 0, matcher.group(1))
        } else {
            return Response(true, 0, "未知返回")
        }
    }

    fun electiveCourse(number: String, name: String, task: ElectiveTask): Response {
        var accessUrl: String = makeAccessUrlHead() + electiveCoursePage
        accessUrl += "?xh=$number"
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk")
        accessUrl += "&gnmkdm=" + "N121203"

        val request = HttpHelper(accessUrl, "gbk")
        val postData = HashMap<String, String>()
        postData["__EVENTTARGET"] = ""
        postData["__EVENTARGUMENT"] = ""
        postData["__VIEWSTATE"] = URLEncoder.encode(task.viewState!!, "gbk")
        postData["__VIEWSTATEGENERATOR"] = task.viewStateGenerator!!
        postData["ddl_kcxz"] = URLEncoder.encode(task.natureFilter!!, "gbk")
        postData["ddl_ywyl"] = URLEncoder.encode(task.haveFilter!!, "gbk")
        postData["ddl_kcgs"] = URLEncoder.encode(task.ownerFilter!!, "gbk")
        postData["ddl_xqbs"] = task.campusFilter!!
        postData["ddl_sksj"] = URLEncoder.encode(task.timeFilter!!, "gbk")
        var courseName = ""
        if (task.nameFilter != null) {
            courseName = task.nameFilter!!
        }
        postData["TextBox1"] = URLEncoder.encode(courseName, "gbk")
        postData["dpkcmcGrid%3AtxtChoosePage"] = task.curPage!!
        postData["dpkcmcGrid%3AtxtPageSize"] = "15"
        postData[URLEncoder.encode(task.courseIndex!!, "gbk")] = "on"
        postData["Button1"] = "++%CC%E1%BD%BB++"

        val response = request.Post(postData, false)
        if (response.status != 200) {
            return Response(false, 0, R.string.error_system)
        }

        val html = response.response
        if (!LoginedCheck(html)) {
            return electiveCourse(number, name, task)
        }

        val pattern: Pattern = Pattern.compile("alert\\('(.*?)'\\)")
        val matcher: Matcher = pattern.matcher(html)
        if (matcher.find()) {
            Log.d("ElectiveCourseTask", "[${task.courseName}]${matcher.group(1)}")
            return Response(false, 0, matcher.group(1))
        } else {
            return Response(true, 0, "选课成功")
        }
    }

    fun electiveCourse(number: String, name: String, electiveCourseList: ElectiveCourseList, courseIndex: String): Response {
        var accessUrl: String = makeAccessUrlHead() + electiveCoursePage
        accessUrl += "?xh=$number"
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk")
        accessUrl += "&gnmkdm=" + "N121203"

        val filter: ElectiveFilter = electiveCourseList.filter
        val request = HttpHelper(accessUrl, "gbk")
        val postData = HashMap<String, String>()
        postData["__EVENTTARGET"] = ""
        postData["__EVENTARGUMENT"] = ""
        postData["__VIEWSTATE"] = URLEncoder.encode(this.viewState, "gbk")
        postData["__VIEWSTATEGENERATOR"] = this.viewStateGenerator
        postData["ddl_kcxz"] = URLEncoder.encode(filter.getCourseNature(), "gbk")
        postData["ddl_ywyl"] = URLEncoder.encode(filter.getCourseHave(), "gbk")
        postData["ddl_kcgs"] = URLEncoder.encode(filter.getCourseOwner(), "gbk")
        postData["ddl_xqbs"] = filter.getCourseCampus()
        postData["ddl_sksj"] = URLEncoder.encode(filter.getCourseTime(), "gbk")
        var courseName = ""
        if (filter.courseNameFilter != null) {
            courseName = filter.courseNameFilter!!
        }
        postData["TextBox1"] = URLEncoder.encode(courseName, "gbk")
        postData["dpkcmcGrid%3AtxtChoosePage"] = electiveCourseList.curPage.toString()
        postData["dpkcmcGrid%3AtxtPageSize"] = "15"
        postData[URLEncoder.encode(courseIndex, "gbk")] = "on"
        postData["Button1"] = "++%CC%E1%BD%BB++"

        val response = request.Post(postData, false)
        if (response.status != 200) {
            return Response(false, 0, R.string.error_system)
        }

        val html = response.response
        if (!LoginedCheck(html)) {
            return electiveCourse(number, name, electiveCourseList, courseIndex)
        }
        setViewParams(html)
        analysisElectivedCourseList(html, electiveCourseList)

        val pattern: Pattern = Pattern.compile("alert\\('(.*?)'\\)")
        val matcher: Matcher = pattern.matcher(html)
        if (matcher.find()) {
            return Response(false, 0, matcher.group(1))
        } else {
            return Response(true, 0, "选课成功")
        }
    }

    fun searchElectiveCourseByName(
            number: String, name: String, courseList: ElectiveCourseList, courseName: String): Response {
        courseList.filter.courseNameFilter = courseName
        return searchElectiveCourse(
                number, name, courseList, "", "有", "", "1", "", courseName, courseList.curPage)
    }

    fun searchElectiveCourse(
            number: String, name: String, courseList: ElectiveCourseList): Response {
        val filter: ElectiveFilter = courseList.filter
        var courseName= ""
        if (filter.courseNameFilter != null) {
            courseName = filter.courseNameFilter!!
        }
        return searchElectiveCourse(
                number, name, courseList,
                filter.getCourseNature(),
                filter.getCourseHave(),
                filter.getCourseOwner(),
                filter.getCourseCampus(),
                filter.getCourseTime(),
                courseName,
                courseList.curPage)
    }

    fun searchElectiveCourse(
            number: String, name: String, courseList: ElectiveCourseList,
            nature: String, have: String, owner: String, campus: String,
            time: String, courseName: String, curPage: Int): Response {
        var accessUrl: String = makeAccessUrlHead() + electiveCoursePage
        accessUrl += "?xh=$number"
        accessUrl += "&xm=" + URLEncoder.encode(name, "gbk")
        accessUrl += "&gnmkdm=" + "N121203"

        val request = HttpHelper(accessUrl, "gbk")
        val postData = HashMap<String, String>()
        postData["__EVENTTARGET"] = "ddl_kcgs"
        postData["__EVENTARGUMENT"] = ""
        postData["__VIEWSTATE"] = URLEncoder.encode(this.viewState, "gbk")
        postData["__VIEWSTATEGENERATOR"] = this.viewStateGenerator
        postData["ddl_kcxz"] = URLEncoder.encode(nature, "gbk")
        postData["ddl_ywyl"] = URLEncoder.encode(have, "gbk")
        postData["ddl_kcgs"] = URLEncoder.encode(owner, "gbk")
        postData["ddl_xqbs"] = campus
        postData["ddl_sksj"] = URLEncoder.encode(time, "gbk")
        postData["TextBox1"] = URLEncoder.encode(courseName, "gbk")
        postData["dpkcmcGrid%3AtxtChoosePage"] = curPage.toString()
        postData["dpkcmcGrid%3AtxtPageSize"] = "15"

        val response = request.Post(postData, false)
        if (response.status != 200) {
            return Response(false, 0, R.string.error_system)
        }

        val html = response.response
        if (!LoginedCheck(html)) {
            return searchElectiveCourse(number, name, courseList, nature, have, owner, campus, time, courseName, curPage)
        }

        setViewParams(html)
        analysisElectiveCourseList(html, courseList)

        return Response(true, 0, "获取成功")
    }

    private fun analysisElectiveCourseList(html: String, electiveCourseList: ElectiveCourseList) {
        var content: String = html.replace("\r", "")
        content = content.replace("\n", "")
        electiveCourseList.courses.clear()

        val courseListIndex: Int = content.indexOf("kcmcGrid__ctl2_xk")
        if (courseListIndex >= 0) {
            val electiveListIndex: Int = content.indexOf("已选课程")
            val courseListContent: String = content.substring(courseListIndex, electiveListIndex)
            val matcherCourseList: Matcher = patternCourseList.matcher(courseListContent)
            while (matcherCourseList.find()) {
                val course = ElectiveCourse()
                course.courseIndex = matcherCourseList.group(1)
                course.name = matcherCourseList.group(3)
                course.code = matcherCourseList.group(4)
                course.teacher = matcherCourseList.group(6)
                if (matcherCourseList.group(7) == null) {
                    course.time = ""
                } else {
                    course.time = getRealStringData(matcherCourseList.group(8))
                }
                course.location = getRealStringData(matcherCourseList.group(9))
                course.studyScore = getRealFloatData(matcherCourseList.group(10))
                course.weekStudyTime = matcherCourseList.group(11)
                course.weekTime = matcherCourseList.group(12)
                course.allHave = matcherCourseList.group(13).toInt()
                course.have = matcherCourseList.group(14).toInt()
                course.owner = matcherCourseList.group(15)
                course.nature = matcherCourseList.group(16)
                course.campus = matcherCourseList.group(17)
                course.college = matcherCourseList.group(18)
                course.examTime = getRealStringData(matcherCourseList.group(19))
                electiveCourseList.courses.add(course)
            }

            val matcherPage: Matcher = patternPage.matcher(html)
            if (matcherPage.find()) {
                electiveCourseList.curPage = matcherPage.group(1).toInt()
                electiveCourseList.pageSize = matcherPage.group(2).toInt()
            }
        }

        analysisElectivedCourseList(html, electiveCourseList)
    }

    private fun analysisElectivedCourseList(html: String, electiveCourseList: ElectiveCourseList) {
        val electivedContent: String = html.substring(html.indexOf("已选课程"))
        val matcherElectived = patternElectived.matcher(electivedContent)
        electiveCourseList.electived.clear()
        while (matcherElectived.find()) {
            val electived = ElectiveCourse()
            electived.name = matcherElectived.group(1)
            electived.teacher = matcherElectived.group(2)
            electived.studyScore = getRealFloatData(matcherElectived.group(3))
            electived.weekStudyTime = matcherElectived.group(4)
            electived.weekTime = matcherElectived.group(5)
            electived.campus = matcherElectived.group(6)
            electived.time = matcherElectived.group(7)
            electived.location = matcherElectived.group(8)
            electived.owner = matcherElectived.group(10)
            electived.nature = matcherElectived.group(11)
            electived.courseIndex = matcherElectived.group(13)
            electiveCourseList.electived.add(electived)
        }
    }

    private fun analysisFilter(html: String, filter: ElectiveFilter) {
        val natureStrIndex: Int = html.indexOf("课程性质：")
        val haveStrIndex: Int = html.indexOf("有无余量：")
        val ownerStrxIndex: Int = html.indexOf("课程归属：")
        val campusStrIndex: Int = html.indexOf("上课校区：")
        val timeStrIndex: Int = html.indexOf("上课时间：")

        /* Get nature filter options */
        filter.courseNatureIndex = getFilterOptions(
                html.substring(natureStrIndex, haveStrIndex),
                filter.courseNature)

        /* Get have filter options */
        filter.isFreeIndex = getFilterOptions(
                html.substring(haveStrIndex, ownerStrxIndex),
                filter.isFree)

        /* Get owner filter options */
        filter.courseOwnerIndex = getFilterOptions(
                html.substring(ownerStrxIndex, campusStrIndex),
                filter.courseOwner)

        /* Get campus filter options */
        filter.courseCampusIndex = getFilterOptions(
                html.substring(campusStrIndex, timeStrIndex),
                filter.courseCampus)

        /* Get time filter options */
        filter.courseTimeIndex = getFilterOptions(
                html.substring(timeStrIndex),
                filter.courseTime)
    }

    private fun getFilterOptions(filterContent: String, optionList: MutableList<String>): Int {
        optionList.clear()
        var index = 0
        val filterOptions: Matcher = patternOptions.matcher(filterContent)
        while (filterOptions.find()) {
            optionList.add(filterOptions.group(2))
            if (filterOptions.group(1) != null) {
                index = optionList.size - 1
            }
        }

        return index
    }
}