package hu.ait.expenseapp.data

import androidx.room.*

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM category")
    fun getAllCategories(): List<Category>

    @Insert
    fun insertCategory(category: Category) : Long

    @Delete
    fun deleteCategory(category: Category)

    @Update
    fun updateCategory(category: Category)

    @Query("DELETE FROM category")
    fun deleteAll()

}