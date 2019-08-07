package cn.moecity.coursework;

import cn.bmob.v3.BmobObject;

public class TutSub extends BmobObject {
    private String tutId;
    private String subId;

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
        return "TutSub{" +
                "tutId='" + tutId + '\'' +
                ", subId='" + subId + '\'' +
                '}';
    }
}
