package com.qb.xrealsys.ifafu.DB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ElectiveCourseTask extends  RealmObject {

    private String courseName;

    private String courseIndex;

    private String courseOwner;

    private String natureFilter;

    private String haveFilter;

    private String ownerFilter;

    private String campusFilter;

    private String timeFilter;

    private String nameFilter;

    private String viewState;

    private String curPage;

    private String viewStateGenerator;

    private long timestamp;

    private boolean focus;

    private String account;

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public String getCurPage() {
        return curPage;
    }

    public void setCurPage(String curPage) {
        this.curPage = curPage;
    }

    public String getCourseOwner() {
        return courseOwner;
    }

    public void setCourseOwner(String courseOwner) {
        this.courseOwner = courseOwner;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getViewState() {
        return viewState;
    }

    public void setViewState(String viewState) {
        this.viewState = viewState;
    }

    public String getViewStateGenerator() {
        return viewStateGenerator;
    }

    public void setViewStateGenerator(String viewStateGenerator) {
        this.viewStateGenerator = viewStateGenerator;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseIndex() {
        return courseIndex;
    }

    public void setCourseIndex(String courseIndex) {
        this.courseIndex = courseIndex;
    }

    public String getNatureFilter() {
        return natureFilter;
    }

    public void setNatureFilter(String natureFilter) {
        this.natureFilter = natureFilter;
    }

    public String getHaveFilter() {
        return haveFilter;
    }

    public void setHaveFilter(String haveFilter) {
        this.haveFilter = haveFilter;
    }

    public String getOwnerFilter() {
        return ownerFilter;
    }

    public void setOwnerFilter(String ownerFilter) {
        this.ownerFilter = ownerFilter;
    }

    public String getCampusFilter() {
        return campusFilter;
    }

    public void setCampusFilter(String campusFilter) {
        this.campusFilter = campusFilter;
    }

    public String getTimeFilter() {
        return timeFilter;
    }

    public void setTimeFilter(String timeFilter) {
        this.timeFilter = timeFilter;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
