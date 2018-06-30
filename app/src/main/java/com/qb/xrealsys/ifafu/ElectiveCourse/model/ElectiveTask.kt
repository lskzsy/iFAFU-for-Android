package com.qb.xrealsys.ifafu.ElectiveCourse.model;

import com.qb.xrealsys.ifafu.DB.ElectiveCourseTask

class ElectiveTask(task: ElectiveCourseTask) {

    var courseName: String? = null

    var courseIndex: String? = null

    var courseOwner: String? = null

    var natureFilter: String? = null

    var haveFilter: String? = null

    var ownerFilter: String? = null

    var campusFilter: String? = null

    var timeFilter: String? = null

    var nameFilter: String? = null

    var viewState: String? = null

    var curPage: String? = null

    var viewStateGenerator: String? = null

    var timestamp: Long = 0

    var focus: Boolean = false

    var account: String? = null

    init {
        this.courseName = task.courseName
        this.courseIndex = task.courseIndex
        this.courseOwner = task.courseOwner
        this.natureFilter = task.natureFilter
        this.haveFilter = task.haveFilter
        this.ownerFilter = task.ownerFilter
        this.campusFilter = task.campusFilter
        this.timeFilter = task.timeFilter
        this.nameFilter = task.nameFilter
        this.viewState = task.viewState
        this.curPage = task.curPage
        this.viewStateGenerator = task.viewStateGenerator
        this.timestamp = task.timestamp
        this.focus = task.isFocus
        this.account = task.account
    }
}
