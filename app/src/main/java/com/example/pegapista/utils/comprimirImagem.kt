import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

fun comprimirImagem(context: Context, imageUri: Uri): ByteArray {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)


    val larguraMaxima = 1080
    val alturaProporcional = (originalBitmap.height * larguraMaxima) / originalBitmap.width

    val bitmapRedimensionado = Bitmap.createScaledBitmap(
        originalBitmap,
        larguraMaxima,
        alturaProporcional,
        true
    )

    val baos = ByteArrayOutputStream()
    bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 70, baos)

    return baos.toByteArray()
}