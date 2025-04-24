package vn.hcmute.videoshort.model;

public class UserModel {
    public String uid;
    public String email;
    public String avatarUrl;

    public UserModel() {}  // Firebase cần constructor trống

    public UserModel(String uid, String email, String avatarUrl) {
        this.uid = uid;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }
}
