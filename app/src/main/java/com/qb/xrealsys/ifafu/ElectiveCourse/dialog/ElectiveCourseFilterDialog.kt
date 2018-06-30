package com.qb.xrealsys.ifafu.ElectiveCourse.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.*
import com.bigkoo.pickerview.OptionsPickerView
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.ElectiveCourseFilterDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveFilter
import com.qb.xrealsys.ifafu.R

class ElectiveCourseFilterDialog (context: Context?, filter: ElectiveFilter, delegate: ElectiveCourseFilterDelegate):
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

    private var filterBtn: Button? = null

    private val delegate: ElectiveCourseFilterDelegate = delegate

    private var optionsPickerViews: MutableList<OptionsPickerView<*>> = ArrayList()

    private var modifyItemId: Int = 0

    private var modifyItemIndex: MutableMap<Int, Int> = HashMap()

    private var isInitData: Boolean = true

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

        if (isInitData) {
            initData()
            isInitData = false
        }
    }

    private fun updatePickerView(titleIndex: Int, items: MutableList<String>, itemIndex: Int) {
        val optionsPickerView = OptionsPickerView.Builder(
                activity,
                this)
                .setLinkage(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleSize(13)
                .setTitleText(activity.getString(filterTitle[titleIndex]))
                .setTitleColor(Color.parseColor("#157efb"))
                .isDialog(true)
                .build()

        optionsPickerView!!.setPicker(items as MutableList<Nothing?>)
        optionsPickerView.setSelectOptions(itemIndex)
        this.optionsPickerViews.add(optionsPickerView)
    }

    private fun initElements() {
        this.natureFilterView = findViewById(R.id.natureFilter)
        this.haveFilterView = findViewById(R.id.haveFilter)
        this.ownerFilterView = findViewById(R.id.ownerFilter)
        this.campusFilterView = findViewById(R.id.campusFilter)
        this.timeFilterView = findViewById(R.id.timeFilter)
        this.closeBtn = findViewById(R.id.closeBtn)
        this.filterBtn = findViewById(R.id.queryBtn)
        this.filterBtn!!.setOnClickListener(this)
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

        updatePickerView(0, this.filter.courseNature, this.filter.courseNatureIndex)
        updatePickerView(1, this.filter.isFree, this.filter.isFreeIndex)
        updatePickerView(2, this.filter.courseOwner, this.filter.courseOwnerIndex)
        updatePickerView(3, this.filter.courseCampus, this.filter.courseCampusIndex)
        updatePickerView(4, this.filter.courseTime, this.filter.courseTimeIndex)
    }

    private fun setFilterViewInput(filterView: LinearLayout, input: String) {
        if (input.isNotEmpty()) {
            filterView.findViewById<TextView>(R.id.itemInput).text = input
        } else {
            filterView.findViewById<TextView>(R.id.itemInput).text = "不限"
        }
    }

    override fun onClick(v: View?) {
        modifyItemId = v!!.id
        when (v.id) {
            R.id.queryBtn -> {
                if (this.modifyItemIndex.containsKey(R.id.natureFilter)) {
                    this.filter.courseNatureIndex = this.modifyItemIndex[R.id.natureFilter]!!
                }
                if (this.modifyItemIndex.containsKey(R.id.haveFilter)) {
                    this.filter.isFreeIndex = this.modifyItemIndex[R.id.haveFilter]!!
                }
                if (this.modifyItemIndex.containsKey(R.id.ownerFilter)) {
                    this.filter.courseOwnerIndex = this.modifyItemIndex[R.id.ownerFilter]!!
                }
                if (this.modifyItemIndex.containsKey(R.id.campusFilter)) {
                    this.filter.courseCampusIndex = this.modifyItemIndex[R.id.campusFilter]!!
                }
                if (this.modifyItemIndex.containsKey(R.id.timeFilter)) {
                    this.filter.courseTimeIndex = this.modifyItemIndex[R.id.timeFilter]!!
                }
                this.filter.courseNameFilter = null

                delegate.filterElectiveCourse()
            }
            R.id.closeBtn -> {
                this.cancel()
            }
            R.id.natureFilter -> {
                this.optionsPickerViews[0].show()
            }
            R.id.haveFilter -> {
                this.optionsPickerViews[1].show()
            }
            R.id.ownerFilter -> {
                this.optionsPickerViews[2].show()
            }
            R.id.campusFilter -> {
                this.optionsPickerViews[3].show()
            }
            R.id.timeFilter -> {
                this.optionsPickerViews[4].show()
            }
        }
    }

    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
        when (this.modifyItemId) {
            R.id.natureFilter -> {
                setFilterViewInput(this.natureFilterView!!, this.filter.courseNature[options1])
                modifyItemIndex[R.id.natureFilter] = options1
            }
            R.id.haveFilter -> {
                setFilterViewInput(this.haveFilterView!!, this.filter.isFree[options1])
                modifyItemIndex[R.id.haveFilter] = options1
            }
            R.id.ownerFilter -> {
                setFilterViewInput(this.ownerFilterView!!, this.filter.courseOwner[options1])
                modifyItemIndex[R.id.ownerFilter] = options1
            }
            R.id.campusFilter -> {
                setFilterViewInput(this.campusFilterView!!, this.filter.getCourseCampusName(options1))
                modifyItemIndex[R.id.campusFilter] = options1
            }
            R.id.timeFilter -> {
                setFilterViewInput(this.timeFilterView!!, this.filter.courseTime[options1])
                modifyItemIndex[R.id.timeFilter] = options1
            }
        }
    }
}