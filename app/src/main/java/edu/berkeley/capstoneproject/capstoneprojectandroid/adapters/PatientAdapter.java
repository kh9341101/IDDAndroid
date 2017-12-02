package edu.berkeley.capstoneproject.capstoneprojectandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.PatientHolder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Patient;


/**
 * Created by LukeTseng on 30/11/2017.
 */

public class PatientAdapter extends RecyclerView.Adapter<PatientHolder> {
    private ArrayList<Patient> mPatients = new ArrayList<Patient>();
    private onPatientSelectedListener mListener;

    @Override
    public PatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PatientHolder.newInstance(parent, mListener);
    }

    @Override
    public void onBindViewHolder(PatientHolder holder, int position) {

        holder.bind(mPatients.get(position));
    }

    @Override
    public int getItemCount() {
        return mPatients.size();
    }

    public void setCards(ArrayList<Patient> patients) {

        if (patients == null) {
            mPatients = new ArrayList<Patient>();
        }
        mPatients = patients;
    }


    public void setOnPatientSelectedListener(onPatientSelectedListener listener) {
        mListener = listener;
    }

    public interface onPatientSelectedListener {
        public void onPatientSelected(View v, int position);
    }

    public Patient getItemAtPostition(int position) {
        return mPatients.get(position);
    }

//    public void setType(int type) {
//        this.mType = type;
//    }
}
