package com.brickgit.tomatist.data;

import com.brickgit.tomatist.data.database.Action;
import com.brickgit.tomatist.data.database.ActionDao;
import com.brickgit.tomatist.data.database.Category;
import com.brickgit.tomatist.data.database.CategoryDao;
import com.brickgit.tomatist.data.database.CategoryGroup;
import com.brickgit.tomatist.data.database.CategoryGroupDao;
import com.brickgit.tomatist.data.database.Database;
import com.brickgit.tomatist.data.database.DatabaseLoader;

import java.util.List;

import androidx.lifecycle.LiveData;

/** Created by Daniel Lin on 2019/1/7. */
public class DataRepository {

  private static volatile DataRepository INSTANCE;

  private Database mDatabase;
  private ActionDao mActionDao;
  private CategoryGroupDao mCategoryGroupDao;
  private CategoryDao mCategoryDao;

  public static DataRepository getInstance() {
    if (INSTANCE == null) {
      synchronized (DataRepository.class) {
        if (INSTANCE == null) INSTANCE = new DataRepository();
      }
    }
    return INSTANCE;
  }

  private DataRepository() {
    mDatabase = DatabaseLoader.getAppDatabase();
    mActionDao = mDatabase.actionDao();
    mCategoryGroupDao = mDatabase.categoryGroupDao();
    mCategoryDao = mDatabase.categoryDao();
  }

  public void insertAction(Action action) {
    mActionDao.insertAction(action);
  }

  public void updateAction(Action action) {
    mActionDao.updateAction(action);
  }

  public void deleteAction(Action action) {
    mActionDao.deleteAction(action);
  }

  public LiveData<Action> getAction(String id) {
    return mActionDao.getAction(id);
  }

  public LiveData<List<Action>> getFinishedActions(int year, int month, int day) {
    return mActionDao.getFinishedActionsForDate(year, month, day);
  }

  public LiveData<List<Action>> getUnfinishedActions() {
    return mActionDao.getUnfinishedActions();
  }

  public LiveData<List<Action>> getUnfinishedActions(String categoryId) {
    return mActionDao.getUnfinishedActions(categoryId);
  }

  public void insertCategoryGroup(CategoryGroup categoryGroup) {
    mCategoryGroupDao.insertCategoryGroup(categoryGroup);
  }

  public LiveData<CategoryGroup> getCategoryGroup(String categoryGroupId) {
    return mCategoryGroupDao.getCategoryGroup(categoryGroupId);
  }

  public LiveData<List<CategoryGroup>> getCategoryGroups() {
    return mCategoryGroupDao.getCategoryGroups();
  }

  public void deleteCategoryGroup(CategoryGroup categoryGroup) {
    mCategoryGroupDao.deleteCategoryGroup(categoryGroup);
  }

  public void insertCategory(Category category) {
    mCategoryDao.insertCategory(category);
  }

  public void insertCategories(List<Category> categories) {
    mCategoryDao.insertCategories(categories);
  }

  public LiveData<Category> getCategory(String categoryId) {
    return mCategoryDao.getCategory(categoryId);
  }

  public LiveData<List<Category>> getCategories(String categoryGroupId) {
    return mCategoryDao.getCategories(categoryGroupId);
  }

  public LiveData<List<Category>> getCategories() {
    return mCategoryDao.getCategories();
  }

  public void deleteCategory(Category category) {
    mCategoryDao.deleteCategory(category);
  }
}
