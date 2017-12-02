package edu.berkeley.capstoneproject.capstoneprojectandroid.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


/**
 * Created by LukeTseng on 01/12/2017.
 */

@Database(entities = {Patient.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;

    public abstract PatientDao userDao();

    public static AppDatabase getAppDatabase(Context context) {

        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return sInstance;
    }



    public static void destroyInstance() {
        sInstance = null;
    }
}
