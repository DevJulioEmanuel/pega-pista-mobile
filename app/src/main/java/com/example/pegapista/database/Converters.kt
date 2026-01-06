package com.example.pegapista.database

import androidx.room.TypeConverter
import com.example.pegapista.data.models.Corrida
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCorrida(corrida: Corrida): String {
        return gson.toJson(corrida)
    }

    @TypeConverter
    fun toCorrida(corridaString: String): Corrida {
        return gson.fromJson(corridaString, Corrida::class.java)
    }
}