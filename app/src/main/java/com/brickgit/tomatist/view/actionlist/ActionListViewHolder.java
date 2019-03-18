package com.brickgit.tomatist.view.actionlist;

import android.view.View;
import android.widget.TextView;

import com.brickgit.tomatist.R;
import com.brickgit.tomatist.data.database.Action;
import com.brickgit.tomatist.data.database.Category;
import com.brickgit.tomatist.data.database.CategoryGroup;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

public class ActionListViewHolder extends RecyclerView.ViewHolder {

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

  private TextView mStartDateTime;
  private TextView mEndDateTime;

  private TextView mTitleView;
  private TextView mNoteView;

  private TextView mCategoryGroupView;
  private TextView mCategoryView;

  private ExpandableLayout mButtons;

  private Action mAction;

  public ActionListViewHolder(
      View view,
      final ActionListAdapter.OnItemClickListener onItemClickListener,
      final ActionListAdapter.OnActionClickListener onActionClickListener) {
    super(view);

    mStartDateTime = view.findViewById(R.id.start_time);
    mEndDateTime = view.findViewById(R.id.end_time);

    mTitleView = view.findViewById(R.id.action_title);
    mNoteView = view.findViewById(R.id.action_note);

    mCategoryGroupView = view.findViewById(R.id.action_category_group);
    mCategoryView = view.findViewById(R.id.action_category);

    mButtons = view.findViewById(R.id.buttons);
    mButtons.setOnExpansionUpdateListener(
        (expansionFraction, state) -> {
          if (state == ExpandableLayout.State.EXPANDING) {
            if (onActionClickListener != null) {
              onActionClickListener.onItemExpand(itemView);
            }
          }
        });

    itemView.setOnClickListener(
        (v) -> {
          toggle();
          if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this);
          }
        });

    view.findViewById(R.id.edit)
        .setOnClickListener(
            (v) -> {
              if (onActionClickListener != null) {
                onActionClickListener.onEditClick(mAction);
              }
            });

    view.findViewById(R.id.copy)
        .setOnClickListener(
            (v) -> {
              if (onActionClickListener != null) {
                onActionClickListener.onCopyClick(mAction);
              }
            });

    view.findViewById(R.id.delete)
        .setOnClickListener(
            (v) -> {
              if (onActionClickListener != null) {
                onActionClickListener.onDeleteClick(mAction);
              }
            });
  }

  public void bind(
      Action action, Map<String, CategoryGroup> categoryGroups, Map<String, Category> categories) {
    mAction = action;

    if (action.isFinished() && action.getStartTime() != null) {
      mStartDateTime.setText(dateFormat.format(action.getStartTime()));
      if (!action.getStartTime().equals(action.getEndTime())) {
        mEndDateTime.setText(dateFormat.format(action.getEndTime()));
      } else {
        mEndDateTime.setText("");
      }
    } else {
      mStartDateTime.setText("");
      mEndDateTime.setText("");
    }

    mTitleView.setText(action.getTitle());
    mNoteView.setText(action.getNote());

    Category category = categories.get(action.getCategoryId());
    if (category != null) {
      mCategoryView.setText(category.getTitle());
      CategoryGroup group = categoryGroups.get(category.getGroupId());
      mCategoryGroupView.setText(group != null ? group.getTitle() : "");
    } else {
      mCategoryView.setText("");
      mCategoryGroupView.setText("");
    }
  }

  public void toggle() {
    mButtons.toggle(true);
  }
}
