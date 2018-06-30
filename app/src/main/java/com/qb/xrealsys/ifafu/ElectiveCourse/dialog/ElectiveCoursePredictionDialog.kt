package com.qb.xrealsys.ifafu.ElectiveCourse.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bigkoo.pickerview.OptionsPickerView
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseTaskController
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.ElectiveCoursePredictionDelegate
import com.qb.xrealsys.ifafu.R

class ElectiveCoursePredictionDialog(
        context: Context?,
        electiveCourseTaskController: ElectiveCourseTaskController,
        delegate: ElectiveCoursePredictionDelegate):
        Dialog(context, R.style.styleProgressDialog), View.OnClickListener, OptionsPickerView.OnOptionsSelectListener {

    private val activity: Activity = context as Activity

    private val mElectiveCourseTaskController: ElectiveCourseTaskController = electiveCourseTaskController

    private val mDelegate: ElectiveCoursePredictionDelegate = delegate

    private val optionsPickerViews: MutableList<OptionsPickerView<*>> = ArrayList()

    private val optionsViewTitles: Array<String> = arrayOf("课程属性", "课程归属", "上课校区")

    private val natureFilter: MutableList<String> = arrayListOf("任意选修课", "")

    private val ownerFilter: MutableList<String> = arrayListOf("创新创业教育类", "人文社科类", "文学素养类", "艺术、体育类", "自然科学类")

    private val campusFilter: MutableList<String> = arrayListOf("0", "1", "")

    private var natureFilterSelection: String = ""

    private var ownerFilterSelection: String = this.ownerFilter[0]

    private var campusFilterSelection: String = ""

    private var closeBtn: ImageView? = null

    private var confirmBtn: Button? = null

    private var natureFilterView: LinearLayout? = null

    private var ownerFilterView: LinearLayout? = null

    private var campusFilterView: LinearLayout? = null

    private var nameFilterView: TextView? = null

    private var modifyId: Int = 0

    init {
        setContentView(R.layout.dialog_elective_course_prediction)
        initElement()
    }

    private fun initElement() {
        this.closeBtn = findViewById(R.id.closeBtn)
        this.confirmBtn = findViewById(R.id.queryBtn)
        this.natureFilterView = findViewById(R.id.natureFilter)
        this.ownerFilterView = findViewById(R.id.ownerFilter)
        this.campusFilterView = findViewById(R.id.campusFilter)
        this.nameFilterView = findViewById(R.id.searchInput)

        this.confirmBtn!!.setOnClickListener(this)
        this.closeBtn!!.setOnClickListener(this)
        this.natureFilterView!!.setOnClickListener(this)
        this.ownerFilterView!!.setOnClickListener(this)
        this.campusFilterView!!.setOnClickListener(this)

        this.natureFilterView!!.findViewById<TextView>(R.id.itemTitle).text = optionsViewTitles[0]
        this.ownerFilterView!!.findViewById<TextView>(R.id.itemTitle).text = optionsViewTitles[1]
        this.campusFilterView!!.findViewById<TextView>(R.id.itemTitle).text = optionsViewTitles[2]

        initOptionViews()
    }

    private fun setFilterViewInput(filterView: LinearLayout, input: String) {
        if (filterView == this.campusFilterView) {
            when (input) {
                "0" -> {
                    filterView.findViewById<TextView>(R.id.itemInput).text = "福建农林大学本部"
                }
                "1" -> {
                    filterView.findViewById<TextView>(R.id.itemInput).text = "网络公选课"
                }
                "" -> {
                    filterView.findViewById<TextView>(R.id.itemInput).text = "不限"
                }
            }
        } else {
            if (input.isNotEmpty()) {
                filterView.findViewById<TextView>(R.id.itemInput).text = input
            } else {
                filterView.findViewById<TextView>(R.id.itemInput).text = "不限"
            }
        }
    }

    private fun initOptionViews() {
        createOptionView(0, this.natureFilter)
        createOptionView(1, this.ownerFilter)
        createOptionView(2, this.campusFilter)
    }

    private fun createOptionView(index: Int, options: MutableList<String>) {
        val optionsPickerView = OptionsPickerView.Builder(
                this.activity,
                this)
                .setLinkage(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleSize(13)
                .setTitleText(this.optionsViewTitles[index])
                .setTitleColor(Color.parseColor("#157efb"))
                .isDialog(true)
                .build()

        optionsPickerView!!.setPicker(options as MutableList<Nothing?>)
        optionsPickerView.setSelectOptions(0)
        this.optionsPickerViews.add(optionsPickerView)
    }

    override fun onClick(v: View?) {
        this.modifyId = v!!.id
        when(this.modifyId) {
            R.id.queryBtn -> {
                this.mElectiveCourseTaskController.addTask(
                        this.natureFilterSelection,
                        this.ownerFilterSelection,
                        this.campusFilterSelection,
                        this.nameFilterView!!.text.toString())
                this.mDelegate.predictionDialogConfirm()
            }
            R.id.closeBtn -> {
                this.mDelegate.predictionDialogClose()
            }
            R.id.natureFilter -> {
                this.optionsPickerViews[0].show()
            }
            R.id.ownerFilter -> {
                this.optionsPickerViews[1].show()
            }
            R.id.campusFilter -> {
                this.optionsPickerViews[2].show()
            }
        }
    }

    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
        when (this.modifyId) {
            R.id.natureFilter -> {
                this.natureFilterSelection = this.natureFilter[options1]
                setFilterViewInput(this.natureFilterView!!, this.natureFilterSelection)
            }
            R.id.ownerFilter -> {
                this.ownerFilterSelection = this.ownerFilter[options1]
                setFilterViewInput(this.ownerFilterView!!, this.ownerFilterSelection)
            }
            R.id.campusFilter -> {
                this.campusFilterSelection = this.campusFilter[options1]
                setFilterViewInput(this.campusFilterView!!, this.campusFilterSelection)
            }
        }
    }
}