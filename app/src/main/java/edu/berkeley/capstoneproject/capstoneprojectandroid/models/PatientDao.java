package edu.berkeley.capstoneproject.capstoneprojectandroid.models;

import java.util.ArrayList;
import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by LukeTseng on 01/12/2017.
 */

@Dao
public interface PatientDao {
    @Query("SELECT * FROM patients")
    List<Patient> getAll();

    @Query("SELECT * FROM patients WHERE uid IN (:userIds)")
    List<Patient> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM patients WHERE uid LIKE :uid LIMIT 1")
    Patient findByUid(int uid);

    @Query("SELECT * FROM patients WHERE name LIKE :name LIMIT 1")
    Patient findByName(String name);

    @Query("DELETE FROM patients")
    void deleteAll();


    @Insert
    void insertAll(Patient... patients);

    @Insert(onConflict = REPLACE)
    void insert(Patient patient);

    @Delete
    void delete(Patient patients);


}
