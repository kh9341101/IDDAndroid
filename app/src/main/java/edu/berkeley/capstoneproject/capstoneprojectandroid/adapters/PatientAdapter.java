package edu.berkeley.capstoneproject.capstoneprojectandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import edu.berkeley.capstoneproject.capstoneprojectandroid.PatientHolder;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Patient;


/**
 * Created by LukeTseng on 30/11/2017.
 */

public class PatientAdapter extends RecyclerView.Adapter<PatientHolder> {
    private Patient[] mCards = new Patient[0];
    private onPatientSelectedListener mListener;

    @Override
    public PatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PatientHolder.newInstance(parent, mListener);
    }

    @Override
    public void onBindViewHolder(PatientHolder holder, int position) {

        holder.bind(mCards[position]);
    }

    @Override
    public int getItemCount() {
        return mCards.length;
    }

    public void setCards(Patient[] cards) {

        if (cards == null) {
            mCards = new Patient[0];
        }
        mCards = cards;
    }

    public void setOnItemSelectedListener(onPatientSelectedListener listener) {
        mListener = listener;
    }

    public interface onPatientSelectedListener {
        public void onPatientSelected(View v, int position);
    }

//    public void setType(int type) {
//        this.mType = type;
//    }
}
