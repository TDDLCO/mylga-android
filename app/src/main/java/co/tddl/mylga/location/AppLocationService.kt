package co.tddl.mylga.location

import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.location.Location



class AppLocationService {

   /* private var locationManager: LocationManager? = null
    var location: Location

    private val MIN_DISTANCE_FOR_UPDATE: Long = 10
    private val MIN_TIME_FOR_UPDATE = (1000 * 60 * 2).toLong()

    fun AppLocationService(context: Context){
        locationManager = context.getSystemService(LOCATION_SERVICE)
    }

    fun getLocation(provider: String): Location? {
        if (locationManager!!.isProviderEnabled(provider)) {
            locationManager!!.requestLocationUpdates(
                provider,
                MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this
            )
            if (locationManager != null) {
                location = locationManager!!.getLastKnownLocation(provider)
                return location
            }
        }
        return null
    }

    fun onLocationChanged(location: Location) {}

    fun onProviderDisabled(provider: String) {}

    fun onProviderEnabled(provider: String) {}

    fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    fun onBind(arg0: Intent): IBinder? {
        return null
    }*/
}