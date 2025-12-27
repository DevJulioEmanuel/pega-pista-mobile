package com.example.pegapista.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.io.File
import java.io.FileOutputStream

object MapUtils {
    fun calcularEnquadramento(pontos: List<LatLng>): LatLngBounds? {
        if (pontos.isEmpty()) return null

        val builder = LatLngBounds.Builder()
        for (ponto in pontos) {
            builder.include(ponto)
        }
        return builder.build()
    }

    fun salvarBitmapTemporario(context: Context, bitmap: Bitmap): Uri {
        val arquivo = File(context.cacheDir, "snapshot_mapa_${System.currentTimeMillis()}.jpg")
        val stream = FileOutputStream(arquivo)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
        return Uri.fromFile(arquivo)
    }
}