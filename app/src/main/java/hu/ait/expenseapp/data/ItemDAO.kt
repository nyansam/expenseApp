package hu.ait.expenseapp.data

import androidx.room.*
import java.util.List

@Dao
interface ItemDAO {
    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>

    @Query("SELECT * FROM item WHERE category == :categoryName")
    fun getAllInCategory(categoryName: String?): List<Item>

    @Query("DELETE FROM item WHERE category == :categoryName")
    fun deleteAllInCategory(categoryName: String?)

    @Insert
    fun insertItem(item: Item) : Long

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Query("DELETE FROM item")
    fun deleteAll()
}