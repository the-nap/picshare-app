package com.picshare.app.context_awareness.location

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.time.Duration.Companion.seconds

@SuppressLint("MissingPermission")
object LocationManager {

  suspend fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient
  ): String? {
    return try {
      val location = fusedLocationClient.lastLocation.await()

      val resolvedLocation = location ?: handleNewLocation(fusedLocationClient)
      resolvedLocation?.let { getAddressFromLocation(it) } ?: "Location not found"
    } catch (e: Exception) {
      "Error fetching location: ${e.message}"
    }
  }

  private suspend fun handleNewLocation(
    fusedLocationClient: FusedLocationProviderClient
  ): Location? {
    return withTimeoutOrNull(15.seconds) {
      fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
      ).await()
    }
  }

  private suspend fun getAddressFromLocation(
    location: Location
  ): String = withContext(Dispatchers.IO) {
    try {
      val url = "https://nominatim.openstreetmap.org/reverse" +
          "?lat=${location.latitude}&lon=${location.longitude}&format=json"

      val connection = URL(url).openConnection() as HttpURLConnection
      connection.setRequestProperty("User-Agent", "PicShareApp/1.0")
      connection.connectTimeout = 8_000
      connection.readTimeout = 8_000

      val response = connection.inputStream.bufferedReader().readText()
      val json = JSONObject(response)
      val addressObj = json.optJSONObject("address")

      val locality = addressObj?.optString("village")?.ifBlank { null }
        ?: addressObj?.optString("town")?.ifBlank { null }
        ?: addressObj?.optString("city")?.ifBlank { null }
        ?: addressObj?.optString("county")?.ifBlank { null }
      val country = addressObj?.optString("country")?.ifBlank { null }
      listOfNotNull(locality, country).joinToString(" ").ifBlank {""}
    } catch (e: Exception) {
      Log.e("LocationManager", "Nominatim fallback failed", e)
      "Error: ${e.message}"
    }
  }
}