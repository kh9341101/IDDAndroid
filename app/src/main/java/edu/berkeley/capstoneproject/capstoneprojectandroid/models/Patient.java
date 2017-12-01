package edu.berkeley.capstoneproject.capstoneprojectandroid.models;

/**
 * Created by LukeTseng on 29/11/2017.
 */

public class Patient {
    private String mTitle;
    private String mDescription;
    private String mThumbnailUrl;
    private String mSummaryText;

    public Patient(String mTitle, String mDescription, String mThumbnailUrl, String mSummaryText) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mThumbnailUrl = mThumbnailUrl;
        this.mSummaryText = mSummaryText;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getSummaryText() {
        return mSummaryText;
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
