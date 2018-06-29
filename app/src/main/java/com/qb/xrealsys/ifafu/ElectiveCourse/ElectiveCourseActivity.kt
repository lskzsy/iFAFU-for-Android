package com.qb.xrealsys.ifafu.ElectiveCourse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseController
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.ElectiveCourseSearchDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.UpdateElectiveCourseListViewDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.dialog.ElectiveCourseFilterDialog
import com.qb.xrealsys.ifafu.ElectiveCourse.dialog.ElectiveCourseSearchDialog
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse
import com.qb.xrealsys.ifafu.MainApplication
import com.qb.xrealsys.ifafu.R
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import java.util.*
import kotlin.collections.HashMap

class ElectiveCourseActivity : AppCompatActivity(), TitleBarButtonOnClickedDelegate, UpdateElectiveCourseListViewDelegate, View.OnClickListener, ElectiveCourseSearchDelegate {

    private var mainApplication: MainApplication? = null

    private var userController: UserAsyncController? = null

    private var titleBarController: TitleBarController? = null

    private var electiveCourseController: ElectiveCourseController? = null

    private var mapTitleToIcon: MutableMap<String, Int>? = null

    private var electiveCourseListView: ListView? = null

    private var queryBtn: Button? = null

    private var filterBtn: Button? = null

    private var searchDialog: ElectiveCourseSearchDialog? = null

    private var filterDialog: ElectiveCourseFilterDialog? = null

    private var noDataView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elective_course)

        this.mainApplication            = this.application as MainApplication
        this.userController             = this.mainApplication!!.userController
        this.electiveCourseController   = this.mainApplication!!.electiveCourseController
        this.electiveCourseController!!.updateDelegate = this
        this.titleBarController         = TitleBarController(this)
        this.titleBarController!!
                .setHeadBack()
                .setTwoLineTitle("选修课管理", String.format(
                        Locale.getDefault(), "%s(%s)",
                        this.userController!!.data.name,
                        this.userController!!.data.account))
                .setOnClickedListener(this)

        initAllElements()
        initData()
        this.electiveCourseController!!.getIndex()
        titleBarController!!.setRightProgress(View.VISIBLE)
    }

    private fun initAllElements() {
        electiveCourseListView = findViewById(R.id.electiveCourseList)
        noDataView = findViewById(R.id.noDataView)
        queryBtn = findViewById(R.id.queryBtn)
        filterBtn = findViewById(R.id.filterBtn)
        filterBtn!!.setOnClickListener(this)
        queryBtn!!.setOnClickListener(this)
    }

    private fun initData() {
        this.mapTitleToIcon = object : java.util.HashMap<String, Int>() {
            init {
                put(getString(R.string.display_elective_score_nature), R.drawable.icon_nature_elective)
                put(getString(R.string.display_elective_score_social), R.drawable.icon_social_elective)
                put(getString(R.string.display_elective_score_art), R.drawable.icon_art_elective)
                put(getString(R.string.display_elective_score_letter), R.drawable.icon_letter_elective)
                put(getString(R.string.display_elective_score_innovate), R.drawable.icon_innovate_elective)
            }
        }
        this.searchDialog = ElectiveCourseSearchDialog(this, this)
        this.filterDialog = ElectiveCourseFilterDialog(this, electiveCourseController!!.getFilter())
    }

    override fun titleBarOnClicked(id: Int) {
        when (id) {
            R.id.headback -> finish()
        }
    }

    override fun updateElectiveCourseList(electiveCourseList: MutableList<ElectiveCourse>) {
        runOnUiThread {
            val adaptData = ArrayList<Map<String, Any>>()
            titleBarController!!.setRightProgress(View.INVISIBLE)
            if (electiveCourseList.size < 1) {
                noDataView!!.visibility = View.VISIBLE
            } else {
                noDataView!!.visibility = View.INVISIBLE
            }

            for (electiveCourse in electiveCourseList) {
                val map = HashMap<String, Any>()
                map["icon"] = this.mapTitleToIcon!![electiveCourse.owner]!!
                map["name"] = electiveCourse.name!!
                map["location"] = "${electiveCourse.location}(${electiveCourse.campus})"
                map["teacher"] = electiveCourse.teacher!!
                map["score"] = "学分: ${electiveCourse.studyScore}"
                map["have"] = "剩余: ${electiveCourse.have}/${electiveCourse.allHave}"
                map["btn"] = "取消预订"
                adaptData.add(map)
            }

            val simpleAdapter = SimpleAdapter(
                    this,
                    adaptData,
                    R.layout.gadget_item_elective_course,
                    arrayOf("icon", "name", "location", "teacher", "score", "have", "btn"),
                    intArrayOf(
                            R.id.electiveCourseItemIcon,
                            R.id.electiveCourseName,
                            R.id.electiveCourseLocation,
                            R.id.electiveCourseTeacher,
                            R.id.electiveCourseScore,
                            R.id.electiveCourseHave,
                            R.id.electiveCourseBtn))
            electiveCourseListView!!.adapter = simpleAdapter
        }
    }

    override fun errorElectiveCourseList(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        if (!this.titleBarController!!.isRightProgressRunning) {
            when (v!!.id) {
                R.id.queryBtn -> {
                    this.searchDialog!!.show()
                }
                R.id.filterBtn-> {
                    this.filterDialog!!.show()
                }
            }
        }
    }

    override fun searchElectiveCourse(courseName: String) {
        this.searchDialog!!.cancel()
        titleBarController!!.setRightProgress(View.VISIBLE)
//        Toast.makeText(this, "搜索\"$courseName\"", Toast.LENGTH_SHORT).show()
        electiveCourseController!!.searchByName(courseName)
    }
}
