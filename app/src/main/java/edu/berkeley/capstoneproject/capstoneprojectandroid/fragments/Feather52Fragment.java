package edu.berkeley.capstoneproject.capstoneprojectandroid.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by Alex on 31/10/2017.
 */

public abstract class Feather52Fragment extends Fragment {

    private static final String TAG = Feather52Fragment.class.getSimpleName();

    protected String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public abstract void clearUi();
}
