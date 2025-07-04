package com.firebyte.elearning;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class AbsensiSesi {
    private String mataKuliah;
    @ServerTimestamp
    private Date waktuBuka;
    private Date waktuTutup;

    public AbsensiSesi() {}
    // Getters
    public String getMataKuliah() { return mataKuliah; }
    public Date getWaktuBuka() { return waktuBuka; }
    public Date getWaktuTutup() { return waktuTutup; }
}