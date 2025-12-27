package com.example.pegapista.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.pegapista.R
import com.example.pegapista.ui.theme.BluePrimary
import com.example.pegapista.utils.MapUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun SnapshotMap(
    caminhoPercorrido: List<LatLng>,
    context: Context,
    onSnapshotPronto: (Uri) -> Unit
) {
    var snapshotTirado by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 4),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.NORMAL,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_clean)
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
            rotationGesturesEnabled = false,
            mapToolbarEnabled = false
        )
    ) {
        Polyline(
            points = caminhoPercorrido,
            color = BluePrimary,
            width = 7f,
            jointType = JointType.ROUND,
            startCap = RoundCap(),
            endCap = RoundCap()
        )

        MapEffect(caminhoPercorrido) { map ->
            if (!snapshotTirado) {
                val bounds = MapUtils.calcularEnquadramento(caminhoPercorrido)
                if (bounds != null) {
                    try {
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                        map.setOnMapLoadedCallback {
                            map.snapshot { bitmap ->
                                if (bitmap != null) {
                                    val uri = MapUtils.salvarBitmapTemporario(context, bitmap)
                                    snapshotTirado = true
                                    map.setOnMapLoadedCallback(null)
                                    onSnapshotPronto(uri)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}