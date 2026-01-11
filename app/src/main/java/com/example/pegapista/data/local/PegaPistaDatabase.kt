package com.example.pegapista.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pegapista.data.local.dao.CorridaDao
import com.example.pegapista.data.local.dao.PostagemDao
import com.example.pegapista.data.local.dao.UserDao
import com.example.pegapista.data.local.entities.CorridaEntity
import com.example.pegapista.data.local.entities.PostagemEntity
import com.example.pegapista.data.local.entities.UserEntity


@Database(
    entities = [CorridaEntity::class, PostagemEntity::class, UserEntity::class],
    version = 4
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun corridaDao(): CorridaDao
    abstract fun userDao(): UserDao
    abstract fun postagemDao(): PostagemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pegapista_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}