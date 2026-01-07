package com.example.pegapista.utils


import android.content.Context

import android.net.Uri

import java.io.File

import java.io.FileOutputStream

import java.util.UUID



fun copiarImagemParaCache(context: Context, uri: Uri): String? {

    return try {

        val contentResolver = context.contentResolver


        val nomeArquivo = "img_post_${UUID.randomUUID()}.jpg"

        val arquivoDestino = File(context.cacheDir, nomeArquivo)


        val inputStream = contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(arquivoDestino)


        inputStream?.use { input ->

            outputStream.use { output ->

                input.copyTo(output)

            }

        }

        arquivoDestino.absolutePath

    } catch (e: Exception) {

        e.printStackTrace()

        null

    }
}