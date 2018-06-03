package com.qb.xrealsys.ifafu.Card.web

import android.graphics.Bitmap
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.Tool.HttpHelper
import com.qb.xrealsys.ifafu.Tool.HttpResponse
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class CardInterface {

    private val host: String = "http://auth.fafu.edu.cn"

    private val verifyUrl: String = "/authserver/captcha.html"

    private val loginUrl: String = "/authserver/login?service=http%3A%2F%2Fapp.fafu.edu.cn%2Fpublicapp%2Fsys%2Fmyyktzd%2Fmobile%2FoneCard%2Findex.html#!/"

    private var postLoginUrl: String? = null

    private var paramLt: String? = null

    init {

    }

    fun getCredit(): Response {
        val accessUrl: String = "http://app.fafu.edu.cn/publicapp/sys/myyktzd/api/getOverviewInfo.do"
        try {
            val response: HttpResponse = HttpHelper(accessUrl).GetWithoutRedirect()
            if (response.status == 200) {
                val jsonObject = JSONObject(response.response)
                val datas: JSONObject = jsonObject.optJSONObject("datas")
                val credit: Double = datas.optDouble("KNYE")
                return Response(true, 0, String.format("%.2f", credit))
            } else {
                return Response(false, -2, "登录失效")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Response(false, -3, e.message)
        } catch (e: JSONException) {
            e.printStackTrace()
            return Response(false, -2, "登录失效")
        }
    }

    fun getVerifyCode(): Bitmap? {
        var bitmap: Bitmap? = null
        val accessUrl: String = this.host + this.verifyUrl
        try {
            val request = HttpHelper(accessUrl)
            bitmap = request.GetHttpGragh()
            //            Log.d("debug", bitmap.toString());
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    fun initRouter(): Boolean {
        val accessUrl: String = this.host + loginUrl
        try {
            val response: HttpResponse = HttpHelper(accessUrl).GetWithoutRedirect()
            if (response.status == 302) {
                return false
            } else if (response.status == 200) {
                val html: String = response.response
                val matcherA: Matcher = Pattern.compile("action=\"(.*?)\"").matcher(html)
                if (matcherA.find()) {
                    postLoginUrl = matcherA.group(1)
                }
                val matcherB: Matcher = Pattern.compile("name=\"lt\" value=\"(.*?)\"").matcher(html)
                if (matcherB.find()) {
                    paramLt = matcherB.group(1)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }

    fun login(account: String, password: String, verifyCode: String): Response {
        val accessUrl: String = this.host + postLoginUrl

        val postData: MutableMap<String, String> = HashMap()
        postData["username"] = account
        postData["password"] = password
        postData["captchaResponse"] = verifyCode
        postData["lt"] = paramLt as String
        postData["dllt"] = "userNamePasswordLogin"
        postData["execution"] = "e1s1"
        postData["_eventId"] = "submit"
        postData["rmShown"] = "1"

        try {
            val response: HttpResponse = HttpHelper(accessUrl).Post(postData)
            if (response.status == 200) {
                val matcherError: Matcher =
                        Pattern.compile("id=\"errorMsg\" style=\"display: none;\">(.*?)</span>")
                                .matcher(response.response)
                if (matcherError.find()) {
                    return Response(false, -2, matcherError.group(1))
                } else {
                    return Response(true, 0, "登录成功")
                }
            } else {
                return Response(false, -1 * response.status, "Error")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Response(false, -1, e.message)
        }
    }
}