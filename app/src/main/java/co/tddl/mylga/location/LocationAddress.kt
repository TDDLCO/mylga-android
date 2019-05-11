package co.tddl.mylga.location

import android.content.Context
import android.os.Bundle
import android.location.Geocoder
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.*


class LocationAddress {
    private val TAG = "LocationAddress"

    fun getAddressFromLocation(
        latitude: Double, longitude: Double,
        context: Context, hankdler: Handler?
    ): String? {
        //val thread = object : Thread() {
            //override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocation(
                        latitude, longitude, 1
                    )
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()
                        for (i in 0 until address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        sb.append(address.locality).append("\n")
                        sb.append(address.postalCode).append("\n")
                        sb.append(address.countryName).append("\n")
                        sb.append(address.adminArea)
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable connect to Geocoder", e)
                } finally {
                   // val message = Message.obtain()
                    // message.setTarget(handler)
                    if (result != null) {
                        //message.what = 1
                        val bundle = Bundle()
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n\nAddress:\n" + result
                        Log.v("Addresss", result)
                        return result
                        //bundle.putString("address", result)
                        //message.setData(bundle)
                    } else {
                        //message.what = 1
                        val bundle = Bundle()
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long."

                        Log.v("Addresss", result)
                        return result
                        //bundle.putString("address", result)
                        //message.setData(bundle)
                    }
                    //message.sendToTarget()
                }
            //}
        //}
        //thread.start()
        return null
    }
}