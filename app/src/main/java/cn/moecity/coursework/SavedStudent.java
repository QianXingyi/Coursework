package cn.moecity.coursework;

public class SavedStudent {
    private String objectId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String androidId;
    private String facebookId;

    public SavedStudent(String objectId, Student student) {
        this.objectId = objectId;
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();
        this.password = student.getPassword();
        this.androidId = student.getAndroidId();
        this.facebookId = student.getFacebookId();
    }

    public SavedStudent() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @Override
    public String toString() {
        return "SavedStudent{" +
                "objectId='" + objectId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", androidId='" + androidId + '\'' +
                ", facebookId='" + facebookId + '\'' +
                '}';
    }
}
