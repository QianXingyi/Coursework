package cn.moecity.coursework;

public class SavedChat {
    private String objectId;
    private String stuId;
    private String tutId;
    private String timestamp;
    private boolean fromTo;
    private boolean isRead;
    private String content;

    public SavedChat(String objectId, Chat chat) {
        this.objectId = objectId;
        this.stuId = chat.getStuId();
        this.tutId = chat.getTutId();
        this.timestamp = chat.getTimestamp();
        this.fromTo = chat.isFromTo();
        this.isRead = chat.isRead();
        this.content = chat.getContent();
    }

    public SavedChat() {

    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

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
        return "SavedChat{" +
                "objectId='" + objectId + '\'' +
                ", stuId='" + stuId + '\'' +
                ", tutId='" + tutId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", fromTo=" + fromTo +
                ", isRead=" + isRead +
                ", content='" + content + '\'' +
                '}';
    }
}
