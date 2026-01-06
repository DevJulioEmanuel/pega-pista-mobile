package com.example.pegapista.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pegapista.database.AppDatabase
import com.example.pegapista.database.dao.CorridaDao
import com.example.pegapista.database.entities.CorridaEntity

@Database(entities = [CorridaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun corridaDao(): CorridaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pegapista_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}