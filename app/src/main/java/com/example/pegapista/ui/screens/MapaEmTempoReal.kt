package com.example.pegapista.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.R
import com.example.pegapista.service.RunningState
import com.example.pegapista.ui.theme.BluePrimary
import com.example.pegapista.ui.viewmodels.CorridaViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapaEmTempoReal(
    viewModel: CorridaViewModel = viewModel(),
    ) {
    val context = LocalContext.current
    val caminhoPercorrido by RunningState.percurso.collectAsState()
    val pontoInicial by viewModel.localizacaoInicial.collectAsState()

    val cameraPositionState = rememberCameraPositionState()
    var cameraJaAjustada by remember { mutableStateOf(false) }

    val temPermissao = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //Para centralziar a tela
    LaunchedEffect(pontoInicial) {
        if (!cameraJaAjustada && caminhoPercorrido.isEmpty()) {
            pontoInicial?.let { latLng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                cameraJaAjustada = true
            }
        }
    }

    LaunchedEffect(caminhoPercorrido) {
        if (caminhoPercorrido.isNotEmpty()) {
            val ultimoPonto = caminhoPercorrido.last()

            if (!cameraJaAjustada) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(ultimoPonto, 17f)
                cameraJaAjustada = true            } else {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLng(ultimoPonto),
                    1000
                )
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = temPermissao,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_clean)
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = true,
            compassEnabled = true
        )
    ) {
        if (caminhoPercorrido.isNotEmpty()) {
            Polyline(
                points = caminhoPercorrido,
                color = BluePrimary,
                width = 8f,
                jointType = JointType.ROUND,
                startCap = RoundCap(),
                endCap = RoundCap()
            )
        }
    }
}