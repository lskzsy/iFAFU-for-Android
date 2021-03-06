package com.qb.xrealsys.ifafu.ElectiveCourse.model

class ElectiveFilter {

    private val courseCampusName: Array<String> = arrayOf("福建农林大学本部", "网络公选课")

    var courseNature: MutableList<String> = ArrayList()

    var isFree: MutableList<String> = ArrayList()

    var courseOwner: MutableList<String> = ArrayList()

    var courseCampus: MutableList<String> = ArrayList()

    var courseTime: MutableList<String> = ArrayList()

    var courseNatureIndex: Int = 0

    var isFreeIndex: Int = 0

    var courseOwnerIndex: Int = 0

    var courseCampusIndex: Int = 0

    var courseTimeIndex: Int = 0

    var courseNameFilter: String? = null

    fun getCourseCampusName(): String {
        return this.courseCampusName[this.courseCampus[this.courseCampusIndex].toInt()]
    }

    fun getCourseCampusName(index: Int): String {
        return this.courseCampusName[index]
    }

    fun getCourseCampus(): String {
        return this.courseCampus[this.courseCampusIndex]
    }

    fun getCourseNature(): String {
        return this.courseNature[this.courseNatureIndex]
    }

    fun getCourseHave(): String {
        return this.isFree[this.isFreeIndex]
    }

    fun getCourseOwner(): String {
        return this.courseOwner[this.courseOwnerIndex]
    }

    fun getCourseTime(): String {
        return this.courseTime[this.courseTimeIndex]
    }
}