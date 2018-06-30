package com.qb.xrealsys.ifafu.ElectiveCourse.model

class ElectiveCourseList {

    var filter: ElectiveFilter = ElectiveFilter()

    var electived: MutableList<ElectiveCourse> = ArrayList()

    var courses: MutableList<ElectiveCourse> = ArrayList()

    var pageSize: Int = 1

    var curPage: Int = 1
}