package hu.ait.expenseapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item")

data class Item (
    @PrimaryKey(autoGenerate = true) var itemId : Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "category") var category: String

) : Serializable
