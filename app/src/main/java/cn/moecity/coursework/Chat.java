package cn.moecity.coursework;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class Chat extends BmobObject {
    private String stuId;
    private String tutId;
    private String timestamp;
    private boolean fromTo;
    private boolean isRead;
    private String content;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.timestamp = simpleDateFormat.format(date);
    }

    public boolean isFromTo() {
        return fromTo;
    }

    public void setFromTo(boolean fromTo) {
        this.fromTo = fromTo;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "stuId='" + stuId + '\'' +
                ", tutId='" + tutId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", fromTo=" + fromTo +
                ", isRead=" + isRead +
                ", content='" + content + '\'' +
                '}';
    }
}
