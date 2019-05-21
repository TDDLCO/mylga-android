package co.tddl.mylga.util

import android.content.Context
import android.content.SharedPreferences
import java.util.*
import java.util.concurrent.TimeUnit


class SharedPreferenceHelper(val context: Context?) {

    private val sharedPref: SharedPreferences = context!!.getSharedPreferences(SharedPreferenceContract.PREF_APP_NAME, Context.MODE_PRIVATE)

    /**
     * Checks if user has installed the application before.
     * @return
     */
    fun isFirstInstallation(): Boolean {
        return ! (sharedPref.contains(SharedPreferenceContract.PREF_INSTALLED)
                && sharedPref.getBoolean(SharedPreferenceContract.PREF_INSTALLED, false))
    }

    /**
     * Sets the value of installed to true.
     */
    fun setFirstInstall() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(SharedPreferenceContract.PREF_INSTALLED, true)
        editor.apply()
    }

    /**
     * Checks if user has set their username already.
     * @return
     */
    fun hasSetUserName(): Boolean {
        return sharedPref.contains(SharedPreferenceContract.PREF_USER_NAME)
    }

    /**
     * Sets the username.
     */
    fun setUserName(userName: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(SharedPreferenceContract.PREF_USER_NAME, userName)
        editor.apply()
    }

    /**
     * Sets true for location granted preference
     */
    fun setLastLocation(location: String?, latitude: String, longitude: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(SharedPreferenceContract.PREF_LAST_LOCATION, location)
        editor.putString(SharedPreferenceContract.PREF_LAST_LOCATION_LAT, latitude)
        editor.putString(SharedPreferenceContract.PREF_LAST_LOCATION_LONG, longitude)
        setLastLocationUpdatedAt(editor)
        editor.apply()
    }

    /**
     * Gets the value of last location in preference file
     */
    fun getLastLocation(): String?{
        if(sharedPref.contains(SharedPreferenceContract.PREF_LAST_LOCATION)){
            return sharedPref.getString(SharedPreferenceContract.PREF_LAST_LOCATION, null)
        }
        return null
    }

    /**
     * Gets the latitude of last location in preference file
     */
    fun getLatitude(): String?{
        if(sharedPref.contains(SharedPreferenceContract.PREF_LAST_LOCATION_LAT)){
            return sharedPref.getString(SharedPreferenceContract.PREF_LAST_LOCATION_LAT, null)
        }
        return null
    }

    /**
     * Gets the longitude of last location in preference file
     */
    fun getLongitude(): String?{
        if(sharedPref.contains(SharedPreferenceContract.PREF_LAST_LOCATION_LONG)){
            return sharedPref.getString(SharedPreferenceContract.PREF_LAST_LOCATION_LONG, null)
        }
        return null
    }

    /**
     * Sets true for location granted preference
     */
    private fun setLastLocationUpdatedAt(editor: SharedPreferences.Editor){
        val date = Date(System.currentTimeMillis())
        editor.putLong(SharedPreferenceContract.PREF_LAST_LOCATION_UPDATED_AT, date.time)
    }


    fun lastLocationUpdatedOverOneDay(): Boolean{

        if(sharedPref.contains(SharedPreferenceContract.PREF_LAST_LOCATION_UPDATED_AT)){
            val dateTime = sharedPref.getLong(SharedPreferenceContract.PREF_LAST_LOCATION_UPDATED_AT, 0)

            val today = Date(System.currentTimeMillis()).time

            val timeDifference = Math.abs(TimeUnit.MILLISECONDS.toDays(dateTime - today))

            return timeDifference > 1
        }
        return true
    }

    /**
     * Unsets the username.
     */
    fun unsetUserName() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(SharedPreferenceContract.PREF_USER_NAME)
        editor.apply()
    }

}