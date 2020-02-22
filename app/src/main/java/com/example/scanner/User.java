package com.example.scanner;

public class User {

    private String Uid, name, photoUrl, qrCode;
    private int Score;

    public User() {
    }

    public User(String uid, String name, String photoUrl, int Score, String qrCode) {
        Uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.Score = Score;
        this.qrCode = qrCode;
    }


    public int getScore() {
        return Score;
    }

    public String getQrCode() {
        return qrCode;
    }


    public void setScore(int score) {
        Score = score;
    }


    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getUid() {
        return Uid;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }


    public void setUid(String uid) {
        Uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
