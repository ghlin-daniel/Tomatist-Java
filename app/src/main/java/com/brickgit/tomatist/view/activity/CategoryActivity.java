package com.brickgit.tomatist.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.brickgit.tomatist.R;
import com.brickgit.tomatist.data.database.Category;
import com.brickgit.tomatist.data.database.CategoryGroup;
import com.brickgit.tomatist.data.preferences.TomatistPreferences;
import com.brickgit.tomatist.view.categorylist.CategoryGroupListAdapter;
import com.brickgit.tomatist.view.categorylist.CategoryGroupListTouchHelperCallback;
import com.brickgit.tomatist.view.categorylist.CategoryListAdapter;
import com.brickgit.tomatist.view.categorylist.CategoryListTouchHelperCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryActivity extends BaseActivity {

  public static final String SELECTED_CATEGORY_ID = "SELECTED_CATEGORY_ID";
  public static final long INVALID_SELECTED_CATEGORY_ID = -1;

  private View mRootView;
  private RecyclerView mCategoryGroupsView;
  private CategoryGroupListAdapter mCategoryGroupAdapter;
  private RecyclerView mCategoriesView;
  private CategoryListAdapter mCategoryAdapter;

  private long mSelectedCategoryGroupId;

  private LiveData<List<CategoryGroup>> mCategoryGroups;
  private Observer<List<CategoryGroup>> mCategoryGroupsObserver =
      (categoryGroups) -> mCategoryGroupAdapter.updateCategoryGroups(categoryGroups);

  private LiveData<List<Category>> mCategories;
  private Observer<List<Category>> mCategoriesObserver =
      (categories) -> mCategoryAdapter.updateCategories(mSelectedCategoryGroupId, categories);

  public static void startForResult(Activity activity, int requestCoe) {
    Intent intent = new Intent(activity, CategoryActivity.class);
    activity.startActivityForResult(intent, requestCoe);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_category);

    mRootView = findViewById(R.id.root_view);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.category);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    findViewById(R.id.add_category_group)
        .setOnClickListener((view) -> showAddCategoryGroupDialog());
    findViewById(R.id.add_category).setOnClickListener((view) -> showAddCategoryDialog());

    mCategoryGroupsView = findViewById(R.id.category_groups);
    mCategoryGroupsView.setLayoutManager(new LinearLayoutManager(this));
    mCategoryGroupAdapter = new CategoryGroupListAdapter();
    mCategoryGroupAdapter.setOnCategoryGroupClickListener(
        (categoryGroup) -> {
          TomatistPreferences.getInstance(CategoryActivity.this)
              .setLastUsedCategoryGroupId(categoryGroup.getCategoryGroupId());
          selectCategoryGroup(categoryGroup.getCategoryGroupId());
        });
    mCategoryGroupsView.setAdapter(mCategoryGroupAdapter);
    ItemTouchHelper.Callback categoryGroupsViewCallback =
        new CategoryGroupListTouchHelperCallback((position) -> removeCategoryGroup(position));
    ItemTouchHelper categoryGroupsViewTouchHelper = new ItemTouchHelper(categoryGroupsViewCallback);
    categoryGroupsViewTouchHelper.attachToRecyclerView(mCategoryGroupsView);

    mCategoriesView = findViewById(R.id.categories);
    mCategoriesView.setLayoutManager(new LinearLayoutManager(this));
    mCategoryAdapter = new CategoryListAdapter();
    mCategoryAdapter.setOnCategoryClickListener(
        (category) -> {
          TomatistPreferences.getInstance(CategoryActivity.this)
              .setLastUsedCategoryId(category.getCategoryId());
          Intent intent = new Intent();
          intent.putExtra(SELECTED_CATEGORY_ID, category.getCategoryId());
          setResult(RESULT_OK, intent);
          finish();
        });
    mCategoriesView.setAdapter(mCategoryAdapter);
    ItemTouchHelper.Callback categoriesViewCallback =
        new CategoryListTouchHelperCallback((position) -> removeCategory(position));
    ItemTouchHelper categoriesViewTouchHelper = new ItemTouchHelper(categoriesViewCallback);
    categoriesViewTouchHelper.attachToRecyclerView(mCategoriesView);

    mCategoryGroups = mCategoryViewModel.getCategoryGroups();
    mCategoryGroups.observe(this, mCategoryGroupsObserver);

    selectCategoryGroup(TomatistPreferences.getInstance(this).lastUsedCategoryGroupId());
  }

  @Override
  public boolean onSupportNavigateUp() {
    setResult(RESULT_CANCELED);
    onBackPressed();
    return true;
  }

  private void selectCategoryGroup(long categoryGroupId) {
    mSelectedCategoryGroupId = categoryGroupId;
    if (mCategories != null) {
      mCategories.removeObserver(mCategoriesObserver);
    }
    mCategories = mCategoryViewModel.getCategories(mSelectedCategoryGroupId);
    mCategories.observe(CategoryActivity.this, mCategoriesObserver);
  }

  private void showAddCategoryGroupDialog() {
    final EditText newCategoryGroupTitleView = new EditText(this);

    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(getString(R.string.add_category_group));
    dialog.setView(newCategoryGroupTitleView);

    dialog.setPositiveButton(
        getString(R.string.action_add),
        (view, which) -> {
          String newCategoryGroupTitle = newCategoryGroupTitleView.getText().toString().trim();
          if (!newCategoryGroupTitle.isEmpty()) {
            CategoryGroup newCategoryGroup = new CategoryGroup();
            newCategoryGroup.setTitle(newCategoryGroupTitle);
            long newCategoryGroupId = mCategoryViewModel.insertCategoryGroup(newCategoryGroup);
            selectCategoryGroup(newCategoryGroupId);
          }
        });

    dialog.create().show();
  }

  private void removeCategoryGroup(int position) {
    if (mCategoryGroups != null) {
      List<CategoryGroup> categoryGroups = mCategoryGroups.getValue();
      if (categoryGroups != null) {
        CategoryGroup categoryGroup = categoryGroups.get(position);
        if (categoryGroup != null) {
          mCategoryViewModel.deleteCategoryGroup(categoryGroup);
          showCategoryGroupDeletedConfirmation(categoryGroup);
        }
      }
    }
  }

  private void showCategoryGroupDeletedConfirmation(CategoryGroup categoryGroup) {
    Snackbar.make(mRootView, R.string.category_group_deleted, Snackbar.LENGTH_SHORT)
        .setAction(R.string.undo, (view) -> mCategoryViewModel.insertCategoryGroup(categoryGroup))
        .show();
  }

  private void showAddCategoryDialog() {
    final EditText newCategoryTitleView = new EditText(this);

    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(getString(R.string.add_category_group));
    dialog.setView(newCategoryTitleView);

    dialog.setPositiveButton(
        getString(R.string.action_add),
        (view, which) -> {
          String newCategoryTitle = newCategoryTitleView.getText().toString().trim();
          if (!newCategoryTitle.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setTitle(newCategoryTitle);
            newCategory.setCategoryGroupId(mSelectedCategoryGroupId);
            mCategoryViewModel.insertCategory(newCategory);
          }
        });

    dialog.create().show();
  }

  private void removeCategory(int position) {
    if (mCategories != null) {
      List<Category> categories = mCategories.getValue();
      if (categories != null) {
        Category category = categories.get(position);
        if (category != null) {
          mCategoryViewModel.deleteCategory(category);
          showCategoryDeletedConfirmation(category);
        }
      }
    }
  }

  private void showCategoryDeletedConfirmation(Category category) {
    Snackbar.make(mRootView, R.string.category_deleted, Snackbar.LENGTH_SHORT)
        .setAction(R.string.undo, (view) -> mCategoryViewModel.insertCategory(category))
        .show();
  }
}
