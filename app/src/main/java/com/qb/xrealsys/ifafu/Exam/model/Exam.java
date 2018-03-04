package com.qb.xrealsys.ifafu.Exam.model;

import com.qb.xrealsys.ifafu.Base.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 24/02/2018.
 */

public class Exam extends Model {

    private String id;

    private String name;

    private String datetime;

    private String address;

    private String seatNumber;

    private String campus;

    private List<Integer> beginTime;

    public List<Integer> getBeginTime() {
        return beginTime;
    }

    public void setDatetimeData(String datetime) {
        beginTime = new ArrayList<>();
        Pattern pattern = Pattern.compile("(.*)年(.*)月(.*)日\\((.*?):(.*?)-(.*?):(.*?)\\)");
        Matcher matcher = pattern.matcher(datetime);

        if (matcher.find()) {
            beginTime.add(Integer.parseInt(matcher.group(1)));
            beginTime.add(Integer.parseInt(matcher.group(2)));
            beginTime.add(Integer.parseInt(matcher.group(3)));
            beginTime.add(Integer.parseInt(matcher.group(4)));
            beginTime.add(Integer.parseInt(matcher.group(5)));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
        setDatetimeData(this.datetime);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }
}
