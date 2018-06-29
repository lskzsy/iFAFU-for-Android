package com.qb.xrealsys.ifafu.ElectiveCourse.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.ElectiveCourseSearchDelegate
import com.qb.xrealsys.ifafu.R

class ElectiveCourseSearchDialog(context: Context?, delegate: ElectiveCourseSearchDelegate):
        Dialog(context, R.style.styleProgressDialog), View.OnClickListener {

    private var closeBtn: ImageView? = null

    private var queryBtn: Button? = null

    private var queryInput: EditText? = null

    private val inContext: Context = context!!

    private var delegate: ElectiveCourseSearchDelegate? = null

    init {
        setContentView(R.layout.dialog_elective_course_search)
        this.delegate = delegate

        closeBtn = findViewById(R.id.closeBtn)
        queryBtn = findViewById(R.id.queryBtn)
        queryInput = findViewById(R.id.searchInput)
        closeBtn!!.setOnClickListener(this)
        queryBtn!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.closeBtn -> {
                this.cancel()
            }

            R.id.queryBtn -> {
                val queryName = queryInput!!.text.toString()
                if (queryName.isNotEmpty()) {
                    this.delegate!!.searchElectiveCourse(queryName)
                } else {
                    Toast.makeText(inContext, "输入不允许为空！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}