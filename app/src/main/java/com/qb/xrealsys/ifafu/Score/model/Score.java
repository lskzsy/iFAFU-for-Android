package com.qb.xrealsys.ifafu.Score.model;

import com.qb.xrealsys.ifafu.Base.model.Model;

import java.io.Serializable;

/**
 * Created by sky on 14/02/2018.
 */

public class Score extends Model implements Serializable {

    private String  year;

    private String  term;

    private String  courseCode;

    private String  courseName;

    private String  courseType;

    private String  courseOwner;

    private float   studyScore;

    private float   score;

    private float   makeupScore;

    private boolean isRestudy;

    private String  institute;

    private float   scorePoint;

    private String  comment;

    private String  makeupComment;

    private boolean isDelayExam;

    public boolean isDelayExam() {
        return isDelayExam;
    }

    public void setDelayExam(boolean delayExam) {
        isDelayExam = delayExam;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseOwner() {
        return courseOwner;
    }

    public void setCourseOwner(String courseOwner) {
        this.courseOwner = courseOwner;
    }

    public float getStudyScore() {
        return studyScore;
    }

    public void setStudyScore(float studyScore) {
        this.studyScore = studyScore;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getMakeupScore() {
        return makeupScore;
    }

    public void setMakeupScore(float makeupScore) {
        this.makeupScore = makeupScore;
    }

    public boolean isRestudy() {
        return isRestudy;
    }

    public void setRestudy(boolean restudy) {
        isRestudy = restudy;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public float getScorePoint() {
        return scorePoint;
    }

    public void setScorePoint(float scorePoint) {
        this.scorePoint = scorePoint;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMakeupComment() {
        return makeupComment;
    }

    public void setMakeupComment(String makeupComment) {
        this.makeupComment = makeupComment;
    }
}
