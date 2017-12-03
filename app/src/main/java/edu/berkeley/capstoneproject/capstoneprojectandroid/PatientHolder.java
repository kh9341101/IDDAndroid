package edu.berkeley.capstoneproject.capstoneprojectandroid;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import android.content.Context;
import android.content.Intent;


import edu.berkeley.capstoneproject.capstoneprojectandroid.activities.ProfileActivity;
import edu.berkeley.capstoneproject.capstoneprojectandroid.models.Patient;
import edu.berkeley.capstoneproject.capstoneprojectandroid.adapters.PatientAdapter;

/**
 * Created by LukeTseng on 30/11/2017.
 */

public class PatientHolder extends RecyclerView.ViewHolder {
    private static int muid;
    private TextView mTitleView;
    private TextView mDescView;
    private ImageView mThumbnailView;
    private TextView mSummaryView;
//    private static PatientAdapter.onPatientSelectedListener mListener;

    public static PatientHolder newInstance(ViewGroup container, PatientAdapter.onPatientSelectedListener Listener) {

        View root = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_news_card, container, false);
//        mListener = Listener;
        return new PatientHolder(root);
    }

    private PatientHolder(View itemView) {
        super(itemView);
        mTitleView = itemView.findViewById(R.id.card_title);
        mDescView = itemView.findViewById(R.id.card_subtitle);
        mSummaryView = itemView.findViewById(R.id.card_summary);
        mThumbnailView = itemView.findViewById(R.id.card_image);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("OnClick", String.valueOf(getAdapterPosition()));
//                Context context = view.getContext();
//                Intent intent = new Intent(context, ProfileActivity.class);
//                context.startActivity(intent);
//                mListener.onPatientSelected(view, getAdapterPosition());
//            }
//        });

    }

    public void bind(Patient card) {

        mTitleView.setText(card.getTitle());
        mDescView.setText(card.getDescription());
        mSummaryView.setText(card.getSummaryText());

        Glide.with(itemView.getContext()).load(card.getThumbnailUrl()).into(mThumbnailView);
    }

    public static int getUid() {
        return PatientHolder.muid;
    }

    public static void setUid(int uid) {
        PatientHolder.muid = uid;
    }

//    private static int getLayoutResourceId(int type) {
//        int selectedLayoutResource;
//        switch (type) {
//            case TYPE_LIST:
//            case TYPE_PATIENT_LIST:
//                selectedLayoutResource = R.layout.layout_news_card;
//                break;
//            case TYPE_SECOND_LIST:
//                selectedLayoutResource = R.layout.layout_second_news_card;
//                break;
//            case TYPE_GRID:
//            case TYPE_SECOND_GRID:
//                selectedLayoutResource = R.layout.layout_ecom_item;
//                break;
//            default:
//                selectedLayoutResource = 0;
//        }
//
//        return selectedLayoutResource;
//    }
}
