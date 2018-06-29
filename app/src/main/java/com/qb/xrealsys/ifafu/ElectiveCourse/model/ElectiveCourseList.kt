package com.qb.xrealsys.ifafu.ElectiveCourse.model

class ElectiveCourseList {

    var filter: ElectiveFilter = ElectiveFilter()

    var courses: MutableList<ElectiveCourse> = ArrayList()

    var pageSize: Int = 0

    var curPage: Int = 0

    fun clearCourses() {
        this.courses.clear()
    }
}