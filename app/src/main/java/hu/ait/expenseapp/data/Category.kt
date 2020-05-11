package hu.ait.expenseapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) var categoryId : Long?,
    @ColumnInfo(name = "categoryName") var categoryName : String,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "numItems") var numItems : Int,
    @ColumnInfo(name = "totalAmount") var totalAmount : Int


) : Serializable