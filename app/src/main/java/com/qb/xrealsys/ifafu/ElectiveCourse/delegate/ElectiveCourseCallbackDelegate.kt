package com.qb.xrealsys.ifafu.ElectiveCourse.delegate

import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse

interface ElectiveCourseCallbackDelegate {
    fun electiveCourseCallback(course: ElectiveCourse, response: Response)
}