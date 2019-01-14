package com.brickgit.tomatist.view.activitylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brickgit.tomatist.R;
import com.brickgit.tomatist.data.database.Activity;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int VIEW_TYPE_ADD_ACTIVITY = 0;
  private static final int VIEW_TYPE_ACTIVITY = 1;

  private static final int ADD_ACTIVITY_BUTTON_POSITION = 0;

  public interface OnActivityClickListener {
    void onAddActivityClick();

    void onActivityClick(Activity activity);
  }

  private List<Activity> mActivities = new LinkedList<>();
  private OnActivityClickListener mOnActivityClickListener;

  public ActivityListAdapter() {
  }

  public void updateActivities(List<Activity> activities) {

    mActivities.clear();
    mActivities.addAll(activities);
    notifyDataSetChanged();
  }

  public void setOnActivityClickListener(OnActivityClickListener onActivityClickListener) {
    mOnActivityClickListener = onActivityClickListener;
  }

  @Override
  @NonNull
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ADD_ACTIVITY) {
      View view =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.view_holder_add_activity, parent, false);
      return new ActivityListAddViewHolder(view, mOnActivityClickListener);
    } else {
      View view =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.view_holder_activity, parent, false);
      return new ActivityListViewHolder(view, mOnActivityClickListener);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == VIEW_TYPE_ACTIVITY) {
      ActivityListViewHolder viewHolder = (ActivityListViewHolder) holder;
      viewHolder.bind(mActivities.get(position - 1));
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == ADD_ACTIVITY_BUTTON_POSITION) {
      return VIEW_TYPE_ADD_ACTIVITY;
    } else {
      return VIEW_TYPE_ACTIVITY;
    }
  }

  @Override
  public int getItemCount() {
    return mActivities.size() + 1;
  }
}