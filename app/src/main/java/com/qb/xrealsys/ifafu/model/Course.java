package com.qb.xrealsys.ifafu.model;

/**
 * Created by sky on 14/02/2018.
 */

public class Course extends Model {

    private String name;

    private int    week;

    private int    begin;

    private int    end;

    private int    weekBegin;

    private int    weekEnd;

    private String teacher;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getWeekBegin() {
        return weekBegin;
    }

    public void setWeekBegin(int weekBegin) {
        this.weekBegin = weekBegin;
    }

    public int getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(int weekEnd) {
        this.weekEnd = weekEnd;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
