package com.picshare.app.context_awareness.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("MissingPermission")
object LocationManager {

  suspend fun fetchLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient
  ): String? {
    return try {
        val location = fusedLocationClient.lastLocation.await()
          ?: handleNewLocation(fusedLocationClient)
          ?: fetchLocationFallback(context)

        Log.d("LocationManager", "location: $location")

        location?.let { getAddressFromLocation(context, it) } ?: "Location not found"
    } catch (e: Exception) {
        Log.e("LocationManager", "fetchLocation failed", e)
        "Error fetching location: ${e.message}"
    }
  }

  suspend fun fetchLocationFallback(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    val provider = when {
        locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ->
            android.location.LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER) ->
            android.location.LocationManager.NETWORK_PROVIDER
        else -> return null
    }

    locationManager.getLastKnownLocation(provider)?.let { return it }

    return withTimeoutOrNull(8_000.milliseconds) {
        suspendCancellableCoroutine<Location?> { cont ->
            lateinit var listener: android.location.LocationListener

            listener = android.location.LocationListener { location ->
                locationManager.removeUpdates(listener)
                if (cont.isActive) cont.resume(location)
            }

            cont.invokeOnCancellation {
                locationManager.removeUpdates(listener)
            }

            locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
        }
    }
}

  private suspend fun handleNewLocation(
    fusedLocationClient: FusedLocationProviderClient
  ): Location? {
    return withTimeoutOrNull(8_000.milliseconds) {
      fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
      ).await()
    }
  }

  private suspend fun getAddressFromLocation(
    context: Context,
    location: Location
  ): String? = withContext(Dispatchers.IO) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val latitude = location.latitude
    val longitude = location.longitude

    val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
    Log.d("LocationManager", addresses.toString())

    Log.d("LocationManager", "latitude: $latitude, longitude: $longitude")

    try {
      val addresses = geocoder.getFromLocation(latitude, longitude, 1)
      Log.d("LocationManager", "addresses=$addresses, line0=${addresses?.firstOrNull()?.getAddressLine(0)}")
      if (!addresses.isNullOrEmpty()) {
        addresses[0].getAddressLine(0)
      } else {
        "No address"
      }
    } catch (e: Exception) {
      e.printStackTrace()
      "Error: ${e.message}"
    }
  }
}
