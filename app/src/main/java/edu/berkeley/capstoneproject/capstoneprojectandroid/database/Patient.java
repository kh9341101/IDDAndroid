package edu.berkeley.capstoneproject.capstoneprojectandroid.database;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

/**
 * Created by LukeTseng on 29/11/2017.
 */

@Entity(tableName = "patients")
public class Patient {
    @PrimaryKey(autoGenerate = true)
    private int uid;

//    @ColumnInfo(name = "account")
//    private String mAccount;
//    @ColumnInfo(name = "password")
//    private String mPassword;
    @ColumnInfo(name = "name")
    private String mTitle;
    @ColumnInfo(name = "description")
    private String mDescription;
    @ColumnInfo(name = "thumburl")
    private String mThumbnailUrl;
    @ColumnInfo(name = "sumtext")
    private String mSummaryText;
    @ColumnInfo(name = "dailyavgdegree")
    public ArrayList<Float> mdailyAvgDegree;
    @ColumnInfo(name = "dailymaxdegree")
    public ArrayList<Float> mdailyMaxDegree;
    @ColumnInfo(name = "dailybendcount")
    public ArrayList<Float> mdailyBendCount;

//    public Patient(String mTitle, String mDescription, String mThumbnailUrl, String mSummaryText) {
//        this.mTitle = mTitle;
//        this.mDescription = mDescription;
//        this.mThumbnailUrl = mThumbnailUrl;
//        this.mSummaryText = mSummaryText;
////        this.dailyAvgDegree = new ArrayList<>();
////        this.mdailyAvgDegree = dailyAvgDegree;
//
//    }

    public Patient(String mTitle, String mDescription, String mThumbnailUrl, String mSummaryText, ArrayList<Float> mdailyAvgDegree, ArrayList<Float> mdailyBendCount, ArrayList<Float> mdailyMaxDegree) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mThumbnailUrl = mThumbnailUrl;
        this.mSummaryText = mSummaryText;
        this.mdailyAvgDegree = new ArrayList<>();
        this.mdailyAvgDegree = mdailyAvgDegree;
        this.mdailyBendCount = mdailyBendCount;
        this.mdailyMaxDegree = mdailyMaxDegree;
    }

    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid;}

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }
    public void setThumbnailUrl(String mThumbnailUrl) {
        this.mThumbnailUrl = mThumbnailUrl;
    }

    public String getSummaryText() {
        return mSummaryText;
    }
    public void setSummaryText(String mSummaryText) {
        this.mSummaryText = mSummaryText;
    }

    public ArrayList<Float> getDailyAvgDegree() {
        return mdailyAvgDegree;
    }
    public void setmDailyAvgDegree(ArrayList<Float> mdailyAvgDegree) {
        this.mdailyAvgDegree = mdailyAvgDegree;
    }

    public ArrayList<Float> getDailyMaxDegree() {
        return mdailyMaxDegree;
    }
    public void setDailyMaxDegree(ArrayList<Float> mdailyMaxDegree) {
        this.mdailyMaxDegree = mdailyMaxDegree;
    }

    public ArrayList<Float> getDailyBendCount() {
        return mdailyBendCount;
    }
    public void setdailyBendCount(ArrayList<Float> mdailyBendCount) {
        this.mdailyBendCount = mdailyBendCount;
    }

//    private String mName;
//    private String mGender;
//    private String mAge;
//    private String mThumbnailUrl;
//    private String mDescription;
//    private String mCause;
//    private String mDateofSurgery;
//
//    public Patient(String mName, String mGender, String mThumbnailUrl, String mDescription, String mCause, String mDateofSurgery) {
//        this.mName = mName;
//        this.mGender = mGender;
//        this.mThumbnailUrl = mThumbnailUrl;
//        this.mDescription = mDescription;
//        this.mCause = mCause;
//        this.mDateofSurgery = mDateofSurgery;
//    }
//
//    public String getName() {
//        return mName;
//    }
//
//    public String getGender() {
//        return mGender;
//    }
//
//    public String getThumbnailUrl() {
//        return mThumbnailUrl;
//    }
//
//    public String getDescription() {
//        return mDescription;
//    }
//
//    public String getCause() {
//        return mCause;
//    }
//
//    public String getDateofSurgery() {
//        return mDateofSurgery;
//    }

}
