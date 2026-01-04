package com.example.pegapista.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class LocationManager(context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    private var locationAnterior: Location? = null
    private var distanciaTotal: Float = 0f

    interface LocationListener {
        fun onLocationUpdate(velocidadeKmh: Double, distanciaMetros: Float, location: Location)
    }

    @SuppressLint("MissingPermission")
    fun startTracking(listener: LocationListener) {
        distanciaTotal = 0f
        locationAnterior = null

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateDistanceMeters(2f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {

                    if (locationAnterior == null) {
                        locationAnterior = location
                        continue
                    }

                    val anterior = locationAnterior!!
                    val distanciaDoPulo = anterior.distanceTo(location)
                    val isPontoValido = location.accuracy <= 20 && distanciaDoPulo >= 4

                    if (isPontoValido) {
                        distanciaTotal += distanciaDoPulo
                        locationAnterior = location

                        val velocidadeKmh = location.speed * 3.6
                        val velocidadeReal = if (velocidadeKmh > 0.5) velocidadeKmh else 0.0

                        listener.onLocationUpdate(velocidadeReal, distanciaTotal, location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    fun stopTracking() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
}