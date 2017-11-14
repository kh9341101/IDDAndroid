package edu.berkeley.capstoneproject.capstoneprojectandroid.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.capstoneproject.capstoneprojectandroid.R;
import edu.berkeley.capstoneproject.capstoneprojectandroid.fragments.Feather52Fragment;

/**
 * Created by Alex on 31/10/2017.
 */

public class Feather52DrawerAdapter extends RecyclerView.Adapter<Feather52DrawerAdapter.ViewHolder> {

    private static final String TAG = Feather52DrawerAdapter.class.getSimpleName();

    private static final int DRAWER_HEADER = 0;
    private static final int DRAWER_LIST = 1;

    private Context mContext;
    private List<DrawerItem> mDrawerItems;
    private String mName = "Feather 52";
    private OnItemSelectedListener mListener;

    public static class DrawerItem {
        private String mTitle;
        private int mIcon;
        private Class<? extends Feather52Fragment> mFragmentClass;

        public DrawerItem(String title, int icon, Class<? extends  Feather52Fragment> fragmentClass) {
            mTitle = title;
            mIcon = icon;
            mFragmentClass = fragmentClass;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public int getIcon() {
            return mIcon;
        }

        public void setIcon(int icon) {
            mIcon = icon;
        }

        public Class<? extends Feather52Fragment> getFragmentClass() {
            return mFragmentClass;
        }

        public void setFragmentClass(Class<? extends Feather52Fragment> fragmentClass) {
            mFragmentClass = fragmentClass;
        }
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        int mHolderId;

        TextView mNameView;

        ImageView mIconView;
        TextView mTitleView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case DRAWER_HEADER:
                    mNameView = (TextView) itemView.findViewById(R.id.drawer_name);
                    mHolderId = DRAWER_HEADER;
                    break;
                case DRAWER_LIST:
                    mIconView = (ImageView) itemView.findViewById(R.id.item_drawer_row_imageview_icon);
                    mTitleView = (TextView) itemView.findViewById(R.id.item_drawer_row_textview_title);
                    mHolderId = DRAWER_LIST;
                    break;
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(view, getAdapterPosition());
                }
            });
        }
    }

    public Feather52DrawerAdapter(Context context, List<DrawerItem> drawerItems) {
        mContext = context;
        mDrawerItems = drawerItems;
    }

    public Feather52DrawerAdapter(Context context, List<DrawerItem> drawerItems, String name) {
        mContext = context;
        mDrawerItems = drawerItems;
        mName = name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DRAWER_HEADER:
                View headerView = LayoutInflater.from(mContext).inflate(R.layout.drawer_header, parent, false);
                ViewHolder headerHolder = new ViewHolder(headerView, viewType);
                return headerHolder;
            case DRAWER_LIST:
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_drawer_row, parent, false);
                ViewHolder itemHolder = new ViewHolder(itemView, viewType);
                return itemHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.mHolderId) {
            case DRAWER_HEADER:
                holder.mNameView.setText(mName);
                break;
            case DRAWER_LIST:
                holder.mIconView.setImageResource(mDrawerItems.get(position-1).getIcon());
                holder.mTitleView.setText(mDrawerItems.get(position-1).getTitle());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDrawerItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isPositionHeader(position)? DRAWER_HEADER : DRAWER_LIST;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    public interface OnItemSelectedListener {
        public void onItemSelected(View v, int position);
    }
}
