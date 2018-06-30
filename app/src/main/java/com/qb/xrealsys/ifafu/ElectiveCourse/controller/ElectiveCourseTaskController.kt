package com.qb.xrealsys.ifafu.ElectiveCourse.controller;

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.DB.ElectiveCourseTask
import com.qb.xrealsys.ifafu.ElectiveCourse.delegate.ElectiveCourseTaskDelegate
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveCourse
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveFilter
import com.qb.xrealsys.ifafu.ElectiveCourse.model.ElectiveTask
import com.qb.xrealsys.ifafu.R
import io.realm.Realm
import io.realm.RealmResults
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import com.qb.xrealsys.ifafu.Main.MainActivity



class ElectiveCourseTaskController(electiveCourseController: ElectiveCourseController, notificationManager: NotificationManager, context: Context) {

    private val mElectiveCourseController: ElectiveCourseController = electiveCourseController

    private val mNotificationManager: NotificationManager = notificationManager

    private val mContext: Context = context

    private val mElectiveCourseTaskList: MutableList<ElectiveTask> = ArrayList()

    private val mListReadLock: Lock = ReentrantLock()

    var delegate: ElectiveCourseTaskDelegate? = null

    fun syncWithDB() {
        val r: Realm = Realm.getDefaultInstance()
        val account = this.mElectiveCourseController.getUser().account
        val results: RealmResults<ElectiveCourseTask>  = r.where(ElectiveCourseTask::class.java)
                .equalTo("account", account).findAll()
        mElectiveCourseTaskList.clear()
        mListReadLock.lock()
        for (result in results) {
            mElectiveCourseTaskList.add(ElectiveTask(result))
        }
        mListReadLock.unlock()
    }

    fun notifyTaskFinished(courseName: String) {
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(mContext, "notifyTaskFinished")
        val mPendingIntent: PendingIntent = PendingIntent.getActivity(mContext, 1,  Intent(), 0)

        mBuilder.setContentIntent(mPendingIntent)
        mBuilder.setSmallIcon(R.drawable.drawable_superman)
        mBuilder.setAutoCancel(true)
        mBuilder.setContentTitle("[$courseName]已经完成选课")

        mNotificationManager.notify(0, mBuilder.build())
    }

    fun addTask(course: ElectiveCourse): Response {
        val task: ElectiveCourseTask = savePostParams()
        val r: Realm = Realm.getDefaultInstance()
        val results = r.where(ElectiveCourseTask::class.java)
                .equalTo("account", task.account).count()

        var response =  Response(true, 0, "已经添加到预订列表中")
        if (results >= 2) {
            response = Response(false, 0, "添加失败：至多只允许同时添加两门课程")
        }
        r.executeTransactionAsync { realm ->
            if (response.isSuccess) {
                task.courseName = course.name
                task.courseIndex = course.courseIndex
                task.courseOwner = course.owner
                task.timestamp = System.currentTimeMillis() / 1000
                realm.insert(task)

                mListReadLock.lock()
                mElectiveCourseTaskList.add(ElectiveTask(task))
                mListReadLock.unlock()
            }
        }

        return response
    }

    fun removeTask(index: Int): Response {
        val account = this.mElectiveCourseController.getUser().account
        val list = getTaskList()
        var response =  Response(false, 0, "移除失败, 目标不在预订列表中")
        if (index < list.size) {
            val task: ElectiveTask = mElectiveCourseTaskList[index]
            mListReadLock.lock()
            mElectiveCourseTaskList.removeAt(index)
            mListReadLock.unlock()
            Realm.getDefaultInstance().executeTransactionAsync { realm ->
                val removeList = realm.where(ElectiveCourseTask::class.java)
                        .equalTo("account", account).findAll()
                if (index < removeList.size) {
                    removeList[index]!!.deleteFromRealm()
                }

                if (delegate != null) {
                    delegate!!.updateElectiveCourseTaskList()
                }
            }
            response = Response(false, 0, "[${task.courseName}]移除成功")
        } else {
            if (delegate != null) {
                delegate!!.updateElectiveCourseTaskList()
            }
        }

        return response
    }

    fun getTaskList(): MutableList<ElectiveTask> {
        return mElectiveCourseTaskList
    }

    fun electiveCourse(): Response {
        val taskList = getTaskList()
        if (taskList.size == 0) {
            return Response(true, 0, "")
        }

        mListReadLock.lock()
        for (index in taskList.withIndex()) {
            val response: Response = this.mElectiveCourseController.elective(index.value)
            if (response.isSuccess) {
                notifyTaskFinished(index.value.courseName!!)
                removeTask(index.index)
            }
        }
        mListReadLock.unlock()

        return Response(false, 0, "")
    }

    private fun savePostParams(): ElectiveCourseTask {
        val filter: ElectiveFilter = this.mElectiveCourseController.getFilter()
        val task = ElectiveCourseTask()
        task.account = this.mElectiveCourseController.getUser().account
        task.viewState = this.mElectiveCourseController.getViewState()
        task.viewStateGenerator = this.mElectiveCourseController.getViewStateGenerator()
        task.campusFilter = filter.getCourseCampus()
        task.natureFilter = filter.getCourseNature()
        task.ownerFilter = filter.getCourseOwner()
        task.haveFilter = filter.getCourseHave()
        task.timeFilter = filter.getCourseTime()
        task.nameFilter = filter.courseNameFilter
        task.curPage = this.mElectiveCourseController.getCurPage().toString()
        return  task
    }
}
