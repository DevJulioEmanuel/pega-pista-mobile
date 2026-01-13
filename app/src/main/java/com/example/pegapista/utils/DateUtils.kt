package com.example.pegapista.utils

import java.util.Calendar

object DateUtils {

    fun isSequenciaAtiva(ultimaAtividadeTimestamp: Long): Boolean {
        if (ultimaAtividadeTimestamp == 0L) return false

        val agora = System.currentTimeMillis()
        if (isMesmoDia(agora, ultimaAtividadeTimestamp)) return true

        if (isOntem(ultimaAtividadeTimestamp)) return true

        return false
    }

    fun isMesmoDia(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isOntem(ultimoTimestamp: Long): Boolean {
        val agora = Calendar.getInstance()
        val ontem = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val dataUltima = Calendar.getInstance().apply {
            timeInMillis = ultimoTimestamp
        }

        return ontem.get(Calendar.YEAR) == dataUltima.get(Calendar.YEAR) &&
                ontem.get(Calendar.DAY_OF_YEAR) == dataUltima.get(Calendar.DAY_OF_YEAR)
    }
}
