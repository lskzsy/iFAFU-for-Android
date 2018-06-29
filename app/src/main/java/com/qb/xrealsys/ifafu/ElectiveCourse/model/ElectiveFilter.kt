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

    fun getCourseCampusName(): String {
        return this.courseCampusName[this.courseCampus[this.courseCampusIndex].toInt()]
    }
}