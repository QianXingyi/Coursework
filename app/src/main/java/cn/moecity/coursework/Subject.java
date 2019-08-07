package cn.moecity.coursework;

import cn.bmob.v3.BmobObject;

public class Subject extends BmobObject {
    private String name;
    private int subNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubNo() {
        return subNo;
    }

    public void setSubNo(int subNo) {
        this.subNo = subNo;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", subNo=" + subNo +
                '}';
    }
}
