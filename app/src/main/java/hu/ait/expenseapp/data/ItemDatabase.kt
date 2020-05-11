package hu.ait.expenseapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.ait.expenseapp.R


@Database(entities = arrayOf(Item::class), version = 2)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDAO

    companion object {
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ItemDatabase::class.java, context.getString(R.string.item)
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}