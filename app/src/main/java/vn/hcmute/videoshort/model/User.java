package vn.hcmute.videoshort.model;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String avatarUrl;

    public User() {
    }

    public User(String uid, String name, String email, String phone, String avatarUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }
}
