package com.qb.xrealsys.ifafu.ElectiveCourse.delegate

import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse

interface UpdateElectiveCourseListViewDelegate {
    fun updateElectiveCourseList(electiveCourseList: MutableList<ElectiveCourse>)

    fun errorElectiveCourseList(error: String)
}