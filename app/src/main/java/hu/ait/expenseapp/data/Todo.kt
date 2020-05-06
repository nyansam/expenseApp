package hu.ait.expenseapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true) var todoId : Long?,
    @ColumnInfo(name = "done") var done: Boolean,
    @ColumnInfo(name = "todoText") var todoText: String,
    @ColumnInfo(name = "category") var category: Int,
    @ColumnInfo(name = "todoPrice") var todoPrice : String,
    @ColumnInfo(name = "todoDesc") var todoDesc : String


) : Serializable