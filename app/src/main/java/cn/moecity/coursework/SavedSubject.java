package cn.moecity.coursework;

public class SavedSubject {
    private String objectId;
    private String name;
    private int subNo;

    public SavedSubject(String objectId, Subject subject) {
        this.objectId = objectId;
        this.name = subject.getName();
        this.subNo = subject.getSubNo();
    }

    public SavedSubject() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

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
        return "SavedSubject{" +
                "objectId='" + objectId + '\'' +
                ", name='" + name + '\'' +
                ", subNo=" + subNo +
                '}';
    }
}
