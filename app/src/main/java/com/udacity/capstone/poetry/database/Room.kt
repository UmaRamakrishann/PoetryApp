package com.udacity.capstone.poetry.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update


@Dao
interface PoetryDao {

    @Query("select * from databasepoem order by author asc")
    fun getPoems(): LiveData<List<DatabasePoem>>

    @Query("select * from databasepoem where isFavorite order by author asc")
    fun getFavoritePoems(): LiveData<List<DatabasePoem>>

    //The IGNORE strategy has been used on purpose so as to persist the favorites selected by the user.
    //Also a REPLACE is really not needed for these entries as a poem does not change once loaded.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg poems: DatabasePoem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(poem: DatabasePoem): Long

    @Update
    suspend fun update(poem: DatabasePoem)

    @Transaction
    suspend fun upsert(poem: DatabasePoem) {
        val id: Long = insert(poem)
        if (id == -1L) {
            update(poem)
        }
    }
}

@Database(entities = [DatabasePoem::class], version = 1)
abstract class PoetryDatabase : RoomDatabase() {
    abstract val poemDao: PoetryDao
}

private lateinit var INSTANCE: PoetryDatabase

fun getDatabase(context: Context): PoetryDatabase {
    synchronized(PoetryDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                PoetryDatabase::class.java,
                "poems"
            ).build()
        }
    }
    return INSTANCE
}