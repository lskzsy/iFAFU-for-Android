package com.qb.xrealsys.ifafu.ElectiveCourse.controller

import com.qb.xrealsys.ifafu.Base.controller.AsyncController
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.DB.ElectiveCourseTask
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.ElectiveCourseCallbackDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.UpdateElectiveCourseListViewDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourseList
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveFilter
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveTask
import com.qb.xrealsys.ifafu.ElectiveCourse.web.ElectiveCourseInterface
import com.qb.xrealsys.ifafu.Tool.ConfigHelper
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import com.qb.xrealsys.ifafu.User.model.User

class ElectiveCourseController (
        userController: UserAsyncController,
        configHelper: ConfigHelper): AsyncController(userController.threadPool){

    private val electiveCourseList: ElectiveCourseList = ElectiveCourseList()

    private val electiveCourseInterface: ElectiveCourseInterface = ElectiveCourseInterface(
            configHelper.GetSystemValue("host"),
            userController)

    private val userController: UserAsyncController = userController

    var callbackDelegate: ElectiveCourseCallbackDelegate? = null

    var updateDelegate: UpdateElectiveCourseListViewDelegate? = null

    fun getCourse(position: Int): ElectiveCourse {
        return this.electiveCourseList.courses[position]
    }

    fun getCurPage(): Int {
        return this.electiveCourseList.curPage
    }

    fun getElectivedCourse(): MutableList<ElectiveCourse> {
        return electiveCourseList.electived
    }

    fun getUser(): User {
        return userController.data
    }

    fun getViewState(): String {
        return electiveCourseInterface.viewState
    }

    fun getViewStateGenerator(): String {
        return electiveCourseInterface.viewStateGenerator
    }

    fun getIndexResponse(): Response {
        val user: User = this.userController.data
        return this.electiveCourseInterface.getElectiveCourseIndex(
                user.account,
                user.name,
                this.electiveCourseList)
    }

    fun selectCourse(task: ElectiveTask): Response {
        val user: User = this.userController.data
        val response: Response = this.electiveCourseInterface.searchElectiveCourse(
                user.account,
                user.name,
                this.electiveCourseList,
                task.natureFilter!!,
                task.haveFilter!!,
                task.ownerFilter!!,
                task.campusFilter!!,
                task.timeFilter!!,
                task.nameFilter!!,
                1)

        if (response.isSuccess && electiveCourseList.courses.size > 0) {
            val course: ElectiveCourse = electiveCourseList.courses[0]
            task.focus = true
            task.courseIndex = course.courseIndex
            task.courseName = course.name
            task.viewState = getViewState()
            task.viewStateGenerator = getViewStateGenerator()
            task.curPage = getCurPage().toString()
            task.courseOwner = course.owner

            return Response(true, 0, "")
        }

        return Response(false, 0, "")
    }

    fun getIndex() {
        this.threadPool.execute {
            val response: Response = getIndexResponse()
            if (response.isSuccess) {
                if (this.updateDelegate != null) {
                    updateDelegate!!.updateElectiveCourseList(electiveCourseList.courses)
                }
            } else {
                updateDelegate!!.errorElectiveCourseList(response.message)
            }
        }
    }

    fun getFilter(): ElectiveFilter {
        return this.electiveCourseList.filter
    }

    fun searchByName(courseName: String) {
        this.threadPool.execute {
            val user: User = this.userController.data
            this.electiveCourseList.curPage = 1
            val response: Response = this.electiveCourseInterface.searchElectiveCourseByName(
                    user.account,
                    user.name,
                    this.electiveCourseList,
                    courseName)
            if (response.isSuccess) {
                if (this.updateDelegate != null) {
                    updateDelegate!!.updateElectiveCourseList(electiveCourseList.courses)
                }
            } else {
                updateDelegate!!.errorElectiveCourseList(response.message)
            }
        }
    }

    fun filter() {
        this.electiveCourseList.curPage = 1
        search()
    }

    private fun search() {
        this.threadPool.execute {
            val user: User = this.userController.data
            val response: Response = this.electiveCourseInterface.searchElectiveCourse(
                    user.account,
                    user.name,
                    this.electiveCourseList)
            if (response.isSuccess) {
                if (this.updateDelegate != null) {
                    updateDelegate!!.updateElectiveCourseList(electiveCourseList.courses)
                }
            } else {
                updateDelegate!!.errorElectiveCourseList(response.message)
            }
        }
    }

    fun pageViewDisplay(): Boolean {
        return 1 != this.electiveCourseList.pageSize
    }

    fun canNextPagte(): Boolean {
        return this.electiveCourseList.curPage < this.electiveCourseList.pageSize
    }

    fun canLastPage(): Boolean {
        return this.electiveCourseList.curPage > 1
    }

    fun getPageString(): String {
        return "${this.electiveCourseList.curPage}/${this.electiveCourseList.pageSize}"
    }

    fun nextPage() {
        if (canNextPagte()) {
            this.electiveCourseList.curPage++
            search()
        }
    }

    fun lastPage() {
        if (canLastPage()) {
            this.electiveCourseList.curPage--
            search()
        }
    }

    fun elective(task: ElectiveTask): Response {
        val user: User = this.userController.data
        return this.electiveCourseInterface.electiveCourse(user.account, user.name, task)
    }

    fun elective(position: Int) {
        this.threadPool.execute {
            val courseIndex = electiveCourseList.courses[position].courseIndex
            val user: User = this.userController.data
            val response: Response = this.electiveCourseInterface.electiveCourse(
                    user.account,
                    user.name,
                    this.electiveCourseList,
                    courseIndex!!)
            if (this.callbackDelegate != null) {
                this.callbackDelegate!!.electiveCourseCallback(electiveCourseList.courses[position], response)
            }
        }
    }

    fun disElective(position: Int) {
        this.threadPool.execute {
            val courseIndex = electiveCourseList.electived[position].courseIndex
            val course:ElectiveCourse = electiveCourseList.electived[position]
            val user: User = this.userController.data
            val response: Response = this.electiveCourseInterface.disElectiveCourse(
                    user.account,
                    user.name,
                    this.electiveCourseList,
                    courseIndex!!)
            if (this.callbackDelegate != null) {
                this.callbackDelegate!!.electiveCourseCallback(course, response)
            }
        }
    }
}