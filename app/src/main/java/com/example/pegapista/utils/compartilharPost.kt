import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.pegapista.data.models.Postagem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

fun compartilharPost(context: Context, post: Postagem) {
    val urlImagem = post.urlsFotos.firstOrNull()

    val textoLegenda = """
        🏃‍♂️ *Corrida no PegaPista!*
        
        👤 ${post.autorNome}
        📏 Distância: ${"%.2f".format(post.corrida.distanciaKm)} km
        ⏱️ Tempo: ${post.corrida.tempo}
        ⚡ Pace: ${post.corrida.pace}
        
        ${post.titulo}
        ${post.descricao}
    """.trimIndent()

    if (urlImagem == null) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoLegenda)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartilhar..."))
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(urlImagem)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)

            if (result is SuccessResult) {
                val bitmap = (result.drawable as BitmapDrawable).bitmap

                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()

                val stream = FileOutputStream("$cachePath/share_image.jpg")
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                stream.close()

                val imagePath = File(context.cacheDir, "images")
                val newFile = File(imagePath, "share_image.jpg")
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    newFile
                )

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textoLegenda)
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/jpeg"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via..."))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textoLegenda)
                    type = "text/plain"
                }
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Compartilhar (erro na img)..."
                    )
                )
            }
        }
    }
}