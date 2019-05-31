package co.tddl.mylga.location

import android.content.Context
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*


class GeocoderHelper {
    private val TAG = "GeocoderHelper"

    fun getAddressFromLocation(latitude: Double, longitude: Double, context: Context): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var result: String? = null
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null && addressList.size > 0) {
                val address = addressList[0]
                result = address.locality
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable connect to Geocoder", e)
        } finally {

            if(result != null){
                return result
            }

            return "Unable to get address for this lat-long."

        }
    }
}