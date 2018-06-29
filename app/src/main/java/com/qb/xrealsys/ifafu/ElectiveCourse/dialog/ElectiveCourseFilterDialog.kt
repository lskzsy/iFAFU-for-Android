package com.qb.xrealsys.ifafu.ElectiveCourse.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.*
import com.bigkoo.pickerview.OptionsPickerView
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveFilter
import com.qb.xrealsys.ifafu.R

class ElectiveCourseFilterDialog (context: Context?, filter: ElectiveFilter):
        Dialog(context, R.style.styleProgressDialog), View.OnClickListener, OptionsPickerView.OnOptionsSelectListener {

    private val filter: ElectiveFilter = filter

    private val activity: Activity = context as Activity

    private var natureFilterView: LinearLayout? = null

    private var haveFilterView: LinearLayout? = null

    private var ownerFilterView: LinearLayout? = null

    private var campusFilterView: LinearLayout? = null

    private var timeFilterView: LinearLayout? = null

    private var filterViews: Array<LinearLayout>? = null

    private var closeBtn: ImageView? = null

    private var optionsPickerView: OptionsPickerView<*>? = null

    private val filterTitle: Array<Int> = arrayOf(
            R.string.display_elective_course_nature_input,
            R.string.display_elective_course_have_input,
            R.string.display_elective_course_owner_input,
            R.string.display_elective_course_campus_input,
            R.string.display_elective_course_time_input)

    init {
        setContentView(R.layout.dialog_elective_course_filter)
        initElements()
    }

    override fun show() {
        super.show()

        initData()
    }

    private fun updatePickerView(titleIndex: Int, items: MutableList<String>, itemIndex: Int) {
        this.optionsPickerView = OptionsPickerView.Builder(
                activity,
                this)
                .setLinkage(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleSize(13)
                .setTitleText(activity.getString(filterTitle[titleIndex]))
                .setTitleColor(Color.parseColor("#157efb"))
                .build()

        this.optionsPickerView!!.setPicker(items as MutableList<Nothing?>)
        this.optionsPickerView!!.setSelectOptions(itemIndex)
        this.optionsPickerView!!.show()
    }

    private fun initElements() {
        this.natureFilterView = findViewById(R.id.natureFilter)
        this.haveFilterView = findViewById(R.id.haveFilter)
        this.ownerFilterView = findViewById(R.id.ownerFilter)
        this.campusFilterView = findViewById(R.id.campusFilter)
        this.timeFilterView = findViewById(R.id.timeFilter)
        this.closeBtn = findViewById(R.id.closeBtn)
        this.closeBtn!!.setOnClickListener(this)
        this.filterViews = arrayOf(
                natureFilterView!!,
                haveFilterView!!,
                ownerFilterView!!,
                campusFilterView!!,
                timeFilterView!!)

        for (i in 0..4) {
            this.filterViews!![i].findViewById<TextView>(R.id.itemTitle).text =
                    this.activity.getString(this.filterTitle[i])
            this.filterViews!![i].setOnClickListener(this)
        }
    }

    private fun initData() {
        setFilterViewInput(this.natureFilterView!!, filter.courseNature[filter.courseNatureIndex])
        setFilterViewInput(this.haveFilterView!!, filter.isFree[filter.isFreeIndex])
        setFilterViewInput(this.ownerFilterView!!, filter.courseOwner[filter.courseOwnerIndex])
        setFilterViewInput(this.campusFilterView!!, filter.getCourseCampusName())
        setFilterViewInput(this.timeFilterView!!, filter.courseTime[filter.courseTimeIndex])
    }

    private fun setFilterViewInput(filterView: LinearLayout, input: String) {
        if (input.isNotEmpty()) {
            filterView.findViewById<TextView>(R.id.itemInput).text = input
        } else {
            filterView.findViewById<TextView>(R.id.itemInput).text = "不限"
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.closeBtn -> {
                this.cancel()
            }
            R.id.natureFilter -> {
                updatePickerView(0, this.filter.courseNature, this.filter.courseNatureIndex)
            }
        }
    }

    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}