package com.example.raksha.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class EmergencyLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null
) {
    fun toMapsUrl(): String = "https://maps.google.com/?q=$latitude,$longitude"
    fun toCoordinatesString(): String = "Lat: %.4f, Lng: %.4f".format(latitude, longitude)
}

class LocationProvider(private val context: Context) {

    fun hasLocationPermission(): Boolean {
        val finePerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finePerm || coarsePerm
    }

    suspend fun getCurrentOrLastLocation(): EmergencyLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) return@withContext null

        try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                    ?: return@withContext null

            var bestLocation: Location? = null

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {
                    val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (gpsLoc != null) bestLocation = gpsLoc
                } catch (_: SecurityException) {}
            }

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try {
                    val netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (netLoc != null) {
                        if (bestLocation == null || netLoc.time > bestLocation.time) {
                            bestLocation = netLoc
                        }
                    }
                } catch (_: SecurityException) {}
            }

            bestLocation?.let { loc ->
                EmergencyLocation(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    accuracy = if (loc.hasAccuracy()) loc.accuracy else null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
