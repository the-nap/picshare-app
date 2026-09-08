package com.picshare.app.context_awareness.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@SuppressLint("MissingPermission")
object LocationManager {

  suspend fun fetchLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient
  ): String? {
    return try {
      val location = fusedLocationClient.lastLocation.await()
      Log.d("LocationManager", location.toString())

      val resolvedLocation = location ?: handleNewLocation(fusedLocationClient)
      resolvedLocation?.let { getAddressFromLocation(context, it) } ?: "Location not found"
    } catch (e: Exception) {
      "Error fetching location: ${e.message}"
    }
  }

  private suspend fun handleNewLocation(
    fusedLocationClient: FusedLocationProviderClient
  ): Location? {
    return fusedLocationClient.getCurrentLocation(
      Priority.PRIORITY_HIGH_ACCURACY,
      CancellationTokenSource().token
    ).await()
  }

  private suspend fun getAddressFromLocation(
    context: Context,
    location: Location
  ): String? = withContext(Dispatchers.IO) {
    val geocoder = Geocoder(context, Locale.getDefault())
    val latitude = location.latitude
    val longitude = location.longitude

    Log.d("LocationManager", "latitude: $latitude, longitude: $longitude")

    try {
      val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
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
