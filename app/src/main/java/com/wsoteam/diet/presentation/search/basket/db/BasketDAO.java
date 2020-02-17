package com.wsoteam.diet.presentation.search.basket.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;
import io.reactivex.Observable;

import java.util.List;

@Dao
public interface BasketDAO {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(BasketEntity food);

  @Query("select * from BasketEntity")
  List<BasketEntity> getAll();

  @Query("DELETE FROM basketentity WHERE serverId = :id AND deepId = :deepId")
  void deleteById(int id, int deepId);

  @Query("DELETE FROM basketentity WHERE id = :id")
  void deleteById(long id);

  @Delete
  void delete(BasketEntity food);

  @Query("DELETE FROM BasketEntity")
  void deleteAll();

  @Query("SELECT * FROM BasketEntity WHERE serverId = :serverId AND deepId = :deepId AND eatingType = :eatingType")
  Maybe<BasketEntity> getSameEntity(int serverId, int deepId, int eatingType);

}
