package co.tddl.mylga.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(val context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(SharedPreferenceContract.PREF_APP_NAME, Context.MODE_PRIVATE)

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
    fun setLastLocation(location: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(SharedPreferenceContract.PREF_LAST_LOCATION, location)
        editor.apply()
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