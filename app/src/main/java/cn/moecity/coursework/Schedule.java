package cn.moecity.coursework;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Schedule extends BmobObject {
    private String Schedule_Time;
    private String Student_Name;
    private String Tutor_Name;
    private String tutId;
    private String stuId;


    public String getStudent_Name() {
        return Student_Name;
    }

    public void setStudent_Name(String student_Name) {
        Student_Name = student_Name;
    }

    public String getTutor_Name() {
        return Tutor_Name;
    }

    public void setTutor_Name(String tutor_Name) {
        Tutor_Name = tutor_Name;
    }

    public String getTutId() {
        return tutId;
    }

    public void setTutId(String tutId) {
        this.tutId = tutId;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getSchedule_Time() {
        return Schedule_Time;
    }

    public void setSchedule_Time(String schedule_Time) {
        Schedule_Time = schedule_Time;
    }
}
