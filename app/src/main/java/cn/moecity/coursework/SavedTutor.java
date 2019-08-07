package cn.moecity.coursework;

public class SavedTutor {
    private String objectId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String androidId;
    private String facebookId;
    private Boolean isLogged;

    public SavedTutor(String objectId, Tutor tutor) {
        this.objectId = objectId;
        this.firstName = tutor.getFirstName();
        this.lastName = tutor.getLastName();
        this.email = tutor.getEmail();
        this.password = tutor.getPassword();
        this.androidId = tutor.getAndroidId();
        this.facebookId = tutor.getFacebookId();
        this.isLogged = tutor.getLogged();
    }

    public SavedTutor() {
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

    public Boolean getLogged() {
        return isLogged;
    }

    public void setLogged(Boolean logged) {
        isLogged = logged;
    }

    @Override
    public String toString() {
        return "SavedTutor{" +
                "objectId='" + objectId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", androidId='" + androidId + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", isLogged=" + isLogged +
                '}';
    }
}
