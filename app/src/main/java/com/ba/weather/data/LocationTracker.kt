package com.ba.weather.data

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager = context.getSystemService(LocationManager::class.java)
    val isLocationEnabled: Boolean
        get() = LocationManagerCompat.isLocationEnabled(locationManager)

    suspend fun getLocation(): Location? {
        return try {
            suspendCancellableCoroutine { continuation ->
                client.lastLocation.addOnSuccessListener {
                    continuation.resume(it)
                }.addOnCanceledListener {
                    continuation.cancel()
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }
    }
}
