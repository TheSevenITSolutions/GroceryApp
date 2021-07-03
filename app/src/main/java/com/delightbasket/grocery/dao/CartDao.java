package com.delightbasket.grocery.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void insertNew(CartOffline cartOffline);

    @Query("SELECT * FROM CartOffline")
    List<CartOffline> getall();

    @Query("SELECT * FROM CartOffline WHERE priceUnitId = :priceUnitId ")
    List<CartOffline> getCartProduct(String priceUnitId);

    @Query("UPDATE cartoffline SET quantity= :quantity WHERE priceUnitId=:priceunitid")
    void updateObj(long quantity, String priceunitid);


    @Query("DELETE FROM cartoffline  WHERE priceUnitId=:priceunitid")
    void deleteObjbyPid(String priceunitid);

    @Delete
    void deleteObj(CartOffline cartOffline);

    @Query("DELETE FROM CartOffline")
    void deleteAll();
    /*@Query("UPDATE CartOffline SET quantity = :quantity")
    void updetCart()*/

}
