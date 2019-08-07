package cn.moecity.coursework;

import cn.bmob.v3.BmobObject;

public class StuTut extends BmobObject {
    private String stuId;
    private String tutId;
    private String subId;

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getTutId() {
        return tutId;
    }

    public void setTutId(String tutId) {
        this.tutId = tutId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    @Override
    public String toString() {
        return "StuTut{" +
                "stuId='" + stuId + '\'' +
                ", tutId='" + tutId + '\'' +
                ", subId='" + subId + '\'' +
                '}';
    }
}
