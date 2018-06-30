package com.qb.xrealsys.ifafu.ElectiveCourse

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate
import com.qb.xrealsys.ifafu.Base.dialog.iOSDialog
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.DB.ElectiveCourseTask
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseController
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseTaskController
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.*
import com.qb.xrealsys.ifafu.ElectiveCourse.dialog.ElectiveCourseFilterDialog
import com.qb.xrealsys.ifafu.ElectiveCourse.dialog.ElectiveCourseSearchDialog
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveTask
import com.qb.xrealsys.ifafu.MainApplication
import com.qb.xrealsys.ifafu.R
import com.qb.xrealsys.ifafu.Tool.GlobalLib
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import io.realm.RealmResults
import java.util.*
import kotlin.collections.HashMap

class ElectiveCourseActivity :
        AppCompatActivity(),
        TitleBarButtonOnClickedDelegate,
        UpdateElectiveCourseListViewDelegate,
        View.OnClickListener,
        ElectiveCourseSearchDelegate,
        ElectiveCourseFilterDelegate,
        ItemButtonClickListener, ElectiveCourseCallbackDelegate, ElectiveCourseTaskDelegate {

    private var mainApplication: MainApplication? = null

    private var userController: UserAsyncController? = null

    private var titleBarController: TitleBarController? = null

    private var electiveCourseController: ElectiveCourseController? = null

    private var electiveCourseTaskController: ElectiveCourseTaskController? = null

    private var mapTitleToIcon: MutableMap<String, Int>? = null

    private var electiveCourseListView: ListView? = null

    private var queryBtn: Button? = null

    private var filterBtn: Button? = null

    private var electivedBtn: Button? = null

    private var nextPageBtn: Button? = null

    private var lastPageBtn: Button? = null

    private var preElectiveBtn: Button? = null

    private var pageView: LinearLayout? = null

    private var pageContentView: TextView? = null

    private var searchDialog: ElectiveCourseSearchDialog? = null

    private var filterDialog: ElectiveCourseFilterDialog? = null

    private var noDataView: LinearLayout? = null

    private var messageDialog: iOSDialog? = null

    private var pageNature: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elective_course)

        this.mainApplication            = this.application as MainApplication
        this.userController             = this.mainApplication!!.userController
        this.electiveCourseController   = this.mainApplication!!.electiveCourseController
        this.electiveCourseController!!.updateDelegate = this
        this.electiveCourseController!!.callbackDelegate = this
        this.electiveCourseTaskController = this.mainApplication!!.electiveCourseTaskController
        this.electiveCourseTaskController!!.delegate = this
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
        nextPageBtn = findViewById(R.id.nextPage)
        lastPageBtn = findViewById(R.id.lastPage)
        pageView = findViewById(R.id.pageView)
        pageContentView = findViewById(R.id.pageContent)
        electivedBtn = findViewById(R.id.electiveBtn)
        preElectiveBtn = findViewById(R.id.preElectiveBtn)
        filterBtn!!.setOnClickListener(this)
        queryBtn!!.setOnClickListener(this)
        preElectiveBtn!!.setOnClickListener(this)
        electivedBtn!!.setOnClickListener(this)
        lastPageBtn!!.setOnClickListener(this)
        nextPageBtn!!.setOnClickListener(this)
        pageView!!.visibility = View.INVISIBLE
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
        this.filterDialog = ElectiveCourseFilterDialog(this, electiveCourseController!!.getFilter(), this)
    }

    override fun titleBarOnClicked(id: Int) {
        when (id) {
            R.id.headback -> finish()
        }
    }

    private fun modifyPageElement(listSize: Int) {
        if (listSize < 1) {
            noDataView!!.visibility = View.VISIBLE
            pageView!!.visibility = View.INVISIBLE
        } else {
            noDataView!!.visibility = View.INVISIBLE
            if (this.electiveCourseController!!.pageViewDisplay()) {
                pageView!!.visibility = View.VISIBLE
                pageContentView!!.text = electiveCourseController!!.getPageString()
                lastPageBtn!!.isEnabled = electiveCourseController!!.canLastPage()
                nextPageBtn!!.isEnabled = electiveCourseController!!.canNextPagte()
            } else {
                pageView!!.visibility = View.INVISIBLE
            }
        }
    }

    override fun updateElectiveCourseList(electiveCourseList: MutableList<ElectiveCourse>) {
        runOnUiThread {
            val adaptData = ArrayList<Map<String, Any>>()
            titleBarController!!.setRightProgress(View.INVISIBLE)
            if (this.pageNature == 1) {
                modifyPageElement(electiveCourseList.size)
            } else {
                pageView!!.visibility = View.INVISIBLE
                if (electiveCourseList.size < 1) {
                    noDataView!!.visibility = View.VISIBLE
                } else {
                    noDataView!!.visibility = View.INVISIBLE
                }
            }

            for (electiveCourse in electiveCourseList) {
                val map = HashMap<String, Any>()
                map["icon"] = this.mapTitleToIcon!![electiveCourse.owner]!!
                map["name"] = electiveCourse.name!!
                map["location"] = "${electiveCourse.location}(${electiveCourse.campus})"
                map["teacher"] = electiveCourse.teacher!!
                map["score"] = "学分: ${electiveCourse.studyScore}"
                map["have"] = "剩余: ${electiveCourse.have}/${electiveCourse.allHave}"
                if (this.pageNature == 2) {
                    map["btn"] = "退选"
                } else {
                    map["btn"] = "选课"
                }

                adaptData.add(map)
            }

            val simpleAdapter = ElectiveCourseItemAdapter(
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
                            R.id.electiveCourseBtn),
                    this)
            electiveCourseListView!!.adapter = simpleAdapter
        }
    }

    private fun updatePreElectiveCourseList(courseTasks: MutableList<ElectiveTask>) {
        this.pageNature = 3

        pageView!!.visibility = View.INVISIBLE
        if (courseTasks.size < 1) {
            noDataView!!.visibility = View.VISIBLE
        } else {
            noDataView!!.visibility = View.INVISIBLE
        }

        val adaptData = ArrayList<Map<String, Any>>()
        for (courseTask in courseTasks) {
            val map = HashMap<String, Any>()
            map["icon"] = this.mapTitleToIcon!![courseTask.courseOwner!!]!!
            map["name"] = courseTask.courseName!!
            map["time"] = "工作时间: ${GlobalLib.GetRuntime(courseTask.timestamp)}"
            map["btn"] = "移除"
            adaptData.add(map)
        }

        val simpleAdapter = ElectiveCourseItemAdapter(
                this,
                adaptData,
                R.layout.gadget_item_pre_elective_item,
                arrayOf("icon", "name", "time", "btn"),
                intArrayOf(
                        R.id.electiveCourseItemIcon,
                        R.id.electiveCourseName,
                        R.id.electiveTaskRuntime,
                        R.id.electiveCourseBtn),
                this)
        electiveCourseListView!!.adapter = simpleAdapter
    }

    override fun errorElectiveCourseList(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            finish()
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
                R.id.lastPage -> {
                    this.pageNature = 1
                    titleBarController!!.setRightProgress(View.VISIBLE)
                    this.electiveCourseController!!.lastPage()
                }
                R.id.nextPage -> {
                    this.pageNature = 1
                    titleBarController!!.setRightProgress(View.VISIBLE)
                    this.electiveCourseController!!.nextPage()
                }
                R.id.electiveBtn -> {
                    this.pageNature = 2
                    updateElectiveCourseList(this.electiveCourseController!!.getElectivedCourse())
                }
                R.id.preElectiveBtn -> {
                    updatePreElectiveCourseList(electiveCourseTaskController!!.getTaskList())
                }
            }
        }
    }

    override fun searchElectiveCourse(courseName: String) {
        this.pageNature = 1
        this.searchDialog!!.cancel()
        titleBarController!!.setRightProgress(View.VISIBLE)
//        Toast.makeText(this, "搜索\"$courseName\"", Toast.LENGTH_SHORT).show()
        electiveCourseController!!.searchByName(courseName)
    }

    override fun filterElectiveCourse() {
        this.pageNature = 1
        this.filterDialog!!.cancel()
        titleBarController!!.setRightProgress(View.VISIBLE)
        electiveCourseController!!.filter()
    }

    class ElectiveCourseItemAdapter(
            context: Context?,
            data: ArrayList<Map<String, Any>>,
            resource: Int,
            from: Array<out String>?,
            to: IntArray?,
            delegate: ItemButtonClickListener) : SimpleAdapter(context, data, resource, from, to), View.OnClickListener {

        private val mDelegate: ItemButtonClickListener = delegate

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = convertView
            val map = this.getItem(position) as MutableMap<String, Any>
            if (view == null) {
                if (map["btn"].toString().contentEquals("移除")) {
                    view = LayoutInflater.from(parent!!.context).inflate(R.layout.gadget_item_pre_elective_item,  parent, false)
                } else {
                    view = LayoutInflater.from(parent!!.context).inflate(R.layout.gadget_item_elective_course,  parent, false)
                }
            }
            val btn: Button?  = view!!.findViewById(R.id.electiveCourseBtn)
            if (btn != null) {
                btn.setOnClickListener(this)
                btn.hint = position.toString()
//                btn.id = position
                btn.text = map["btn"].toString()
            }
            return super.getView(position, view, parent)
        }

        override fun onClick(v: View?) {
//            Toast.makeText(mContext, "Fuck item ${v!!.id}", Toast.LENGTH_SHORT).show()
            val button: Button = v as Button
            mDelegate.itemButtonClicked(button.hint.toString().toInt())
        }

    }

    override fun itemButtonClicked(position: Int) {
        when (this.pageNature) {
            1 -> {
                this.electiveCourseController!!.elective(position)
//                this.electiveCourseTaskController!!.addTask(this.electiveCourseController!!.getCourse(position))
//                ElectiveCourseService.start(this)
            }
            2 -> {
                this.electiveCourseController!!.disElective(position)
            }
            3 -> {
                val response: Response = this.electiveCourseTaskController!!.removeTask(position)
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun updateElectiveCourseTaskList() {
        runOnUiThread {
            val list = electiveCourseTaskController!!.getTaskList()
            if (list.size < 1) {
                ElectiveCourseService.stop(this)
            }
            updatePreElectiveCourseList(list)
        }
    }


    override fun electiveCourseCallback(course: ElectiveCourse, response: Response) {
        runOnUiThread {
            if (this.pageNature == 2) {
                updateElectiveCourseList(this.electiveCourseController!!.getElectivedCourse())
                Toast.makeText(this, "[${course.name}]${response.message}", Toast.LENGTH_SHORT).show()
            } else {
                if (!response.isSuccess && (response.message.contains("不是选课时间") || response.message.contains("人数超过限制"))) {
                    messageDialog = iOSDialog(this)
                            .setButtons(arrayListOf("取消", "确定"))
                            .setContent(getString(R.string.display_pre_elective_course_message).format(Locale.getDefault(), response.message))
                            .setOnClickedListener {
                                messageDialog!!.cancel()
                                when (it) {
                                    1 -> {
                                        val addResponse: Response = this.electiveCourseTaskController!!.addTask(course)
                                        ElectiveCourseService.start(this)
                                        Toast.makeText(this, "[${course.name}]${addResponse.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    messageDialog!!.show()
                } else {
                    Toast.makeText(this, "[${course.name}]${response.message}", Toast.LENGTH_SHORT).show()
                    this.electiveCourseTaskController!!.notifyTaskFinished(course.name!!)
                }
            }
        }
    }
}
