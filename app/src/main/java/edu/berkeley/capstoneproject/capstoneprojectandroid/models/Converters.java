package edu.berkeley.capstoneproject.capstoneprojectandroid.models;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by LukeTseng on 04/12/2017.
 */

public class Converters {
    @TypeConverter
    public static ArrayList<Float> fromString(String value) {

        Type listType = new TypeToken<ArrayList<Float>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Float> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}