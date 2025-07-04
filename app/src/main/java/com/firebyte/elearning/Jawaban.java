package com.firebyte.elearning;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Jawaban {
    private String userId;
    private String userName;
    private String fileUrl;
    private Date submittedAt;

    public Jawaban() {
        // Diperlukan untuk Firestore
    }

    public Jawaban(String userId, String userName, String fileUrl) {
        this.userId = userId;
        this.userName = userName;
        this.fileUrl = fileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    @ServerTimestamp
    public Date getSubmittedAt() {
        return submittedAt;
    }
}